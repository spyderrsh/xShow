package com.spyderrsh.xshow.model

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
sealed interface FileModel {
    val path: String
    val serverPath: String
    val shortName: String

    @Serializable
    data class Folder(
        override val path: String,
        override val shortName: String,
        override val serverPath: String,
        val parentFolder: Folder?
    ) : FileModel

    @Serializable
    sealed interface Media : FileModel {
        val extension: String

        @Serializable
        data class Image(
            override val path: String,
            override val serverPath: String,
            override val shortName: String,
            override val extension: String
        ) : Media

        @Serializable
        sealed interface Video : Media {
            val duration: Duration?

            @Serializable
            data class Full(
                override val path: String,
                override val shortName: String,
                override val serverPath: String,
                override val extension: String,
                override val duration: Duration?
            ) : Video

            @Serializable
            data class Clip(val parent: Full, val startTime: Int, val endTime: Int) : Video {

                override val duration: Duration = (endTime - startTime).seconds

                override val path: String
                    get() = parent.path
                override val serverPath: String
                    get() = TODO("Not yet implemented")
                override val shortName: String
                    get() = "${parent.shortName}:$startTime-$endTime"
                override val extension: String
                    get() = parent.extension
            }
        }
    }

}