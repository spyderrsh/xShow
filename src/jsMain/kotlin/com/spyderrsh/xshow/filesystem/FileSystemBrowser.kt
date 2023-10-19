package com.spyderrsh.xshow.filesystem

import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.ui.ShowLoading
import com.spyderrsh.xshow.ui.UnsupportedState
import com.spyderrsh.xshow.ui.internalLink
import io.kvision.core.Container
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
        when (it) {
            is FileSystemBrowserState.ErrorState -> UnsupportedState(it)
            is FileSystemBrowserState.Files -> ShowFiles(it)
            is FileSystemBrowserState.LoadingFolder -> {
                ShowLoading()
                it.currentFiles?.let { files ->
                    ShowFiles(files)
                }
            }

            FileSystemBrowserState.Starting -> ShowLoading()
        }
    }
}

fun Container.ShowFiles(files: FileSystemBrowserState.Files) {
    span("Files For ${files.currentFolder.path}")
    vPanel {
        ShowParentFolder(files.currentFolder)
        files.files.forEach {
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
            else -> span("${file::class.simpleName}: ${file.shortName}${if (file is FileModel.Media) ".${file.extension}" else ""}")
        }
    }

}

private fun Container.ShowFolder(folder: FileModel.Folder) {
    span("Folder: ")
    internalLink(folder.shortName){
        onClick { getViewModel().loadFolder(folder) }
    }
}
