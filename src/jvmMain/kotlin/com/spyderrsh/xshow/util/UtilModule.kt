package com.spyderrsh.xshow.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.Executors


private const val SCANNER = "scanner"
val NamedScanner = named(SCANNER)

val UtilModule = module {
    singleOf(::DefaultFileModelUtil) {
        createdAtStart()
        bind<XShowFileModelUtil>()
    }

    single(NamedScanner) {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }.withOptions {
        createdAtStart()
        bind<CoroutineDispatcher>()
    }

    single { DefaultXShowDispatchers(get(NamedScanner)) }
        .withOptions {
            bind<XShowDispatchers>()
        }
}