package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.RAction

data class SlideshowState(
    val currentMedia: FileModel.Media? = null

)
sealed interface SlideshowAction: RAction {
    data class UpdateItem(val value: FileModel.Media): SlideshowAction
}

fun slideshowReducer(
    state: SlideshowState,
    action: SlideshowAction
): SlideshowState {
    return when(action) {
        is SlideshowAction.UpdateItem -> handleUpdateItem(state, action)
    }
}

fun handleUpdateItem(state: SlideshowState, action: SlideshowAction.UpdateItem): SlideshowState = state.copy(
    currentMedia = action.value
)
