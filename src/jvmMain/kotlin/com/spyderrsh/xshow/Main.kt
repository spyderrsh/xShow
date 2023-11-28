package com.spyderrsh.xshow

import com.spyderrsh.xshow.db.DatabaseInitializer
import com.spyderrsh.xshow.db.MediaDbDataSource
import com.spyderrsh.xshow.media.DefaultMediaRepository
import com.spyderrsh.xshow.media.MediaRepository
import com.spyderrsh.xshow.model.XShowSession
import com.spyderrsh.xshow.scanner.DirectoryScanner
import com.spyderrsh.xshow.scanner.ScannerModule
import com.spyderrsh.xshow.service.FileSystemService
import com.spyderrsh.xshow.service.SlideshowService
import com.spyderrsh.xshow.service.filesystem.DefaultFilesystemRepository
import com.spyderrsh.xshow.service.filesystem.FilesystemRepository
import com.spyderrsh.xshow.slideshow.SlideshowModule
import com.spyderrsh.xshow.slideshow.SlideshowSessionManager
import com.spyderrsh.xshow.util.DefaultServerConfig
import com.spyderrsh.xshow.util.UtilModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.initStaticResources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.logger.slf4jLogger
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
fun Application.main() {
    install(Compression)
    install(Sessions) {
        cookie<XShowSession>("XSHOWSESSION", storage = SessionStorageMemory()) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "strict"
        }
    }
    install(PartialContent)

    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started")
        initializeDatabase()
        startDirAnalyzer()
    }

    environment.monitor.subscribe(DirectoryScanner.DirectoryScanFinished){
        startSlideshowSession()
    }

    val module = module {
        includes(ScannerModule, UtilModule, SlideshowModule)
        singleOf(::DefaultServerConfig) {
            createdAtStart()
            bind<ServerConfig>()
        }
        singleOf(::DefaultFilesystemRepository) {
            createdAtStart()
            bind<FilesystemRepository>()
        }
        singleOf(::DefaultMediaRepository) {
            createdAtStart()
            bind<MediaRepository>()
        }
        singleOf(::MediaDbDataSource) {
            createdAtStart()
        }
        singleOf(::DatabaseInitializer) {
            createdAtStart()
        }

        factoryOf(::PingService)
        factoryOf(::FileSystemService)
        factoryOf(::SlideshowService)

    }
    install(ContentNegotiation) {
        json(DefaultJson)
    }

    initStaticResources()

    install(Koin) {
        slf4jLogger(level = org.koin.core.logger.Level.ERROR)
        modules(KoinModule.applicationModule(this@main), module)
    }


    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
        addStaticRoutes()
    }
}

fun Application.startSlideshowSession() {
    log.info("Starting Slideshow Session Manager")
    val slideshowSessionManager by inject<SlideshowSessionManager>()
    slideshowSessionManager.initialize()
}

fun Application.startDirAnalyzer() {
    val scanner by inject<DirectoryScanner>()
    scanner.start()
}

private fun Routing.addStaticRoutes() {
    val config by inject<ServerConfig>()
    staticFiles(config.staticPath, File(DefaultServerConfig().rootFolderPath))
}

private fun Application.initializeDatabase() {
    val databaseInitializer by inject<DatabaseInitializer>()
    databaseInitializer.initialize()
}
