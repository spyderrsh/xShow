package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.AppComponent
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.ui.MediaView
import com.spyderrsh.xshow.ui.ShowLoading
import com.spyderrsh.xshow.ui.UnsupportedState
import com.spyderrsh.xshow.ui.internalLink
import io.kvision.core.*
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.html.span
import io.kvision.modal.Modal
import io.kvision.modal.ModalSize
import io.kvision.panel.vPanel
import io.kvision.state.bind
import io.kvision.utils.event

private fun getViewModel(): FileSystemBrowserViewModel {
    return FileSystemBrowserScopeComponent.fileSystemBrowserViewModel
}

fun Container.FileSystemBrowser(folder: FileModel.Folder) {
    val viewModel = getViewModel()
    viewModel.loadFolder(folder)
    bind(viewModel.state) {
        if (it.errors.isNotEmpty())
            UnsupportedState(it)

        if (it.loadingFolder != null) {
            ShowLoading()
        }

        if (it.currentFiles.isNotEmpty() || !it.isLoading)
            ShowFiles(it)

    }
    MediaModal()
}

fun MediaModal() {
    Modal(closeButton = false, size = ModalSize.XLARGE) {
        onEvent {
            this.event("hidden.bs.modal") { getViewModel().hideFileOverlay() }
            this.event("hide.bs.modal") { getViewModel().hideFileOverlay() }

        }
        bind(getViewModel().overlayState) {

            if(it== null){
                hide()
            }else {
                MediaView(it)
                show()
            }
        }
    }
}

fun Container.ShowFiles(state: FileSystemBrowserState) {
    PlayButton()
    span("Files For ${state.currentFolder?.path}")
    vPanel {
        if (state.currentFolder != null) {
            ShowParentFolder(state.currentFolder!!)
        }
        state.currentFiles.forEach {
            ShowFile(it)
        }
    }
}

private fun Container.ShowParentFolder(folder: FileModel.Folder) {
    folder.parentFolder?.let { parent ->
        ShowFile(parent.copy(shortName = ".."))
    }
}

fun Container.ShowFile(file: FileModel) {
    div {
        when (file) {
            is FileModel.Folder -> ShowFolder(file)
            is FileModel.Media -> ShowMedia(file)
        }
    }

}

private fun Container.ShowMedia(file: FileModel.Media) {
    internalLink("${file::class.simpleName}: ${file.shortName}.${file.extension}") {
        onClick {
            getViewModel().showFileOverlay(file)
        }
    }
}

private fun Container.ShowFolder(folder: FileModel.Folder) {
    span("Folder: ")
    internalLink(folder.shortName) {
        onClick { getViewModel().loadFolder(folder) }
    }
}

private fun Container.PlayButton() {
    div {
        image("assets/play.svg") {
            width = CssSize(24, UNIT.pt)
            height = CssSize(24, UNIT.pt)
            addCssClass("tint-icon")
            onClick {
                AppComponent.appViewModel.startSlideshow(getViewModel().state.value.currentFolder!!)
            }
        }
    }
}
