package com.spyderrsh.xshow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import com.spyderrsh.xshow.AppComponent.appViewModel
import com.spyderrsh.xshow.filesystem.FileSystemBrowser
import com.spyderrsh.xshow.style.XshowTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.configureWebResources
import org.jetbrains.compose.resources.urlResource


@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    configureWebResources {
        // same as default - this is not necessary to add here. It's here to show this feature
        setResourceFactory { urlResource("./$it") }
    }
    // koin needs wasm support
//    startKoin {
//
//    }
    CanvasBasedWindow("XShow") {
        MainWindow(appViewModel)
    }
}

@Composable
fun MainWindow(appViewModel: AppViewModel) {
    val appState by appViewModel.appStateFlow.collectAsState(AppState())
    XshowTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            XShow(appState)
        }
    }
}

@Composable
fun XShow(appState: AppState) {
    when (appState.currentScreen) {
        AppScreen.FileSystemBrowser -> FileSystemBrowser(appState)
        AppScreen.SlideShow -> TODO(" Slideshow()")
    }
}
