package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.AppComponent
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.ui.ShowLoading
import com.spyderrsh.xshow.ui.UnsupportedState
import com.spyderrsh.xshow.ui.internalLink
import io.kvision.core.Container
import io.kvision.core.CssSize
import io.kvision.core.UNIT
import io.kvision.core.onClick
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.bind
private fun getViewModel(): FileSystemBrowserViewModel {
    return FileSystemBrowserScopeComponent().fileSystemBrowserViewModel
}
fun Container.FileSystemBrowser(folder: FileModel.Folder) {
    val viewModel = getViewModel()
    viewModel.loadFolder(folder)
    bind(viewModel.state) {
        if(it.errors.isNotEmpty())
            UnsupportedState(it)

        if(it.loadingFolder != null) {
            ShowLoading()
        }

        if(it.currentFiles.isNotEmpty() || !it.isLoading)
            ShowFiles(it)

    }
}

fun Container.ShowFiles(state: FileSystemBrowserState) {
    PlayButton()
    span("Files For ${state.currentFolder?.path}")
    vPanel {
        if(state.currentFolder != null){
            ShowParentFolder(state.currentFolder)
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
    internalLink("${file::class.simpleName}: ${file.shortName}.${file.extension}", url = file.serverPath)
}
private fun Container.ShowFolder(folder: FileModel.Folder) {
    span("Folder: ")
    internalLink(folder.shortName){
        onClick { getViewModel().loadFolder(folder) }
    }
}
private fun Container.PlayButton() {
    div {
            image("assets/play.svg"){
                width = CssSize(24, UNIT.pt)
                height = CssSize(24, UNIT.pt)
                addCssClass("tint-icon")
                onClick {
                    AppComponent().appViewModel.startSlideshow(getViewModel().state.getState().currentFolder!!)
                }
            }
    }
}
