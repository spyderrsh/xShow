package com.spyderrsh.xshow.converter

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val VideoConverterModule = module {
    singleOf(::DefaultVideoConverter) {
        createdAtStart()
        bind<VideoConverter>()
    }
}