package com.spyderrsh.xshow.converter

import com.spyderrsh.xshow.media.MediaRepository
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.filesystem.FilesystemRepository
import com.spyderrsh.xshow.util.XShowDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.job.FFmpegJob
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.logging.Logger
import kotlin.coroutines.resume

class DefaultVideoConverter(
    private val dispatchers: XShowDispatchers,
    private val mediaRepository: MediaRepository,
    private val ffMpeg: FFmpeg,
    private val fFprobe: FFprobe,
    private val filesystemRepository: FilesystemRepository
) : VideoConverter {
    private val executor = FFmpegExecutor(ffMpeg, fFprobe)
    private val scope = CoroutineScope(SupervisorJob())
    override fun start() {
        scope.launch {
            Logger.getGlobal().info("Starting Video Conversion")
            mediaRepository.getAllUnsupportedVideos().onSuccess {
                it.forEach {
                    Logger.getGlobal().info("Starting conversion for video: ${it.path}")
                    startConversion(it)
                }
            }.onFailure {
                Logger.getGlobal().severe("Failed to get all unsupported videos")
                Logger.getGlobal().throwing("DefaultVideoConverter", "start", it)

            }
        }

    }

    private suspend fun startConversion(video: FileModel.Media.Video.Full) {
        val builder = ffMpeg.builder().addInput(
            video.path
        ).addOutput(
            video.path.replace(".${video.extension}", "_converted_${video.extension}.mp4", true)
        ).setVideoCodec("libx264").setAudioCodec("aac").done()
        suspendCancellableCoroutine<FFmpegJob.State> { cancellableContinuation ->
            var job: FFmpegJob? = null
            job = executor.createJob(
                builder
            ) { progress ->
                println("Progress: $progress")
                if (progress.isEnd) {
                    cancellableContinuation.resume(job!!.state)
                }
            }
            runCatching {
                job!!.run()
            }.onFailure {
                Logger.getGlobal().severe("Failed to run job for video: ${video.path}")
                Logger.getGlobal().throwing("DefaultVideoConverter", "startConversion", it)
                cancellableContinuation.resume(FFmpegJob.State.FAILED)
            }
        }.let {
            when (it) {
                FFmpegJob.State.FINISHED,
                FFmpegJob.State.RUNNING -> {
                    println("Conversion Finished ($it), deleting original file")
                    newSuspendedTransaction {
                        mediaRepository.deleteItem(this, video)
                    }
                    filesystemRepository.deleteFile(video)
                }

                FFmpegJob.State.FAILED -> {
                    newSuspendedTransaction {
                        mediaRepository.deleteItem(this, video)
                    }
                    println("Conversion Failed for video: ${video.path}")
                }

                else -> {
                    println("Conversion was cancelled in state $it for video: ${video.path}")
                }
            }
        }

    }
}