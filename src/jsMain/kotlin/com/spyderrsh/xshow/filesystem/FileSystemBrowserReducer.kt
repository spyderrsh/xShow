package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.RAction

sealed interface FileSystemBrowserState {

    private fun handleUnsupportedAction(action: FileSystemBrowserAction): FileSystemBrowserState =
        ErrorState(UnsupportedOperationException("$this cannot handle action $action"), this)

    fun handleLoadingFolderStarted(action: FileSystemBrowserAction.LoadingFolderStarted): FileSystemBrowserState =
        handleUnsupportedAction(action)

    fun handleFolderLoaded(action: FileSystemBrowserAction.FolderLoaded): FileSystemBrowserState =
        handleUnsupportedAction(action)

    fun handleFailedToLoad(action: FileSystemBrowserAction.FailedToLoad): FileSystemBrowserState =
        ErrorState(action.error, this)

    data object Starting : FileSystemBrowserState {
        override fun handleLoadingFolderStarted(action: FileSystemBrowserAction.LoadingFolderStarted): FileSystemBrowserState {
            return LoadingFolder(action.folderToLoad, null)
        }
    }

    data class LoadingFolder(val loadingFolder: FileModel.Folder, val currentFiles: Files?) : FileSystemBrowserState {
        override fun handleLoadingFolderStarted(action: FileSystemBrowserAction.LoadingFolderStarted): FileSystemBrowserState {
            return LoadingFolder(action.folderToLoad, currentFiles)
        }

        override fun handleFolderLoaded(action: FileSystemBrowserAction.FolderLoaded): FileSystemBrowserState {
            return Files(loadingFolder, action.files)
        }
    }

    data class ErrorState(val error: Throwable, val lastGoodState: FileSystemBrowserState) : FileSystemBrowserState
    data class Files(val currentFolder: FileModel.Folder, val files: List<FileModel>) : FileSystemBrowserState {
        override fun handleLoadingFolderStarted(action: FileSystemBrowserAction.LoadingFolderStarted): FileSystemBrowserState {
            return LoadingFolder(action.folderToLoad, this)
        }
    }
}

sealed interface FileSystemBrowserAction : RAction {
    data class LoadingFolderStarted(val folderToLoad: FileModel.Folder) : FileSystemBrowserAction
    data class FolderLoaded(val files: List<FileModel>) : FileSystemBrowserAction
    data class FailedToLoad(val error: Throwable) : FileSystemBrowserAction
}

fun fileSystemBrowserReducer(
    state: FileSystemBrowserState,
    action: FileSystemBrowserAction
): FileSystemBrowserState {
    return when (action) {
        is FileSystemBrowserAction.LoadingFolderStarted -> state.handleLoadingFolderStarted(action)
        is FileSystemBrowserAction.FolderLoaded -> state.handleFolderLoaded(action)
        is FileSystemBrowserAction.FailedToLoad -> state.handleFailedToLoad(action)
    }
}

