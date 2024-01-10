package com.spyderrsh.xshow.scanner

import com.spyderrsh.xshow.media.MediaRepository
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.XShowFileModelUtil
import org.jetbrains.exposed.sql.Transaction
import java.io.File

class DefaultImageProcessor(
    private val mediaRepository: MediaRepository,
    private val fileModelUtil: XShowFileModelUtil
) : ImageProcessor {

    override suspend fun process(transaction: Transaction, file: File) {
        mediaRepository.getOrPutProcessedImage(transaction, file) {
            processInternal(file)
        }
    }

    private fun processInternal(file: File): Result<FileModel.Media.Image> {
        return runCatching {
            fileModelUtil.createImageFromFile(file)
        }
    }
}