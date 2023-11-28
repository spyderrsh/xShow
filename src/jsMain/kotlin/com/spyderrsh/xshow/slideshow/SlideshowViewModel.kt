package com.spyderrsh.xshow.slideshow

import io.kvision.redux.TypedReduxStore
import io.kvision.state.stateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SlideshowViewModel(
    private val stateStore: TypedReduxStore<SlideshowState, SlideshowAction>,
    private val scope: CoroutineScope
) {
    val state: StateFlow<SlideshowState> = stateStore.stateFlow
    fun fetchNextItem() {
        scope.launch {
            SlideshowModel.getNextMedia().let {
                stateStore.dispatch(SlideshowAction.UpdateItem(it))
            }
        }
    }
}