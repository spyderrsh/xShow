package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.redux.createTypedReduxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FileSystemBrowserScopeComponent {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val fileSystemBrowserViewModel: FileSystemBrowserViewModel =
        FileSystemBrowserViewModel(createTypedReduxStore(::fileSystemBrowserReducer, FileSystemBrowserState()), scope)
}
//
//object FileSystemBrowserModule {
//    val NAMED_BROWSER_STATE = named("BrowserState")
//}
//
//fun Module.fileSystemBrowserModule() {
//    single(NAMED_BROWSER_STATE) {
//        createTypedReduxStore(::fileSystemBrowserReducer, FileSystemBrowserState())
//    }
//    single(NAMED_BROWSER_STATE) {
//        CoroutineScope(Dispatchers.Default + SupervisorJob())
//    }
//    single { FileSystemBrowserViewModel(get(NAMED_BROWSER_STATE), get(NAMED_BROWSER_STATE)) }
//}