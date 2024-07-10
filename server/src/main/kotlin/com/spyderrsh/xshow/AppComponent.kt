package com.spyderrsh.xshow

import com.spyderrsh.xshow.db.DatabaseInitializer
import com.spyderrsh.xshow.scanner.DirectoryScanner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppComponent() : KoinComponent {
    val directoryScanner: DirectoryScanner by inject()
    val databaseInitializer: DatabaseInitializer by inject()

    companion object {
        private val INSTANCE by lazy { AppComponent() }
        fun get() = AppComponent()
    }
}