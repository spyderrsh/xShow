package com.spyderrsh.xshow.ui

import io.kvision.core.Container
import io.kvision.html.div

fun Container.ShowLoading() {
    div("Loading")
}

fun Container.UnsupportedState(state: Any) {
    div("State is not yet supported: $state")
}