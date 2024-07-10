package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.FileModel

//import io.kvision.remote.getService

object Model {

    private val fileSystemService = AppComponent.filesystemService

    suspend fun getRootFolder(): FileModel.Folder = fileSystemService.getRootFolder()
    suspend fun getFiles(folder: FileModel.Folder) = fileSystemService.getFolderContents(folder).sortedBy { it.shortName.lowercase() }

}
