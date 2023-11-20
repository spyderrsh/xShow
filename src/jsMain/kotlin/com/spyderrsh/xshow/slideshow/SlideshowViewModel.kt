package com.spyderrsh.xshow.slideshow

import io.kvision.redux.TypedReduxStore
import kotlinx.coroutines.CoroutineScope

class SlideshowViewModel(
    private val stateStore: TypedReduxStore<SlideshowState, SlideshowAction>,
    private val scope: CoroutineScope
)