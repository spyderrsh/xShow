package com.spyderrsh.xshow

import io.kvision.redux.TypedReduxStore
import kotlinx.coroutines.launch

class AppViewModel(
    appStateStoreProvider: AppStateStoreProvider
) {
    private val stateStore = appStateStoreProvider.get()
    
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