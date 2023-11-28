package com.spyderrsh.xshow.slideshow

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val SlideshowModule = module {
    singleOf(::DefaultSlideshowSessionManager){
        bind<SlideshowSessionManager>()
    }
}