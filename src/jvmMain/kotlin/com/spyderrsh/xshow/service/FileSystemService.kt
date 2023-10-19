package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.filesystem.FilesystemRepository

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class FileSystemService(
    private val filesystemRepository: FilesystemRepository
): IFileSystemService {
    override suspend fun getRootFolder(): FileModel.Folder {
        return filesystemRepository.getRootFolder()
    }

    override suspend fun getFolderContents(folder: FileModel.Folder): List<FileModel> {
        return filesystemRepository.getFolderContents(folder)
    }

    override suspend fun deleteFile(file: FileModel): Boolean {
        TODO("Not yet implemented")
    }
}