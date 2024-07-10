package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.IFileSystemService.Companion.GET_FOLDER_CONTENTS_PATH
import com.spyderrsh.xshow.service.IFileSystemService.Companion.GET_ROOT_FOLDER_PATH
import com.spyderrsh.xshow.service.filesystem.FilesystemRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Suppress("ACTUAL_WITHOUT_EXPECT")
class FileSystemService(
    private val filesystemRepository: FilesystemRepository
) : IFileSystemService {
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

fun FileSystemService.setupRouting(routing: Routing) {
    routing.get(GET_ROOT_FOLDER_PATH) {
        call.respond(getRootFolder())
    }
    routing.post(GET_FOLDER_CONTENTS_PATH) {
        runCatching {
            call.receive<FileModel.Folder>()
        }.onFailure { call.respond(HttpStatusCode.BadRequest) }
            .onSuccess { folder ->
                runCatching {
                    call.respond(getFolderContents(folder))
                }.onFailure {
                    logError(call, it)
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
    }
}