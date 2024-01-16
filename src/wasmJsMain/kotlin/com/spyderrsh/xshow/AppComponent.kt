package com.spyderrsh.xshow

import com.spyderrsh.xshow.filesystem.FileSystemService
import com.spyderrsh.xshow.redux.createTypedReduxStore
import com.spyderrsh.xshow.slideshow.SlideshowService
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//
//class AppComponent: KoinComponent {
//    val appViewModel: AppViewModel by inject()
//}

object AppComponent {
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    }
    const val serverBaseUrl = "http://192.168.1.84:8080"
    val filesystemService = FileSystemService(httpClient, serverBaseUrl)
    val slideshowService = SlideshowService(httpClient, serverBaseUrl)
    val appViewModel = AppViewModel(createTypedReduxStore(::appReducer, AppState()))
}