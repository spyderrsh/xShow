package com.spyderrsh.xshow.util

import com.spyderrsh.xshow.ServerConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.util.logging.Logger

@OptIn(ExperimentalSerializationApi::class)
class DefaultServerConfig : ServerConfig by Json.decodeFromStream<JsonServerConfig>(
    DefaultServerConfig::class.java.getResourceAsStream("/config.json")!!
) {
    init {
        Logger.getLogger("ServerConfig").config("Got Server config: $this")
    }
}

@Serializable
private data class JsonServerConfig(
    @SerialName("root") override val rootFolderPath: String,
    @SerialName("static_path") override val staticPath: String,
    @SerialName("db_path") override val dbPath: String
) : ServerConfig