package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.ui.MediaView
import com.spyderrsh.xshow.ui.MediaViewCallbacks
import com.spyderrsh.xshow.ui.ShowLoading
import io.kvision.core.Container
import io.kvision.state.bind

private fun getViewModel(): SlideshowViewModel {
    return SlideShowScopeComponent().slideShowViewModel
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
                ShowItem(it.currentMedia)
                ShowOverlay(it.currentMedia)
            }
        }
    }

}

fun Container.ShowItem(media: FileModel.Media) {
    MediaView(media, callbacks)
}

fun Container.ShowOverlay(media: FileModel.Media) {

}
