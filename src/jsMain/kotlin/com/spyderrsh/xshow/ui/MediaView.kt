package com.spyderrsh.xshow.ui

import com.spyderrsh.xshow.model.FileModel
import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.core.onEvent
import io.kvision.html.TAG
import io.kvision.html.image
import io.kvision.html.tag
import kotlin.math.abs

interface MediaViewCallbacks {
    val onImageClick: (FileModel.Media.Image) -> Unit
    val onVideoClipEnd: (FileModel.Media.Video.Clip) -> Unit
    val onVideoEnd: (FileModel.Media.Video) -> Unit

    companion object {
        val EMPTY = object : MediaViewCallbacks {
            override val onImageClick: (FileModel.Media.Image) -> Unit = {}
            override val onVideoClipEnd: (FileModel.Media.Video.Clip) -> Unit = {}
            override val onVideoEnd: (FileModel.Media.Video) -> Unit = {}
        }
    }
}

fun Container.MediaView(media: FileModel.Media, callbacks: MediaViewCallbacks = MediaViewCallbacks.EMPTY) {
    when (media) {
        is FileModel.Media.Image -> {
            FileModelImage(media, callbacks)
        }

        is FileModel.Media.Video -> {
            FileModelVideo(media, callbacks)
        }
    }
}

fun Container.FileModelImage(image: FileModel.Media.Image, callbacks: MediaViewCallbacks) {
    image(
        src = image.serverPath,
        centered = true
    ) {
        onClick { callbacks.onImageClick(image) }
    }
}

fun Container.FileModelVideo(video: FileModel.Media.Video, callbacks: MediaViewCallbacks) {
    tag(
        TAG.VIDEO, attributes =
        mapOf("autoplay" to "", "controls" to "", "src" to video.serverPath)
    ) {
        onEvent {
            ended = { callbacks.onVideoEnd(video) }
            pause = {
                println("Pause event called")
                if (video is FileModel.Media.Video.Clip) {
                    val currentTime = this@tag.getElementD().currentTime as? Double
                    currentTime?.let {
                        println("currentTime = $it")
                        println("endTime = ${video.endTime}")
                        // Check that it paused close to the endTime
                        if (abs(it.toInt() - video.endTime) < 2) {
                            callbacks.onVideoClipEnd(video)
                        }
                    }
                }
            }
        }
    }
}