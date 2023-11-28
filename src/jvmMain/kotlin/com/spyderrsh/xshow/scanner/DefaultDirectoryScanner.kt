package com.spyderrsh.xshow.scanner

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.util.XShowDispatchers
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.sql.Connection
import java.util.logging.Logger

class DefaultDirectoryScanner(
    private val dispatchers: XShowDispatchers,
    private val config: ServerConfig,
    private val fileProcessor: FileProcessor,
    private val videoProcessor: VideoProcessor,
    private val folderProcessor: DirectoryProcessor,
    private val imageProcessor: ImageProcessor,
    private val application: Application
) : DirectoryScanner {
    private val coroutineScope = CoroutineScope(SupervisorJob() + dispatchers.scanner)
    override fun start() {
        coroutineScope.launch {
            startInternal()
        }
    }

    private suspend fun startInternal() {

        val folderChannel = Channel<File>(1000)
        val videoChannel = Channel<File>(1000)
        val imageChannel = Channel<File>(1000)
        newSuspendedTransaction(context = dispatchers.scanner,
            transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED) {
            // Add files to a queue
            val walkerJob = coroutineScope.launch {
                startFileWalker(folderChannel, videoChannel, imageChannel)
            }
            // Process files
            val videoJob = coroutineScope.launch {
                startVideoProcessor(this@newSuspendedTransaction, videoChannel)
                Logger.getGlobal().info("Video processing done")
            }

            val imageJob = coroutineScope.launch {
                startImageProcessor(this@newSuspendedTransaction, imageChannel)
                Logger.getGlobal().info("Image processing done")
            }

            val folderJob = coroutineScope.launch {
                startFolderProcessor(this@newSuspendedTransaction, folderChannel)
                Logger.getGlobal().info("Folder processing done")
            }

            joinAll(walkerJob, videoJob, imageJob, folderJob)
        }
        Logger.getGlobal().info("All Jobs done!!!")
        // Save video info to database
        // Load into set for slideshow
        application.environment.monitor.raise(DirectoryScanner.DirectoryScanFinished, Unit)
    }

    private suspend fun startFolderProcessor(transaction: Transaction, folderChannel: Channel<File>) {
        folderChannel.receiveAsFlow().onEach { folderProcessor.process(transaction, it) }.flowOn(dispatchers.scanner).collect {}
    }

    private suspend fun startImageProcessor(transaction: Transaction, imageChannel: Channel<File>) {
        imageChannel.receiveAsFlow().onEach { imageProcessor.process(transaction, it) }.flowOn(dispatchers.scanner).collect {}
    }

    private suspend fun startVideoProcessor(transaction: Transaction, videoChannel: Channel<File>) {
        videoChannel.receiveAsFlow().onEach { videoProcessor.process(transaction, it) }.flowOn(dispatchers.scanner).collect {}
    }

    private suspend fun startFileWalker(
        folderChannel: Channel<File>,
        videoChannel: Channel<File>,
        imageChannel: Channel<File>
    ) {
        val root = File(config.rootFolderPath)
        root.walkBottomUp().forEach {
            fileProcessor.groupByMediaType(it, folderChannel, videoChannel, imageChannel)
        }
        videoChannel.close()
        imageChannel.close()
        folderChannel.close()
    }
}