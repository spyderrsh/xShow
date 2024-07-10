package com.spyderrsh.xshow.ui

import com.spyderrsh.xshow.model.FileModel
import io.kvision.core.*
import io.kvision.html.Div
import io.kvision.html.TAG
import io.kvision.html.image
import io.kvision.html.tag
import io.kvision.utils.auto
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
    (this as? Div)?.apply {
        addCssStyle(FillContainerStyle)
        addCssStyle(CenterInPageStyle)
    }
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
        addCssStyle(FillContainerStyle)
        addCssStyle(CenterInPageStyle)
        onClick { callbacks.onImageClick(image) }
    }
}

fun Container.FileModelVideo(video: FileModel.Media.Video, callbacks: MediaViewCallbacks) {
    tag(
        TAG.VIDEO, attributes =
        mapOf("autoplay" to "", "controls" to "", "src" to video.serverPath)
    ) {
        addCssStyle(FillContainerStyle)
        addCssStyle(CenterInPageStyle)
        onEvent {
            ended = { callbacks.onVideoEnd(video) }
            pause = {
                if (video is FileModel.Media.Video.Clip) {
                    val currentTime = this@tag.getElementD().currentTime as? Double
                    currentTime?.let {
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

val FillContainerStyle = Style {

    maxHeight = CssSize(100, UNIT.vh)
    maxWidth = CssSize(100, UNIT.vw)
    height = CssSize(100, UNIT.perc)
    width = CssSize(100, UNIT.perc)
    margin = auto
//    width = auto
    display = Display.BLOCK
    this.setStyle("object-fit", "contain")
}
val CenterInPageStyle = Style {
    position = Position.ABSOLUTE
    top = CssSize(50, UNIT.perc)
    left = CssSize(50, UNIT.perc)
    this.setStyle("transform", "translate(-50%, -50%)")
}