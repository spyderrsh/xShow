package com.spyderrsh.xshow.slideshow

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.spyderrsh.xshow.AppState
import com.spyderrsh.xshow.filesystem.ShowLoading
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.painterResourceCached
import org.jetbrains.compose.resources.*

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

fun playVideo(serverPath: String, videoContainerTag: String = "content"): Unit = js(
    """{
           const videoDiv = document.getElementById(videoContainerTag);
           const oldVideo = document.getElementById("play-video");
           if(oldVideo) {
               oldVideo.remove();
           }
           const node = document.createElement("video");
           videoDiv.setAttribute("class", "fill-container center-in-page");
           node.setAttribute("class", "fill-container center-in-page");
           node.setAttribute("autoplay","");
           node.setAttribute("controls","");
           node.setAttribute("src",serverPath);
           node.setAttribute("id","play-video");
           videoDiv.insertBefore(node, videoDiv.children[0]);
        }
    """
)

private val emptyImageBitmap: ImageBitmap by lazy { ImageBitmap(1, 1) }

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
    Box(Modifier.fillMaxSize().background(Color(0x2200ff00))) {
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
        SlideshowButton("exit", onClick)
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
            SlideshowButton("delete", onDeleteClick)
            SlideshowButton("fullscreen", onFullscreenClick)
        }
        Row {
            SlideshowButton("previous", onPreviousClick)
            SlideshowButton("next", onNextClick)
        }
    }
}

@Composable
fun SlideshowButton(assetName: String, onClick: () -> Unit) {
    val painter = painterResourceCached("assets/ic_${assetName}_128.png")
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        Modifier.size(40.dp)
            .clickable(interactionSource, indication = LocalIndication.current, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter,
            contentDescription = assetName,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun SlideshowShowVideo(video: FileModel.Media.Video) {
//    Text("Video currently not supported $video")
    playVideo(video.serverPath)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SlideshowShowImage(image: FileModel.Media.Image, onNextClick: () -> Unit) {
    val urlResource = remember(image.serverPath) { urlResource(image.serverPath) }
    val rib = urlResource.rememberImageBitmap()
    var isError by remember { mutableStateOf(false) }
    var painter by remember { mutableStateOf(BitmapPainter(emptyImageBitmap)) }
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(image.serverPath, rib) {
        when (rib) {
            is LoadState.Success -> {
                painter = BitmapPainter(rib.orEmpty())
            }

            is LoadState.Loading -> {
                isError = false
            }

            is LoadState.Error -> {
                painter = BitmapPainter(emptyImageBitmap)
                isError = true
            }
        }
    }
    if (isError) {
        Text("Issue loading image: $image")
    } else {
        Image(
            painter,
            image.shortName,
            modifier = Modifier.fillMaxSize().clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onNextClick
            )
        )
    }
}
