package com.spyderrsh.xshow

import com.spyderrsh.xshow.filesystem.FileSystemBrowser
import com.spyderrsh.xshow.ui.ShowLoading
import com.spyderrsh.xshow.ui.UnsupportedState
import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.ToastifyModule
import io.kvision.core.LineBreak
import io.kvision.html.Span
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication
import io.kvision.state.bind
import io.kvision.state.insert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

val AppScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

class App : Application() {

    init {
        startKoin {

        }
    }

    private val loadedModules = mutableListOf<Module>()

    override fun start(state: Map<String, Any>) {

        AppScope.launch {
//            val rootFolder = Model.getRootFolder().also { println(it) }
            loadedModules.addAll(
                listOf(appModule())
            )
            loadKoinModules(loadedModules)
        val component = AppComponent()

        root("kvapp") {
            div().bind(component.appViewModel.appState) {
                when {
                    it.currentScreen == AppScreen.FileSystemBrowser && it.rootFolder == null -> {
                        ShowLoading()
                    }
                    it.currentScreen == AppScreen.FileSystemBrowser && it.rootFolder != null -> {
                        FileSystemBrowser(it.rootFolder)
                    }
                    else -> UnsupportedState(it)
                }
            }

        }

        }
    }

    override fun dispose(): Map<String, Any> {
        unloadKoinModules(loadedModules)
        loadedModules.clear()
        return super.dispose()
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        ToastifyModule,
        CoreModule
    )
}
