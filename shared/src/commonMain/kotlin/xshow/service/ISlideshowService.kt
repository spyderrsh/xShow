package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel

interface ISlideshowService {
    suspend fun nextMedia(): FileModel.Media
    suspend fun nextNMedia(count: Int): List<FileModel.Media>
    suspend fun previousMedia(): FileModel.Media
    suspend fun deleteMedia(media: FileModel.Media)

    companion object {
        const val NEXT_MEDIA_ENDPOINT = "/nextMedia"
        const val PREVIOUS_MEDIA_ENDPOINT = "/previousMedia"
        const val DELETE_MEDIA_ENDPOINT = "/deleteMedia"
    }

}