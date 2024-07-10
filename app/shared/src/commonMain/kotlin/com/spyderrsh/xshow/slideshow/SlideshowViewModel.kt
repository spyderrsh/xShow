package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.redux.TypedReduxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SlideshowViewModel(
    private val stateStore: TypedReduxStore<SlideshowState, SlideshowAction>,
    private val scope: CoroutineScope
) {
    val state: StateFlow<SlideshowState> get() = stateStore.stateFlow

    init {
        state.onEach { println("State updated: $it") }
            .launchIn(scope)

        fetchNextItem()
    }
    fun fetchNextItem() {
        println("Fetching next item")
        scope.launch {
            fetchNextItemInternal()
        }
    }

    private suspend fun fetchNextItemInternal() {
        SlideshowModel.getNextMedia().let {
            stateStore.dispatch(SlideshowAction.UpdateItem(it))
        }
    }

    fun deleteItem(media: FileModel.Media) {
        scope.launch {
            fetchNextItemInternal()
            SlideshowModel.deleteMedia(media)
        }
    }

    fun toggleFullscreen() {
        stateStore.dispatch(SlideshowAction.ToggleFullscreen)
    }

    fun fetchPreviousItem() {
        scope.launch {
            fetchPreviousItemInternal()
        }
    }

    private suspend fun fetchPreviousItemInternal() {
        SlideshowModel.getPreviousMedia().let {
            stateStore.dispatch(SlideshowAction.UpdateItem(it))
        }
    }
}