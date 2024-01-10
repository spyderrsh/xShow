package com.spyderrsh.xshow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
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
    CanvasBasedWindow("ImageViewer") {
        MainWindow()
    }
}

@Composable
fun MainWindow() {
    XshowTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            XShow()
        }
    }
}

@Composable
fun XShow() {
    Box(Modifier.wrapContentSize()) {
        Text("Loading")
    }
}
