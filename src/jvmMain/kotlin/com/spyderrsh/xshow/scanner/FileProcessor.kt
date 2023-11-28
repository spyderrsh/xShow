package com.spyderrsh.xshow.scanner

import kotlinx.coroutines.channels.SendChannel
import java.io.File

interface FileProcessor {
    suspend fun groupByMediaType(
        file: File,
        folderFlow: SendChannel<File>,
        videoFlow: SendChannel<File>,
        imageFlow: SendChannel<File>
    )

}
