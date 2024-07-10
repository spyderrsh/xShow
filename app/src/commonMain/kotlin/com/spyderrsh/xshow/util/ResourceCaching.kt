@file:OptIn(ExperimentalResourceApi::class)

package com.spyderrsh.xshow.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


private val cache = mutableStateMapOf<DrawableResource, Painter>()

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun painterResourceCached(res: DrawableResource): Painter {
    return painterResource(res)
}