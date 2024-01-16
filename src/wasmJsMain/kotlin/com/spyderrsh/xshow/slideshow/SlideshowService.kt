package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.ISlideshowService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SlideshowService(private val client: HttpClient, private val baseUrl: String) : ISlideshowService {
    override suspend fun nextMedia(): FileModel.Media {
        return client.get("$baseUrl${ISlideshowService.NEXT_MEDIA_ENDPOINT}").body()
    }

    override suspend fun nextNMedia(count: Int): List<FileModel.Media> {
        TODO("Not yet implemented")
    }

    override suspend fun previousMedia(): FileModel.Media {
        return client.get("$baseUrl${ISlideshowService.PREVIOUS_MEDIA_ENDPOINT}").body()
    }

    override suspend fun deleteMedia(media: FileModel.Media) {
        return client.post("$baseUrl${ISlideshowService.DELETE_MEDIA_ENDPOINT}") {
            setBody(media)
        }.body()
    }
}
