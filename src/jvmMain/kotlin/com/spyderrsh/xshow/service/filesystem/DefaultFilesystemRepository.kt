package com.spyderrsh.xshow.service.filesystem

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.DefaultFileModelUtil
import com.spyderrsh.xshow.util.toFile
import kotlinx.coroutines.delay
import java.io.File
import java.nio.file.InvalidPathException
import java.nio.file.Paths
import java.util.logging.Logger


class DefaultFilesystemRepository(
    private val serverConfig: ServerConfig,
    private val fileUtil: DefaultFileModelUtil
) : FilesystemRepository {
    override fun getRootFolder(): FileModel.Folder {
        Logger.getGlobal().info("Root file requested")
        return fileUtil.createFolderFromPath(serverConfig.rootFolderPath).also {
            Logger.getGlobal().info("Root file returning: $it")
        }
    }

    override fun getFolderContents(folder: FileModel.Folder): List<FileModel> {
        runCatching {
            fileUtil.getFolderContents(folder)
        }.onFailure {
            Logger.getLogger(this::class.simpleName)
                .throwing(this::class.simpleName, ::getFolderContents.name, it)
        }.onSuccess {
            return it
        }
        return emptyList()

    }

    override suspend fun deleteFile(file: FileModel) {
        deleteFileWithRetries(file)
    }

    private suspend fun deleteFileWithRetries(file: FileModel, attempt: Int = 0, maxAttempts: Int = 10): Result<Unit> {
        return runCatching {
            deleteFileInternal(file)
        }.recoverCatching {
            Logger.getGlobal().info("Retry ${attempt + 1} deleting \"${file.shortName}\"")
            // TODO -- Make this error less generic and match the move problem
            if (attempt < maxAttempts && it is IllegalStateException) {
                delay(200L * Math.pow(2.0, attempt.toDouble()).toLong())
                return deleteFileWithRetries(file, attempt + 1, maxAttempts)
            } else {
                throw it
            }
        }
    }

    private fun deleteFileInternal(file: FileModel) {


        val fileToDelete = file.toFile() // This will handle both Video and clips

        if (!fileToDelete.exists()) {
            throw IllegalArgumentException("Cannot delete file that does not exist")
        }

        val rootDir = File(serverConfig.rootFolderPath).absoluteFile
        if (!fileToDelete.startsWith(rootDir)) {
            throw IllegalArgumentException("Cannot delete file that is not in the root directory")
        }
        val trashDir = File(serverConfig.trashPath)

        val trashLocationUnchecked = trashDir.resolve(fileToDelete.relativeTo(rootDir))
        val trashLocationPath = removeInvalidChars(trashLocationUnchecked.path)
        val trashLocation = File(trashLocationPath)
        if (trashLocationUnchecked.absolutePath != trashLocation.absolutePath) {
            Logger.getGlobal()
                .info("Scrubbed Trash Path from ${trashLocationUnchecked.absolutePath} to ${trashLocation.absolutePath}")
        }
        trashLocation.parentFile.mkdirs()

        if (!fileToDelete.renameTo(File(trashLocation.absolutePath))) {
            throw IllegalStateException("Error moving ${fileToDelete.absolutePath} to ${trashLocation.absolutePath}")
        } else {
            Logger.getGlobal().info("Successfully moved file to ${trashLocation.absolutePath}")
        }
    }

    override fun doesItemExist(item: FileModel): Boolean {
        return File(item.path).exists()
    }
}

fun removeInvalidChars(fileName: String): String {
    try {
        Paths.get(fileName)
        return fileName
    } catch (e: InvalidPathException) {
        if (e.input != null && e.input.length > 0 && e.index >= 0) {
            val stringBuilder = StringBuilder(e.input)
            stringBuilder.replace(e.index, e.index + 1, "_")
            return removeInvalidChars(stringBuilder.toString())
        }
        throw e
    }
}