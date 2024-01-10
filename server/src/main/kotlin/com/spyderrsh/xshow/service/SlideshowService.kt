package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.slideshow.SlideshowSessionManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch

@Suppress("ACTUAL_WITHOUT_EXPECT")
class SlideshowService(
    private val slideshowSessionManager: SlideshowSessionManager,
) : ISlideshowService {
    override suspend fun nextMedia(): FileModel.Media {
        return slideshowSessionManager.getNextItem()
    }

    override suspend fun nextNMedia(count: Int): List<FileModel.Media> {
        TODO("Not yet implemented")
    }

    override suspend fun previousMedia(): FileModel.Media {
        return slideshowSessionManager.getPreviousItem()
    }

    override suspend fun deleteMedia(media: FileModel.Media) {
        slideshowSessionManager.deleteItem(media)
    }
}

fun ISlideshowService.setupRouting(routing: Routing) {
    routing.get(ISlideshowService.NEXT_MEDIA_ENDPOINT) {
        call.respond(nextMedia())
    }
    routing.get(ISlideshowService.PREVIOUS_MEDIA_ENDPOINT) {
        call.respond(previousMedia())
    }
    routing.post(ISlideshowService.DELETE_MEDIA_ENDPOINT) {
        runCatching {
            call.receive<FileModel.Media>()
        }.onSuccess { mediaToDelete ->
            launch {
                runCatching {
                    deleteMedia(mediaToDelete)
                }.onSuccess {
                    call.respond(HttpStatusCode.Accepted)
                }.onFailure {
                    logError(call, it)
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }.onFailure {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}