package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.ISlideshowService
import io.kvision.remote.getService

object SlideshowModel {

    private val slideshowService = getService<ISlideshowService>()

    suspend fun getNextMedia() = slideshowService.nextMedia()

    suspend fun getPreviousMedia() = slideshowService.previousMedia()
    suspend fun deleteMedia(media: FileModel.Media) {
        TODO("Not yet implemented")
    }
}