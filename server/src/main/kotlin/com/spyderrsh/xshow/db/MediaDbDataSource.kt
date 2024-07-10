package com.spyderrsh.xshow.db

import com.spyderrsh.xshow.db.dao.ImageDao
import com.spyderrsh.xshow.db.dao.VideoDao
import com.spyderrsh.xshow.db.table.ImageTable
import com.spyderrsh.xshow.db.table.VideoTable
import com.spyderrsh.xshow.model.FileModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class MediaDbDataSource {
    suspend fun getVideoByPath(transaction: Transaction, path: String): VideoDao? {
        return transaction.withSuspendTransaction {
            VideoTable.select { VideoTable.path eq path }.limit(1).singleOrNull()?.let {
                VideoDao.wrapRow(it)
            }
        }
    }

    suspend fun getImageByPath(transaction: Transaction, path: String): ImageDao? {
        return transaction.withSuspendTransaction {
            ImageTable.select { ImageTable.path eq path }.limit(1).singleOrNull()?.let {
                ImageDao.wrapRow(it)
            }
        }
    }

    // Helper method to handle common logic in putNewVideo and putNewImage
    suspend fun putNewMedia(transaction: Transaction, model: FileModel.Media, maxLength: Int, putAction: () -> Unit) {
        if (model.path.length > maxLength) {
            val message = "Media path is larger than allowed: ${model.path}"
            Logger.getGlobal().severe(message)
            throw IllegalArgumentException(message)
        }
        transaction.withSuspendTransaction {
            try {
                putAction()
            } catch (exception: Throwable) {
                Logger.getGlobal().log(Level.WARNING, "Issue adding ${model.shortName} to database", exception)
            }
            Logger.getGlobal().info("Added ${model.shortName} to database")
        }
    }

    // Updated putNewVideo and putNewImage methods
    suspend fun putNewVideo(transaction: Transaction, model: FileModel.Media.Video.Full) {
        val modelDuration = model.duration
        if (modelDuration == null) {
            val message = "Issue checking file (corrupted?): ${model.path}"
            Logger.getGlobal().severe(message)
            throw IllegalArgumentException(message)
        }
        putNewMedia(transaction, model, VideoTable.MAX_PATH_LENGTH) {
            VideoDao.new {
                path = model.path
                extension = model.extension
                shortName = model.shortName
                serverPath = model.serverPath
                duration = modelDuration
            }
        }
    }

    suspend fun putNewImage(transaction: Transaction, model: FileModel.Media.Image) {
        putNewMedia(transaction, model, ImageTable.MAX_PATH_LENGTH) {
            ImageDao.new {
                path = model.path
                extension = model.extension
                shortName = model.shortName
                serverPath = model.serverPath
            }
        }
    }

    suspend fun getAllImagesInDir(transaction: Transaction, path: String): Collection<ImageDao> {
        val folder = File(path)
        return transaction.withSuspendTransaction {
            ImageDao.find { ImageTable.path like "${folder.absolutePath}%" }.toList().also {
                Logger.getGlobal().info("Found ${it.size} images in $path*")
            }
        }
    }

    suspend fun getAllVideosInDir(transaction: Transaction, path: String): Collection<VideoDao> {
        val folder = File(path)
        return transaction.withSuspendTransaction {
            VideoDao.find { VideoTable.path like "${folder.absolutePath}%" }.toList().also {
                Logger.getGlobal().info("Found ${it.size} videos in $path*")
            }
        }
    }

    suspend fun deleteVideoByPath(transaction: Transaction, video: FileModel.Media.Video) {
        transaction.withSuspendTransaction {
            VideoTable.deleteWhere { VideoTable.path eq video.path }
        }
    }

    suspend fun deleteImageByPath(transaction: Transaction, image: FileModel.Media.Image) {
        transaction.withSuspendTransaction {
            ImageTable.deleteWhere { ImageTable.path eq image.path }
        }
    }

}
