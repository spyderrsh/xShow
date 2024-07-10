package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel

data class SlideshowState(
    val currentMedia: FileModel.Media? = null,
    val fullscreen: Boolean = false

)

sealed interface SlideshowAction {
    object ToggleFullscreen : SlideshowAction

    data class UpdateItem(val value: FileModel.Media) : SlideshowAction
}

fun slideshowReducer(
    state: SlideshowState,
    action: SlideshowAction
): SlideshowState {
    return when (action) {
        is SlideshowAction.UpdateItem -> handleUpdateItem(state, action)
        is SlideshowAction.ToggleFullscreen -> handleToggleFullscreen(state, action)
    }
}

fun handleToggleFullscreen(state: SlideshowState, action: SlideshowAction.ToggleFullscreen): SlideshowState =
    state.copy(
        fullscreen = !state.fullscreen
    )

fun handleUpdateItem(state: SlideshowState, action: SlideshowAction.UpdateItem): SlideshowState = state.copy(
    currentMedia = action.value
)
