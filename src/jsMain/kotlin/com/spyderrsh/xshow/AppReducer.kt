package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.slideshow.SlideshowOptions
import io.kvision.redux.RAction
import org.reduxkotlin.TypedReducer

sealed interface AppState {
    data object Starting : AppState

    data class ErrorState(val error: Throwable, val lastGoodState: AppState) : AppState
    data class FileBrowser(val startFolder: FileModel.Folder) : AppState
    data class Slideshow(val slideshowOptions: SlideshowOptions) : AppState
}

sealed interface AppAction : RAction {
    data class ReceivedRootFolder(val startFolder: FileModel.Folder) : AppAction
    data class StartSlideshow(val slideshowOptions: SlideshowOptions) : AppAction
    data class FailedToReceiveRootFolder(val e: Throwable) : AppAction
}

fun appReducer(state: AppState, action: AppAction): AppState {
    return when (action) {
        is AppAction.StartSlideshow -> reduceStartSlideshow(state, action)
        is AppAction.FailedToReceiveRootFolder -> reduceFailedToReceiveRootFolder(state, action)
        is AppAction.ReceivedRootFolder -> reduceReceivedRootFolder(state, action)
    }
}

fun reduceReceivedRootFolder(state: AppState, action: AppAction.ReceivedRootFolder): AppState = when (state) {
    else -> AppState.FileBrowser(action.startFolder)
}

fun reduceFailedToReceiveRootFolder(state: AppState, action: AppAction.FailedToReceiveRootFolder): AppState =
    when (state) {
        else -> AppState.ErrorState(action.e, state)
    }

private fun reduceStartSlideshow(state: AppState, action: AppAction.StartSlideshow): AppState = when (state) {
    else -> AppState.Slideshow(action.slideshowOptions)
}
