package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel

interface ISlideshowService {
    suspend fun nextMedia(): FileModel.Media
    suspend fun nextNMedia(count: Int): List<FileModel.Media>
    suspend fun previousMedia(): FileModel.Media
    suspend fun deleteMedia(media: FileModel.Media)

    companion object {
        val NEXT_MEDIA_ENDPOINT = "/nextMedia"
        val PREVIOUS_MEDIA_ENDPOINT = "/previousMedia"
        val DELETE_MEDIA_ENDPOINT = "/deleteMedia"
    }

}