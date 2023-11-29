package com.spyderrsh.xshow.service.filesystem

import com.spyderrsh.xshow.model.FileModel

interface FilesystemRepository {
    fun getRootFolder(): FileModel.Folder
    fun getFolderContents(folder: FileModel.Folder): List<FileModel>
    suspend fun deleteFile(file: FileModel)
}