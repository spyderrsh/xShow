@file:OptIn(ExperimentalResourceApi::class)

package com.spyderrsh.xshow.slideshow

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Precision
import coil3.size.Scale
import coil3.size.SizeResolver
import com.spyderrsh.xshow.AppState
import com.spyderrsh.xshow.component.XShowVideo
import com.spyderrsh.xshow.filesystem.ShowLoading
import com.spyderrsh.xshow.generated.resources.*
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.painterResourceCached
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

fun enterFullscreen(): Unit =
    js(
        """{var elem = document.documentElement;

if (elem.requestFullscreen) {
  elem.requestFullscreen();
} else if (elem.webkitRequestFullscreen) { 
  elem.webkitRequestFullscreen();
} else if (elem.msRequestFullscreen) {
  elem.msRequestFullscreen();
}}"""
    )

fun exitFullscreen(): Unit =
    js(
        """{if (document.exitFullscreen) {
  document.exitFullscreen();
} else if (document.webkitExitFullscreen) { 
  document.webkitExitFullscreen();
} else if (document.msExitFullscreen) {
  document.msExitFullscreen();
  }}"""
    )

fun isFullscreen(): Boolean =
    js("""document.fullscreen""")

@Composable
fun Slideshow(appState: AppState, onCloseSlideshowClick: () -> Unit) {
    val viewModel = remember { SlideShowScopeComponent.slideShowViewModel }
    val slideshowState by viewModel.state.collectAsState()
    Slideshow(
        slideshowState,
        onNextClick = { viewModel.fetchNextItem() },
        onPreviousClick = { viewModel.fetchPreviousItem() },
        onDeleteClick = { viewModel.deleteItem(it) },
        onFullscreenClick = { viewModel.toggleFullscreen() },
        onCloseSlideshowClick = onCloseSlideshowClick
    )
}

@Composable
fun Slideshow(
    state: SlideshowState,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onDeleteClick: (FileModel.Media) -> Unit,
    onFullscreenClick: () -> Unit,
    onCloseSlideshowClick: () -> Unit
) {
    val isFullscreen = state.fullscreen
    val currentMedia = state.currentMedia
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(isFullscreen) {
        if (isFullscreen && !isFullscreen()) {
            enterFullscreen()
        } else if (!isFullscreen && isFullscreen()) {
            exitFullscreen()
        }
    }
    Box(
        Modifier.fillMaxSize()
            .background(Color.Transparent)

    ) {
        when (currentMedia) {
            is FileModel.Media.Image -> SlideshowShowImage(currentMedia, onNextClick)
            is FileModel.Media.Video -> SlideshowShowVideo(currentMedia)
            null -> ShowLoading()
        }
        if (currentMedia != null) {
            SlideshowControls(
                Modifier.align(Alignment.BottomEnd).padding(16.dp),
                onNextClick,
                onPreviousClick,
                { onDeleteClick(currentMedia) },
                onFullscreenClick
            )
        }
        ExitSlideshow(Modifier.align(Alignment.TopStart).padding(16.dp), onCloseSlideshowClick)
    }
}

@Composable
fun ExitSlideshow(modifier: Modifier, onClick: () -> Unit) {
    Box(modifier = modifier) {
        SlideshowButton(Res.drawable.ic_exit_128, onClick)
    }
}

@Composable
fun SlideshowControls(
    modifier: Modifier,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFullscreenClick: () -> Unit
) {
    Column(modifier = modifier) {
        Row {
            SlideshowButton(Res.drawable.ic_delete_128, onDeleteClick)
            SlideshowButton(Res.drawable.ic_fullscreen_128, onFullscreenClick)
        }
        Row {
            SlideshowButton(Res.drawable.ic_previous_128, onPreviousClick)
            SlideshowButton(Res.drawable.ic_next_128, onNextClick)
        }
    }
}

@Composable
fun SlideshowButton(asset: DrawableResource, onClick: () -> Unit) {
    val painter = painterResourceCached(asset)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier.size(40.dp)
            .clickable(interactionSource, indication = LocalIndication.current, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun SlideshowShowVideo(video: FileModel.Media.Video) {
    XShowVideo(video.serverPath, Modifier.fillMaxSize())
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SlideshowShowImage(image: FileModel.Media.Image, onNextClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data("http://192.168.1.84:8080/${image.serverPath}")
            .precision(Precision.EXACT)
            .scale(Scale.FILL)
            .size(SizeResolver.ORIGINAL).build(),
        filterQuality = FilterQuality.Medium,
        contentDescription = image.shortName,
        modifier = Modifier.fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onNextClick
            ),
        onState = ::println
    )

}
