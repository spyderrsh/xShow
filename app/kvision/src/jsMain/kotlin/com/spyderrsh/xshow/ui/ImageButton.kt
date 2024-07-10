package com.spyderrsh.xshow.ui

import io.kvision.core.Container
import io.kvision.core.CssSize
import io.kvision.core.onClick
import io.kvision.html.Image
import io.kvision.html.image

fun Container.ImageButton(
    src: String,
    width: CssSize = DEFAULT_ICON_SIZE,
    height: CssSize = DEFAULT_ICON_SIZE,
    onClick: () -> Unit = { },
    init: Image.() -> Unit = {}
) = image(src) {
    this.width = width
    this.height = height
    addCssClass("tint-icon")
    onClick {
        onClick()
    }
    init()

}