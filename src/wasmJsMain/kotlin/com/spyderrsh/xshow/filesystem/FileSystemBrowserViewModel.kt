package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.Model
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.redux.TypedReduxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
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

    fun onFileClick(model: FileModel) {
        println("Received click on $model")
        when (model) {
            is FileModel.Folder -> loadFolder(model)
            else -> println("Unhandled FileModel Click: ${model.shortName}")
        }
    }


    init {
        stateStore.stateFlow.onEach { state ->
            state.loadingFolder?.let {
                stateStore.dispatch { dispatch, getState ->
                    scope.launch {
                        runCatching { Model.getFiles(it) }
                            .onSuccess { dispatch(FileSystemBrowserAction.FolderLoaded(it)) }
                            .onFailure { dispatch(FileSystemBrowserAction.FailedToLoad(it)) }
                    }
                }
            }
        }.launchIn(scope)
    }
}