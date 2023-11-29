package com.spyderrsh.xshow.media

import com.spyderrsh.xshow.model.FileModel
import org.jetbrains.exposed.sql.Transaction
import java.io.File
import kotlin.time.Duration

interface MediaRepository {
    suspend fun getOrPutProcessedVideo(
        transaction: Transaction,
        file: File,
        function: (File) -> Result<FileModel.Media.Video.Full>
    ): Result<FileModel.Media.Video.Full>

    suspend fun getOrPutProcessedImage(
        transaction: Transaction,
        file: File,
        function: (File) -> Result<FileModel.Media.Image>
    ): Result<FileModel.Media.Image>

    suspend fun getMedia(
        rootPath: String,
        images: Boolean,
        videos: Boolean,
        clips: Boolean,
        clipDuration: Duration
    ): Result<Collection<FileModel.Media>>

    suspend fun deleteItem(transaction: Transaction, media: FileModel.Media): Result<Unit>
}
