package com.spyderrsh.xshow.ui

import io.kvision.core.Container
import io.kvision.core.ResString
import io.kvision.html.Link


/**
 * DSL builder extension function.
 *
 * It takes the same parameters as the constructor of the built component.
 */
fun Container.internalLink(
    label: String, url: String? = null, icon: String? = null, image: ResString? = null,
    separator: String? = null, labelFirst: Boolean = true, target: String? = null, dataNavigo: Boolean? = null,
    className: String = "internal",
    init: (Link.() -> Unit)? = null
): Link {
    val link =
        Link(
            label,
            url,
            icon,
            image,
            separator,
            labelFirst,
            target,
            dataNavigo,
            className,
            init
        )
    this.add(link)
    return link
}