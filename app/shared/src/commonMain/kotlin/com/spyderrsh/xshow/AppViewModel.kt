package com.spyderrsh.xshow

import com.spyderrsh.xshow.AppComponent.appScope
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.redux.TypedReduxStore
import com.spyderrsh.xshow.slideshow.SlideshowOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppViewModel(
    private val stateStore: TypedReduxStore<AppState, AppAction>,
    private val scope: CoroutineScope
) {
    fun startSlideshow(currentFolder: FileModel.Folder) {
        stateStore.dispatch { dispatch, getState ->
            dispatch(AppAction.StartSlideshow(SlideshowOptions(currentFolder)))
        }
    }

    fun exitSlideshow() {
        stateStore.dispatch { dispatch, getState ->
            dispatch(AppAction.ExitSlideshow)
        }
    }

    val appStateFlow get() = stateStore.stateFlow
    
    init {

        stateStore.dispatch { dispatch, getState ->
            appScope.launch {
                runCatching { Model.getRootFolder() }
                    .onSuccess {
                        dispatch(AppAction.ReceivedRootFolder(it))
                    }
                    .onFailure {
                        dispatch(AppAction.FailedToReceiveRootFolder(it))
                    }
            }
        }

        appStateFlow.onEach { println("State updated: $it") }
            .launchIn(scope)
    }
}