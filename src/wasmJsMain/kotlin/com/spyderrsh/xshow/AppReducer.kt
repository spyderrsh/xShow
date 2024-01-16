package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.slideshow.SlideshowOptions


data class AppState(
    val currentScreen: AppScreen = AppScreen.FileSystemBrowser,
    val previousScreens: List<AppScreen> = emptyList(),
    val rootFolder: FileModel.Folder? = null,
    val slideshowOptions: SlideshowOptions? = null,
    val errors: List<Throwable> = emptyList()
)

enum class AppScreen {
    FileSystemBrowser,
    SlideShow
}


sealed interface AppAction {
    object ExitSlideshow : AppAction

    data class ReceivedRootFolder(val startFolder: FileModel.Folder) : AppAction
    data class StartSlideshow(val slideshowOptions: SlideshowOptions) : AppAction
    data class FailedToReceiveRootFolder(val e: Throwable) : AppAction
}

fun appReducer(state: AppState, action: AppAction): AppState {
    return when (action) {
        is AppAction.StartSlideshow -> reduceStartSlideshow(state, action)
        is AppAction.FailedToReceiveRootFolder -> reduceFailedToReceiveRootFolder(state, action)
        is AppAction.ReceivedRootFolder -> reduceReceivedRootFolder(state, action)
        is AppAction.ExitSlideshow -> reduceExitSlideshow(state, action)
    }
}

fun reduceExitSlideshow(state: AppState, action: AppAction.ExitSlideshow): AppState = state.copy(
    currentScreen = state.previousScreens.last(),
    previousScreens = state.previousScreens.dropLast(1)
)

fun reduceReceivedRootFolder(state: AppState, action: AppAction.ReceivedRootFolder): AppState =
    state.copy(rootFolder = action.startFolder)


fun reduceFailedToReceiveRootFolder(state: AppState, action: AppAction.FailedToReceiveRootFolder): AppState =
    state.copy(errors = state.errors + action.e)

private fun reduceStartSlideshow(state: AppState, action: AppAction.StartSlideshow): AppState =
    state.copy(
        currentScreen = AppScreen.SlideShow,
        previousScreens = state.previousScreens + state.currentScreen,
        slideshowOptions = action.slideshowOptions
    )

