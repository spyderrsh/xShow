package com.spyderrsh.xshow.media

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.db.MediaDbDataSource
import com.spyderrsh.xshow.db.dao.ImageDao
import com.spyderrsh.xshow.db.dao.VideoDao
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.XShowDispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import kotlin.time.Duration

class DefaultMediaRepository(
    private val mediaDbDataSource: MediaDbDataSource,
    private val dispatchers: XShowDispatchers,
    private val config: ServerConfig
) : MediaRepository {
    override suspend fun getOrPutProcessedVideo(
        transaction: Transaction,
        file: File,
        function: (File) -> Result<FileModel.Media.Video.Full>
    ): Result<FileModel.Media.Video.Full> {
        mediaDbDataSource.getVideoByPath(transaction, file.absolutePath)?.let {
            return runCatching {
                it.toFullVideoModel()
            }
        }
        return function(file).mapCatching {
            mediaDbDataSource.putNewVideo(transaction, it)
            it
        }
    }

    override suspend fun getOrPutProcessedImage(
        transaction: Transaction,
        file: File,
        function: (File) -> Result<FileModel.Media.Image>
    ): Result<FileModel.Media.Image> {
        mediaDbDataSource.getImageByPath(transaction, file.absolutePath)?.let {
            return runCatching { it.toImageModel() }
        }
        return function(file).mapCatching {
            mediaDbDataSource.putNewImage(transaction, it)
            it
        }
    }

    override suspend fun getMedia(
        rootPath: String,
        images: Boolean,
        videos: Boolean,
        clips: Boolean,
        clipDuration: Duration
    ): Result<Collection<FileModel.Media>> = runCatching {
        withContext(dispatchers.io) {
            val collection = mutableListOf<FileModel.Media>()
            newSuspendedTransaction {
                if (images) {

                    mediaDbDataSource.getAllImagesInDir(this, rootPath)
                        .mapTo(collection) { it.toImageModel() }
                }
                if (videos) {
                    if (!clips) {
                        mediaDbDataSource.getAllVideosInDir(this, rootPath)
                            .filter { it.isSupportedExtension() }
                            .mapTo(collection) {
                                it.toFullVideoModel()
                            }

                    } else {
                        mediaDbDataSource.getAllVideosInDir(this, rootPath)
                            .filter { it.isSupportedExtension() }
                            .flatMapTo(collection) {
                                it.toFullVideoModel().transformToVideoPlusClips(clipDuration)
                            }
                    }
                }
            }
            collection
        }
    }

    override suspend fun getAllUnsupportedVideos(): Result<Collection<FileModel.Media.Video.Full>> = runCatching {
        withContext(dispatchers.io) {
            newSuspendedTransaction {
                mediaDbDataSource.getAllVideosInDir(this, config.rootFolderPath)
                    .filterNot { it.isSupportedExtension() }
                    .map { it.toFullVideoModel() }
            }
        }
    }

    override suspend fun deleteItem(transaction: Transaction, media: FileModel.Media): Result<Unit> {
        return runCatching {
            when (media) {
                is FileModel.Media.Image -> deleteImage(transaction, media)
                is FileModel.Media.Video -> deleteVideo(transaction, media)
            }
        }
    }

    private suspend fun deleteVideo(transaction: Transaction, media: FileModel.Media.Video) {
        mediaDbDataSource.deleteVideoByPath(transaction, media)
    }

    private suspend fun deleteImage(transaction: Transaction, image: FileModel.Media.Image) {
        mediaDbDataSource.deleteImageByPath(transaction, image)
    }

    private fun VideoDao.toFullVideoModel(): FileModel.Media.Video.Full {
        return FileModel.Media.Video.Full(
            path = path,
            shortName = shortName,
            serverPath = serverPath,
            extension = extension,
            duration = duration
        )
    }

    private fun ImageDao.toImageModel(): FileModel.Media.Image {
        return FileModel.Media.Image(
            path = path,
            serverPath = serverPath,
            shortName = shortName,
            extension = extension
        )
    }
}

private fun VideoDao.isSupportedExtension(): Boolean {
    return extension.lowercase() == "mp4" || extension == "mkv"
}

private fun FileModel.Media.Video.Full.transformToVideoPlusClips(clipDuration: Duration): Iterable<FileModel.Media> {
    val durationSeconds = duration?.inWholeSeconds?.toInt() ?: return listOf(this)
    val clipSeconds = clipDuration.inWholeSeconds.toInt()
    val doubleClipSeconds = 2 * clipSeconds
    return IntProgression.fromClosedRange(
        rangeStart = 0,
        rangeEnd = durationSeconds,
        step = clipSeconds
    ).mapNotNull { start ->
        if (start + clipSeconds >= durationSeconds) {
            return@mapNotNull null // Clip is too short
        }
        // If next clip would go over time, just include it with this clip
        val endTime = if (start + doubleClipSeconds >= durationSeconds) {
            durationSeconds
        } else {
            // clip regular
            start + clipSeconds
        }
        FileModel.Media.Video.Clip(
            this,
            start,
            endTime
        )
    } + this
}

