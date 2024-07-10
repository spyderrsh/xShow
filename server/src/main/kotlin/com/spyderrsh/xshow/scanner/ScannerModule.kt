package com.spyderrsh.xshow.scanner

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFprobe
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val ScannerModule = module {
    singleOf(::DefaultDirectoryScanner) {
        createdAtStart()
        bind<DirectoryScanner>()
    }
    singleOf(::DefaultFileProcessor) {
        createdAtStart()
        bind<FileProcessor>()
    }
    singleOf(::DefaultVideoProcessor) {
        createdAtStart()
        bind<VideoProcessor>()
    }
    singleOf(::DefaultDirectoryProcessor) {
        createdAtStart()
        bind<DirectoryProcessor>()
    }
    singleOf(::DefaultImageProcessor) {
        createdAtStart()
        bind<ImageProcessor>()
    }
    single { FFmpeg() }.withOptions {
        createdAtStart()
    }
    single { FFprobe() }.withOptions {
        createdAtStart()
    }
}