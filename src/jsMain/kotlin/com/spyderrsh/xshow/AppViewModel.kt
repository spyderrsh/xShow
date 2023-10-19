package com.spyderrsh.xshow

import io.kvision.redux.TypedReduxStore
import kotlinx.coroutines.launch

class AppViewModel(
    private val stateStore: TypedReduxStore<AppState, AppAction>
) {

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