package com.spyderrsh.xshow.service.filesystem

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.DefaultFileModelUtil
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
}