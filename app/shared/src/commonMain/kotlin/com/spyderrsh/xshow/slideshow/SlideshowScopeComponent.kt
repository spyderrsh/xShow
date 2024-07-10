package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.redux.createTypedReduxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object SlideShowScopeComponent {
    val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val slideShowViewModel: SlideshowViewModel =
        SlideshowViewModel(createTypedReduxStore(::slideshowReducer, SlideshowState()), scope)
}
//object SlideShowModule {
//    val NAMED_SLIDESHOW_STATE = named("SlideshowState")
//}
//fun Module.slideShowModule() {
//    single(NAMED_SLIDESHOW_STATE) {
//        createTypedReduxStore(::slideshowReducer, SlideshowState())
//    }
//    single(NAMED_SLIDESHOW_STATE) {
//        CoroutineScope(Dispatchers.Default + SupervisorJob())
//    }
//    single { SlideshowViewModel(get(NAMED_SLIDESHOW_STATE), get(NAMED_SLIDESHOW_STATE)) }
//}