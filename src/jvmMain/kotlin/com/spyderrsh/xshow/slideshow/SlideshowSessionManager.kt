package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel

interface SlideshowSessionManager {
    fun initialize()
    fun getNextItem(): FileModel.Media
    fun getPreviousItem(): FileModel.Media
    suspend fun deleteItem(media: FileModel.Media)
}