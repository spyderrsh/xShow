package com.spyderrsh.xshow.ui

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.p
import io.kvision.html.span

fun Container.ShowLoading() {
    div("Loading")
}

fun Container.UnsupportedState(state: Any) {
    div("State is not yet supported: $state")
}