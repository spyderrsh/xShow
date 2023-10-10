package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel
import io.kvision.annotations.KVService

@KVService
interface IFileSystemService {

    suspend fun getRootFolder(): FileModel.Folder
    suspend fun getFolderContents(folder: FileModel.Folder): List<FileModel>
    suspend fun deleteFile(file: FileModel): Boolean
}