package com.spyderrsh.xshow.scanner

import com.spyderrsh.xshow.util.XShowFileModelUtil
import kotlinx.coroutines.channels.SendChannel
import java.io.File

class DefaultFileProcessor(
    private val fileModelUtil: XShowFileModelUtil
) : FileProcessor {
    override suspend fun groupByMediaType(
        file: File,
        folderFlow: SendChannel<File>,
        videoFlow: SendChannel<File>,
        imageFlow: SendChannel<File>
    ) {
        when {
            file.extension.lowercase() in fileModelUtil.VideoExtensions -> videoFlow.send(file)
            file.extension.lowercase() in fileModelUtil.ImageExtensions -> imageFlow.send(file)
            file.isDirectory -> folderFlow.send(file)
            else -> fileModelUtil.UnusedExtensions.add(file.extension.lowercase())
        }
    }
}