package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class FileSystemService: IFileSystemService {
    override suspend fun getRootFolder(): FileModel.Folder {
        TODO("Not yet implemented")
    }

    override suspend fun getFolderContents(folder: FileModel.Folder): List<FileModel> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(file: FileModel): Boolean {
        TODO("Not yet implemented")
    }
}