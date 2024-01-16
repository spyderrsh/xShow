package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel

interface IFileSystemService {

    suspend fun getRootFolder(): FileModel.Folder
    suspend fun getFolderContents(folder: FileModel.Folder): List<FileModel>
    suspend fun deleteFile(file: FileModel): Boolean

    companion object {
        const val GET_ROOT_FOLDER_PATH = "/getRootFolder"
        const val GET_FOLDER_CONTENTS_PATH = "/getFolderContents"
    }
}