package com.spyderrsh.xshow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.CanvasBasedWindow
import com.hamama.kwhi.LocalLayerContainer
import com.spyderrsh.xshow.AppComponent.appViewModel
import com.spyderrsh.xshow.filesystem.FileSystemBrowser
import com.spyderrsh.xshow.slideshow.Slideshow
import com.spyderrsh.xshow.style.XshowTheme
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.configureWebResources

fun clearCanvasBackground(): Unit = js(
    """{
            var gl = document.getElementById("ComposeTarget").getContext('webgl2');
            if(gl) {
                gl.clearColor(0, 0, 0, 0);
                gl.clear(gl.COLOR_BUFFER_BIT);
            }
        }"""
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    configureWebResources {
        // same as default - this is not necessary to add here. It's here to show this feature
//        resourcePathMapping { "./$it" }
//        setResourceFactory { urlResource("./$it") }
    }
    // koin needs wasm support
//    startKoin {
//
//    }
    CanvasBasedWindow(title = "XShow", applyDefaultStyles = true) {

        CompositionLocalProvider(LocalLayerContainer provides document.body!!) {

            var invalidations by remember { mutableStateOf(0) }
            window.onresize = {
                invalidations = 0
            }
            MainWindow(appViewModel)
            LaunchedEffect(invalidations) {
                clearCanvasBackground()
                if (invalidations < 1) {
                    invalidations++
                }
            }
        }
    }
}

@Composable
fun MainWindow(appViewModel: AppViewModel) {
    val appState by appViewModel.appStateFlow.collectAsState(AppState())
    XshowTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {

        XShow(
                appState = appState,
                onPlayClick = { appViewModel.startSlideshow(appState.rootFolder!!) },
                onCloseSlideshowClick = { appViewModel.exitSlideshow() })
        }
    }
}

@Composable
fun XShow(appState: AppState, onPlayClick: () -> Unit, onCloseSlideshowClick: () -> Unit) {

    when (appState.currentScreen) {
        AppScreen.FileSystemBrowser -> FileSystemBrowser(appState, onPlayClick)
        AppScreen.SlideShow -> Slideshow(appState, onCloseSlideshowClick)
    }
}
