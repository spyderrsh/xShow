package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.Model
import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.TypedReduxStore
import io.kvision.state.ObservableState
import io.kvision.state.stateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FileSystemBrowserViewModel(
    private val stateStore: TypedReduxStore<FileSystemBrowserState, FileSystemBrowserAction>,
    private val scope: CoroutineScope
) {
    val state: StateFlow<FileSystemBrowserState> get() = stateStore.stateFlow
    val overlayState: StateFlow<FileModel.Media?> =
        state.map { it.overlayFile }
            .stateIn(scope, SharingStarted.Eagerly, null)

    fun loadFolder(folder: FileModel.Folder) {
        stateStore.dispatch(FileSystemBrowserAction.LoadingFolderStarted(folder))
    }

    fun showFileOverlay(file: FileModel.Media) {
        stateStore.dispatch(FileSystemBrowserAction.ShowOverlay(file))
    }

    fun hideFileOverlay() {
        stateStore.dispatch(FileSystemBrowserAction.HideOverlay)
    }


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