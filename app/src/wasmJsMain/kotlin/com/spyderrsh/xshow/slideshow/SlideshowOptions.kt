package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class SlideshowOptions(
    val rootFolder: FileModel.Folder,
    val picDuration: Duration = 3.seconds,
    val showPictures: Boolean = true,
    val showFullVideo: Boolean = true,
    val showClipVideo: Boolean = true
)
