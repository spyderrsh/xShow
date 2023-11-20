package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.filesystem.FileSystemBrowserModule.NAMED_BROWSER_STATE
import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.createTypedReduxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class FileSystemBrowserScopeComponent : KoinComponent {
    val fileSystemBrowserViewModel: FileSystemBrowserViewModel by inject()
}
object FileSystemBrowserModule {
    val NAMED_BROWSER_STATE = named("BrowserState")
}
fun Module.fileSystemBrowserModule() {
    single(NAMED_BROWSER_STATE) {
        createTypedReduxStore(::fileSystemBrowserReducer, FileSystemBrowserState())
    }
    single(NAMED_BROWSER_STATE) {
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    }
    single { FileSystemBrowserViewModel(get(NAMED_BROWSER_STATE), get(NAMED_BROWSER_STATE)) }
}