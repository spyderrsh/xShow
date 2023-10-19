package com.spyderrsh.xshow.util

import com.spyderrsh.xshow.model.FileModel
import java.io.File

interface XShowFileModelUtil {
    val ImageExtensions: List<String>
    val VideoExtensions: List<String>
    val UnusedExtensions: MutableSet<String>
    fun createFolderFromPath(path: String, parentFolder: FileModel.Folder? = null): FileModel.Folder
    fun createFolderFromFile(file: File, parentFolder: FileModel.Folder?): FileModel.Folder
    fun createImageFromFile(file: File): FileModel.Media.Image
    fun createVideoFromFile(file: File): FileModel.Media.Video

    fun getFolderContents(folder: FileModel.Folder): List<FileModel>
    fun getFolderMedia(folder: FileModel.Folder): List<FileModel.Media> =
        getFolderContents(folder).filterIsInstance<FileModel.Media>()

}