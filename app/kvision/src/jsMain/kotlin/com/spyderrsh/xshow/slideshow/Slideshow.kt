package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.AppComponent
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.ui.ImageButton
import com.spyderrsh.xshow.ui.MediaView
import com.spyderrsh.xshow.ui.MediaViewCallbacks
import com.spyderrsh.xshow.ui.ShowLoading
import io.kvision.core.*
import io.kvision.html.div
import io.kvision.panel.gridPanel
import io.kvision.state.bind
import kotlinx.browser.document

private fun getViewModel(): SlideshowViewModel {
    return SlideShowScopeComponent.slideShowViewModel
}

private val callbacks: MediaViewCallbacks = object : MediaViewCallbacks {
    override val onImageClick: (FileModel.Media.Image) -> Unit = { getViewModel().fetchNextItem() }
    override val onVideoClipEnd: (FileModel.Media.Video.Clip) -> Unit = { getViewModel().fetchNextItem() }
    override val onVideoEnd: (FileModel.Media.Video) -> Unit = { getViewModel().fetchNextItem() }
}

fun Container.Slideshow() {
    val viewModel = getViewModel()
    viewModel.fetchNextItem()
    bind(viewModel.state) {
        when {
            it.currentMedia == null -> ShowLoading()
            else -> {
                ShowItem(it.currentMedia!!)
                ShowOverlay(it.currentMedia!!)
            }
        }
        setFullscreen(it.fullscreen)
    }

}

fun Container.setFullscreen(isFullscreen: Boolean) {
    if (isFullscreen && !document.fullscreen) {
        document.documentElement?.apply {
            requestFullscreen()
        }
    } else if (!isFullscreen && document.fullscreen) {
        document.exitFullscreen()
    }
}

fun Container.ShowItem(media: FileModel.Media) {
    MediaView(media, callbacks)
}

fun Container.ShowOverlay(media: FileModel.Media) {
    ShowExitButton()
    ShowBottomRightButtons(media)
}

fun Container.ShowExitButton() {
    div {
        position = Position.ABSOLUTE
        this.left = CssSize(16, UNIT.pt)
        this.top = CssSize(16, UNIT.pt)
        ExitButton()
    }
}

fun Container.ShowBottomRightButtons(media: FileModel.Media) {

    gridPanel(columnGap = 16, rowGap = 16, justifyItems = JustifyItems.CENTER) {
        position = Position.ABSOLUTE
        this.right = CssSize(16, UNIT.pt)
        this.bottom = CssSize(16, UNIT.pt)
        // Top Row
        var row = 1
        options(1, row) {
            div {
                DeleteButton(media)
            }
        }
        options(2, row) {
            div {
                FullscreenButton()
            }
        }
        ++row
        options(1, row) {
            div {
                PreviousMediaButton()
            }
        }
        options(2, row) {
            div {
                NextMediaButton()
            }
        }
    }
}

fun Container.PreviousMediaButton() {
    ImageButton("assets/previous.svg",
        onClick = {
            getViewModel().fetchPreviousItem()
        })
}

fun Container.NextMediaButton() {
    ImageButton("assets/next.svg",
        onClick = {
            getViewModel().fetchNextItem()
        })
}

fun Container.DeleteButton(media: FileModel.Media) {
    ImageButton("assets/delete.svg", onClick = { getViewModel().deleteItem(media) })
}

fun Container.FullscreenButton() {
    ImageButton("assets/fullscreen.svg", onClick = {
        getViewModel().toggleFullscreen()
    })
}

fun Container.ExitButton() {
    ImageButton("assets/exit.svg", onClick = {
        AppComponent.appViewModel.exitSlideshow()
    })
}
