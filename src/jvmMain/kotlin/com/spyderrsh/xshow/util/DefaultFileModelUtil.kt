package com.spyderrsh.xshow.util

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.model.FileModel
import io.ktor.http.*
import java.io.File

class DefaultFileModelUtil(serverConfig: ServerConfig) : XShowFileModelUtil {

    private val rootFile = File(serverConfig.rootFolderPath).absoluteFile

    override fun createFolderFromPath(path: String, parentFolder: FileModel.Folder?): FileModel.Folder {
        return createFolderFromFile(File(path).absoluteFile, parentFolder)
    }

    override fun createFolderFromFile(file: File, parentFolder: FileModel.Folder?): FileModel.Folder {
        return FileModel.Folder(
            path = file.path,
            shortName = if (parentFolder == null) "root" else file.name,
            serverPath = file.getServerPath(),
            parentFolder = parentFolder
        )
    }

    override fun createImageFromFile(file: File): FileModel.Media.Image {
        return FileModel.Media.Image(
            file.absolutePath,
            serverPath = file.getServerPath(),
            shortName = file.nameWithoutExtension,
            extension = file.extension.lowercase()
        )
    }

    override fun createVideoFromFile(file: File): FileModel.Media.Video {
        return FileModel.Media.Video.Full(
            file.absolutePath,
            serverPath = file.getServerPath(),
            shortName = file.nameWithoutExtension,
            extension = file.extension.lowercase(),
            duration = null
        )
    }

    override val ImageExtensions = listOf("jpg", "jpeg", "png")
    override val VideoExtensions = listOf("mp4", "mov", "avi", "mpg", "mpeg", "webm", "gif", "gifv")
    override val UnusedExtensions = mutableSetOf<String>()


    override fun getFolderContents(folder: FileModel.Folder): List<FileModel> {
        val file = folder.toFile()
        if (!file.startsWith(rootFile)) {
            throw IllegalArgumentException("File is not in root directory: ${folder.path}")
        }
        if (!file.isDirectory) {
            throw IllegalArgumentException("Tried to open ${folder.path} as directory, but it is not a directory.")
        }
        return file.listFiles()?.mapNotNull {
            when {
                it.isDirectory -> createFolderFromPath(it.absolutePath, folder)
                it.extension.lowercase() in ImageExtensions -> createImageFromFile(it)
                it.extension.lowercase() in VideoExtensions -> createVideoFromFile(it)
                else -> {
                    UnusedExtensions.add(it.extension.lowercase())
                    null
                }
            }
        }.orEmpty()
    }

    private fun File.getServerPath(): String {
        return this.relativeTo(rootFile).path.encodeURLPath()
    }
}


fun FileModel.toFile() = File(path).absoluteFile
