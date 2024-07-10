package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel

object SlideshowModel {

    private val slideshowService = com.spyderrsh.xshow.AppComponent.slideshowService

    suspend fun getNextMedia() = slideshowService.nextMedia()

    suspend fun getPreviousMedia() = slideshowService.previousMedia()
    suspend fun deleteMedia(media: FileModel.Media) = slideshowService.deleteMedia(media)
}