package com.spyderrsh.xshow.redux

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface TypedReduxStore<State, Action> {

    fun dispatch(action: Action)
    fun dispatch(block: (dispatch: (Action) -> Unit, getState: () -> State) -> Unit)
    val stateFlow: StateFlow<State>

}

fun <State, Action> createTypedReduxStore(
    reducer: (State, Action) -> State,
    initialState: State
): TypedReduxStore<State, Action> {
    return TypedReduxStoreInternal<State, Action>(reducer, initialState)
}

class TypedReduxStoreInternal<State, Action>(
    private val reducer: (State, Action) -> State,
    initialState: State
) : TypedReduxStore<State, Action> {
    private val state = MutableStateFlow(initialState)
    override val stateFlow: StateFlow<State>
        get() = state

    override fun dispatch(action: Action) {
        state.update { value -> reducer(value, action) }
    }

    override fun dispatch(block: (dispatch: (Action) -> Unit, getState: () -> State) -> Unit) {
        return block({ action: Action -> state.update { value -> reducer(value, action) } }, { state.value })
    }
}
