package com.spyderrsh.xshow

import io.kvision.redux.TypedReduxStore
import io.kvision.redux.createTypedReduxStore

class AppStateStoreProvider {
    private var appStateStore: TypedReduxStore<AppState, AppAction>? = null
    fun get() = appStateStore ?: createTypedReduxStore(::appReducer, AppState.Starting).also {
        appStateStore = it
    }
}
