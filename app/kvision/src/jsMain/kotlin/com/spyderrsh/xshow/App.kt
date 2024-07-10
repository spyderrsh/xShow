package com.spyderrsh.xshow

import com.spyderrsh.xshow.filesystem.FileSystemBrowser
import com.spyderrsh.xshow.slideshow.Slideshow
import com.spyderrsh.xshow.ui.ShowLoading
import com.spyderrsh.xshow.ui.UnsupportedState
import io.kvision.*
import io.kvision.panel.root
import io.kvision.state.bind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val AppScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

class App : Application() {


    override fun start(state: Map<String, Any>) {

        AppScope.launch {

            val component = AppComponent

        root("kvapp") {
            bind(component.appViewModel.appStateFlow) {
                println(it)
                when {
                    it.currentScreen == AppScreen.FileSystemBrowser && it.rootFolder == null -> {
                        ShowLoading()
                    }
                    it.currentScreen == AppScreen.FileSystemBrowser && it.rootFolder != null -> {
                        FileSystemBrowser(it.rootFolder!!)
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
