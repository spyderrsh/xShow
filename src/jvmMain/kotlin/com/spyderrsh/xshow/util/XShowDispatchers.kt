package com.spyderrsh.xshow.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


interface XShowDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val scanner: CoroutineDispatcher
}
class DefaultXShowDispatchers(
    private val scannerDispatcher: CoroutineDispatcher
): XShowDispatchers {
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default

    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val scanner: CoroutineDispatcher
        get() = scannerDispatcher
}