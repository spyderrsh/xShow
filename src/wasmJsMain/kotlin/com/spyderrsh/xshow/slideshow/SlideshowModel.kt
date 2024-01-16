package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.AppComponent
import com.spyderrsh.xshow.model.FileModel

object SlideshowModel {

    private val slideshowService = AppComponent.slideshowService

    suspend fun getNextMedia() = slideshowService.nextMedia()

    suspend fun getPreviousMedia() = slideshowService.previousMedia()
    suspend fun deleteMedia(media: FileModel.Media) = slideshowService.deleteMedia(media)
}