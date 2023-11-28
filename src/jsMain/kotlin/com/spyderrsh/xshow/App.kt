package com.spyderrsh.xshow

import com.spyderrsh.xshow.filesystem.FileSystemBrowser
import com.spyderrsh.xshow.slideshow.Slideshow
import com.spyderrsh.xshow.ui.ShowLoading
import com.spyderrsh.xshow.ui.UnsupportedState
import io.kvision.*
import io.kvision.html.div
import io.kvision.panel.root
import io.kvision.state.bind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
                println(it)
                when {
                    it.currentScreen == AppScreen.FileSystemBrowser && it.rootFolder == null -> {
                        ShowLoading()
                    }
                    it.currentScreen == AppScreen.FileSystemBrowser && it.rootFolder != null -> {
                        FileSystemBrowser(it.rootFolder)
                    }
                    it.currentScreen == AppScreen.SlideShow -> {
                        Slideshow()
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
