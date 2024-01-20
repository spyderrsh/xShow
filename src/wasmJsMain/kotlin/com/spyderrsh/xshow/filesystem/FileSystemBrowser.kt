package com.spyderrsh.xshow.filesystem

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spyderrsh.xshow.AppState
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.util.painterResourceCached
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun FileSystemBrowser(appState: AppState, onPlayClick: () -> Unit) {
    val viewModel = remember { FileSystemBrowserScopeComponent.fileSystemBrowserViewModel }
    LaunchedEffect(appState.rootFolder) {
        appState.rootFolder?.let { viewModel.loadFolder(it) }
    }
    val fileSystemBrowserState by viewModel.state.collectAsState()
    FileSystemBrowser(
        fileSystemBrowserState,
        onFileClick = { viewModel.onFileClick(it) },
        onPlayClick = onPlayClick
    )
}

@Composable
fun FileSystemBrowser(state: FileSystemBrowserState, onFileClick: (FileModel) -> Unit, onPlayClick: () -> Unit) {
    // Initial Loading State
    if (state.isLoading && state.currentFiles.isEmpty() || state.currentFolder == null) {
        Box(Modifier.wrapContentSize()) {
            ShowLoading()
        }
        return
    }
    val parentFolder = remember(state.currentFolder) { state.currentFolder.parentFolder }
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (state.isLoading) {
                item { ShowLoading() }
            }
            item { FolderTitle(state.currentFolder) }
            if (parentFolder != null) {
                item { ShowParentFolder(parentFolder) { onFileClick(parentFolder) } }
            }
            items(state.currentFiles) {
                ShowFile(it, onClick = { onFileClick(it) })
            }
        }
        PlayButton(modifier = Modifier.align(Alignment.BottomEnd), onPlayClick)
    }

}

@Composable
fun PlayButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val playButtonIcon = painterResourceCached("assets/ic_play_128.png")
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        painter = playButtonIcon,
        contentDescription = "Play Button",
        modifier = Modifier.size(64.dp)
            .then(modifier)
            .clickable(interactionSource, indication = null, onClick = onClick)
    )
}

@Composable
fun ShowFile(model: FileModel, onClick: () -> Unit) {
    ShowFile(
        "${model.shortName}${
            if (model is FileModel.Media) {
                model.extension
            } else {
                ""
            }
        }", model, onClick
    )
}

@Composable
fun ShowFile(displayString: String, model: FileModel, onClick: () -> Unit) {
    when (model) {
        is FileModel.Media -> ShowMediaFile(displayString, model, onClick)
        is FileModel.Folder -> ShowFolderFile(displayString, model, onClick)
    }
}

@Composable
fun ImageWithTitle(imageContent: @Composable () -> Unit, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        imageContent()
        Spacer(Modifier.width(4.dp))
        Text(title)
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ShowFolderFile(displayString: String, model: FileModel.Folder, onClick: () -> Unit) {
    val vectorPainter = painterResourceCached("assets/ic_folder_128.png")
    ClickableRow(leftHandContent = {
        ImageWithTitle(
            { Image(vectorPainter, "folder icon", modifier = Modifier.size(24.dp)) },
            displayString
        )
    }) {
        onClick()
    }
}

// TODO move to design system
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClickableRow(leftHandContent: @Composable () -> Unit, onClick: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clickable(interactionSource, indication = null, onClick = onClick)
    ) { leftHandContent() }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ShowMediaFile(displayString: String, model: FileModel.Media, onClick: () -> Unit) {
    val videoPainter = painterResourceCached("assets/ic_video_128.png")
    val imagePainter = painterResourceCached("assets/ic_image_128.png")
    val isImage = remember { model is FileModel.Media.Image }
    ClickableRow(
        leftHandContent = {
            ImageWithTitle(
                {
                    Image(
                        if (isImage) {
                            imagePainter
                        } else {
                            videoPainter
                        },
                        contentDescription = if (isImage) {
                            "image icon"
                        } else "video icon",
                        modifier = Modifier.size(24.dp)
                    )
                },
                displayString
            )
        },
        onClick = onClick
    )
}

@Composable
fun ShowParentFolder(folder: FileModel.Folder, onClick: () -> Unit) {
    ShowFile("..", folder, onClick)
}

@Composable
fun FolderTitle(folder: FileModel.Folder) {
    Text(folder.serverPath, style = MaterialTheme.typography.h1)
}

//TODO move to util file
@Composable
fun ShowLoading() {
    Text("Loading...")
}
