package com.spyderrsh.xshow.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface FileModel {
    val path: String

    @Serializable
    data class Folder(override val path: String) : FileModel

    @Serializable
    sealed interface Media : FileModel {
        @Serializable
        data class Image(override val path: String) : Media

        @Serializable
        sealed interface Video : Media {
            @Serializable
            data class Full(override val path: String) : Video

            @Serializable
            data class Clip(override val path: String, val startTime: Int, val endTime: Int) : Video
        }
    }

}