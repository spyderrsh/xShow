package com.spyderrsh.xshow.service.filesystem

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.DefaultFileModelUtil
import com.spyderrsh.xshow.util.toFile
import java.util.logging.Logger

class DefaultFilesystemRepository(
    private val serverConfig: ServerConfig,
    private val fileUtil: DefaultFileModelUtil
) : FilesystemRepository {
    override fun getRootFolder(): FileModel.Folder {
        return fileUtil.createFolderFromPath(serverConfig.rootFolderPath)
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