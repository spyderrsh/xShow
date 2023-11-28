package com.spyderrsh.xshow.scanner

import io.ktor.events.*

interface DirectoryScanner {
    fun start()

    object DirectoryScanFinished: EventDefinition<Unit>()
}