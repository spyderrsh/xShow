package com.spyderrsh.xshow.service

import com.spyderrsh.xshow.model.FileModel

//import io.kvision.annotations.KVService

//@KVService
interface ISlideshowService {
    suspend fun nextMedia(): FileModel.Media
    suspend fun nextNMedia(count: Int): List<FileModel.Media>
    suspend fun previousMedia(): FileModel.Media
    suspend fun deleteMedia(media: FileModel.Media)
}