package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.Model
import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.TypedReduxStore
import io.kvision.state.ObservableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FileSystemBrowserViewModel(
    private val stateStore: TypedReduxStore<FileSystemBrowserState, FileSystemBrowserAction>,
    private val scope: CoroutineScope
) {
    fun loadFolder(folder: FileModel.Folder) {
        stateStore.dispatch(FileSystemBrowserAction.LoadingFolderStarted(folder))
    }

    val state: ObservableState<FileSystemBrowserState> get() = stateStore

    init {
        stateStore.subscribe { state ->
            state.loadingFolder?.let {
                stateStore.dispatch { dispatch, getState ->
                    scope.launch {
                        runCatching { Model.getFiles(it) }
                            .onSuccess { dispatch(FileSystemBrowserAction.FolderLoaded(it)) }
                            .onFailure { dispatch(FileSystemBrowserAction.FailedToLoad(it)) }
                    }
                }
            }
        }
    }
}