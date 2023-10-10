package com.spyderrsh.xshow

import com.spyderrsh.xshow.model.XShowSession
import com.spyderrsh.xshow.service.FileSystemService
import com.spyderrsh.xshow.service.SlideshowService
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

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
    }
    val module = module {
        factoryOf(::PingService)
        factoryOf(::FileSystemService)
        factoryOf(::SlideshowService)

    }
    kvisionInit(module)
}
