package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.IFileSystemService
import com.spyderrsh.xshow.service.ISlideshowService
import io.kvision.remote.getService

object Model {

    private val pingService = getService<IPingService>()
    private val fileSystemService = getService<IFileSystemService>()
    private val slideshowService = getService<ISlideshowService>()

    suspend fun ping(message: String): String {
        return pingService.ping(message)
    }

    suspend fun getRootFolder(): FileModel.Folder = fileSystemService.getRootFolder()
    suspend fun getFiles(folder: FileModel.Folder) = fileSystemService.getFolderContents(folder).sortedBy { it.shortName.lowercase() }

}
