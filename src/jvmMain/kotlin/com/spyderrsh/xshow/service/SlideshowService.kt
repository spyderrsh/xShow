package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.slideshow.SlideshowSessionManager

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class SlideshowService(
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