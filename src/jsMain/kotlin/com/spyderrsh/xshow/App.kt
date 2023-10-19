package com.spyderrsh.xshow

import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.ToastifyModule
import io.kvision.html.Span
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication
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
        val root = root("kvapp") {
        }

        AppScope.launch {
            val rootFolder = Model.getRootFolder()
            loadedModules.addAll(
                listOf(appModule(rootFolder))
            )
            loadKoinModules(loadedModules)

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
