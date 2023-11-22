package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.RAction

data class FileSystemBrowserState(
    val currentFolder: FileModel.Folder? = null,
    val currentFiles: List<FileModel> = emptyList(),
    val loadingFolder: FileModel.Folder? = null,
    val errors: List<Throwable> = emptyList(),
    val overlayFile: FileModel.Media? = null
) {

    fun handleLoadingFolderStarted(action: FileSystemBrowserAction.LoadingFolderStarted): FileSystemBrowserState =
        copy(loadingFolder = action.folderToLoad)

    fun handleFolderLoaded(action: FileSystemBrowserAction.FolderLoaded): FileSystemBrowserState =
        copy(
            currentFolder = loadingFolder,
            loadingFolder = null,
            currentFiles = action.files
        )

    fun handleFailedToLoad(action: FileSystemBrowserAction.FailedToLoad): FileSystemBrowserState =
        copy(
            errors = errors + action.error,
            loadingFolder = null
        )

    fun handleShowOverlay(action: FileSystemBrowserAction.ShowOverlay): FileSystemBrowserState = copy(
        overlayFile = action.file
    )

    fun handleHideOverlay(action: FileSystemBrowserAction.HideOverlay): FileSystemBrowserState = copy(
        overlayFile = null
    )

    val isLoading = loadingFolder != null
}

sealed interface FileSystemBrowserAction : RAction {
    data class LoadingFolderStarted(val folderToLoad: FileModel.Folder) : FileSystemBrowserAction
    data class FolderLoaded(val files: List<FileModel>) : FileSystemBrowserAction
    data class FailedToLoad(val error: Throwable) : FileSystemBrowserAction

    data class ShowOverlay(val file: FileModel.Media): FileSystemBrowserAction
    object HideOverlay : FileSystemBrowserAction
}

fun fileSystemBrowserReducer(
    state: FileSystemBrowserState,
    action: FileSystemBrowserAction
): FileSystemBrowserState {
    return when (action) {
        is FileSystemBrowserAction.LoadingFolderStarted -> state.handleLoadingFolderStarted(action)
        is FileSystemBrowserAction.FolderLoaded -> state.handleFolderLoaded(action)
        is FileSystemBrowserAction.FailedToLoad -> state.handleFailedToLoad(action)
        is FileSystemBrowserAction.ShowOverlay -> state.handleShowOverlay(action)
        is FileSystemBrowserAction.HideOverlay -> state.handleHideOverlay(action)
    }
}

