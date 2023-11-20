package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.XShowSession
import com.spyderrsh.xshow.service.FileSystemService
import com.spyderrsh.xshow.service.SlideshowService
import com.spyderrsh.xshow.service.filesystem.DefaultFilesystemRepository
import com.spyderrsh.xshow.service.filesystem.FilesystemRepository
import com.spyderrsh.xshow.util.DefaultFileModelUtil
import com.spyderrsh.xshow.util.DefaultServerConfig
import com.spyderrsh.xshow.util.XShowFileModelUtil
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit
import io.netty.util.internal.ResourcesUtil
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.File

fun Application.main() {
    install(Compression)
    install(Sessions) {
        cookie<XShowSession>("XSHOWSESSION", storage = SessionStorageMemory()) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "strict"
        }
    }

    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
        addStaticRoutes()
    }

    val module = module {
        singleOf(::DefaultServerConfig){
            bind<ServerConfig>()
        }
        singleOf(::DefaultFileModelUtil){
            bind<XShowFileModelUtil>()
        }
        singleOf(::DefaultFilesystemRepository){
            bind<FilesystemRepository>()
        }
        factoryOf(::PingService)
        factoryOf(::FileSystemService)
        factoryOf(::SlideshowService)

    }
    kvisionInit(module)
}

private fun Routing.addStaticRoutes() {
    // TODO Figure out how to use the same DefaultServerConfig from Koin
    val config = DefaultServerConfig()
    staticFiles(config.staticPath, File(DefaultServerConfig().rootFolderPath))
}
