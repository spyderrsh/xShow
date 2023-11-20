package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.slideshow.SlideshowOptions
import io.kvision.redux.TypedReduxStore
import kotlinx.coroutines.launch

class AppViewModel(
    private val stateStore: TypedReduxStore<AppState, AppAction>
) {
    fun startSlideshow(currentFolder: FileModel.Folder) {
        stateStore.dispatch { dispatch, getState ->
            dispatch(AppAction.StartSlideshow(SlideshowOptions(currentFolder)))
        }
    }

    val appState get() = stateStore
    
    init {

        stateStore.dispatch { dispatch, getState ->
            AppScope.launch {
                runCatching {Model.getRootFolder() }
                    .onSuccess {
                        dispatch(AppAction.ReceivedRootFolder(it))
                    }
                    .onFailure {
                        dispatch(AppAction.FailedToReceiveRootFolder(it))
                    }
            }
        }
    }
}