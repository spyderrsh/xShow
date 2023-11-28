package com.spyderrsh.xshow.db

import com.spyderrsh.xshow.db.dao.ImageDao
import com.spyderrsh.xshow.db.dao.VideoDao
import com.spyderrsh.xshow.db.table.ImageTable
import com.spyderrsh.xshow.db.table.VideoTable
import com.spyderrsh.xshow.model.FileModel
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import java.io.File
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

    suspend fun putNewVideo(transaction: Transaction, model: FileModel.Media.Video.Full) {
        if (model.path.length > VideoTable.MAX_PATH_LENGTH) {
            val message = "Video path is larger than allowed: ${model.path}"
            Logger.getGlobal().severe(message)
            throw IllegalArgumentException(message)
        }
        transaction.withSuspendTransaction {
            VideoDao.new {
                path = model.path
                extension = model.extension
                shortName = model.shortName
                serverPath = model.serverPath
                duration = model.duration!! // throw error if we are trying to put without duration
            }
        }
    }

    suspend fun putNewImage(transaction: Transaction, model: FileModel.Media.Image) {
        if (model.path.length > ImageTable.MAX_PATH_LENGTH) {
            val message = "Video path is larger than allowed: ${model.path}"
            Logger.getGlobal().severe(message)
            throw IllegalArgumentException(message)
        }
        transaction.withSuspendTransaction {
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

}
