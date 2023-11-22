package com.spyderrsh.xshow.ui

import com.spyderrsh.xshow.model.FileModel
import io.kvision.core.Component
import io.kvision.core.Container
import io.kvision.html.TAG
import io.kvision.html.image
import io.kvision.html.tag

fun Container.MediaView(media: FileModel.Media) {
    when (media) {
        is FileModel.Media.Image -> {
            FileModelImage(media)
        }

        is FileModel.Media.Video -> {
            FileModelVideo(media)
        }
    }
}

fun Container.FileModelImage(image: FileModel.Media.Image) {
    image(
        src = image.serverPath,
        centered = true
    )
}

fun Container.FileModelVideo(video: FileModel.Media.Video) {
    tag(
        TAG.VIDEO, attributes =
        mapOf("autoplay" to "", "controls" to "", "src" to video.serverPath)
    )
}