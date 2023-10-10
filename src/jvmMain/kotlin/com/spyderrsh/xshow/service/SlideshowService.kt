package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class SlideshowService: ISlideshowService {
    override suspend fun nextMedia(): FileModel.Media {
        TODO("Not yet implemented")
    }

    override suspend fun nextNMedia(count: Int): List<FileModel.Media> {
        TODO("Not yet implemented")
    }

    override suspend fun previousMedia(): FileModel.Media {
        TODO("Not yet implemented")
    }
}