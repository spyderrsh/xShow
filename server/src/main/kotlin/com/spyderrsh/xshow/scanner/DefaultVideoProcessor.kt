package com.spyderrsh.xshow.scanner

import com.spyderrsh.xshow.media.MediaRepository
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.XShowFileModelUtil
import net.bramp.ffmpeg.FFprobe
import org.jetbrains.exposed.sql.Transaction
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.time.Duration.Companion.seconds

class DefaultVideoProcessor(
    private val ffprobe: FFprobe,
    private val mediaRepository: MediaRepository,
    private val fileModelUtil: XShowFileModelUtil
) : VideoProcessor {

    override suspend fun process(transaction: Transaction, file: File) {
        mediaRepository.getOrPutProcessedVideo(transaction, file) {
            processInternal(file)
        }.onFailure {
            Logger.getGlobal().log(Level.WARNING, "Issue processing file: ${file.absolutePath}", it)
        }
    }

    private fun processInternal(file: File): Result<FileModel.Media.Video.Full> {
        return runCatching {
            val probe = ffprobe.probe(file.absolutePath)
            val duration = probe.streams.maxOf { it.duration }
            fileModelUtil.createVideoFromFile(file, duration.seconds)
        }
    }
}