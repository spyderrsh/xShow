package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.IFileSystemService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class FileSystemService(private val client: HttpClient, private val serverBaseUrl: String) : IFileSystemService {
    override suspend fun getRootFolder(): FileModel.Folder {
        return client.get("$serverBaseUrl${IFileSystemService.GET_ROOT_FOLDER_PATH}").body()
    }

    override suspend fun getFolderContents(folder: FileModel.Folder): List<FileModel> {
        return client.post("$serverBaseUrl${IFileSystemService.GET_FOLDER_CONTENTS_PATH}") { setBody(folder) }.body()
    }

    override suspend fun deleteFile(file: FileModel): Boolean {
        return false
    }

}
