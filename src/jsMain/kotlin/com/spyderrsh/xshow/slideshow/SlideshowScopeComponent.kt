package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.slideshow.SlideShowModule.NAMED_SLIDESHOW_STATE
import io.kvision.redux.createTypedReduxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named

class SlideShowScopeComponent : KoinComponent {
    val slideShowViewModel: SlideshowViewModel by inject()
}
object SlideShowModule {
    val NAMED_SLIDESHOW_STATE = named("SlideshowState")
}
fun Module.slideShowModule() {
    single(NAMED_SLIDESHOW_STATE) {
        createTypedReduxStore(::slideshowReducer, SlideshowState())
    }
    single(NAMED_SLIDESHOW_STATE) {
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    }
    single { SlideshowViewModel(get(NAMED_SLIDESHOW_STATE), get(NAMED_SLIDESHOW_STATE)) }
}