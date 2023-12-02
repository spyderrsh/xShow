package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.media.MediaRepository
import com.spyderrsh.xshow.model.FileModel
import com.spyderrsh.xshow.service.filesystem.FilesystemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.logging.Logger
import kotlin.time.Duration.Companion.seconds

class DefaultSlideshowSessionManager(
    private val mediaRepository: MediaRepository,
    private val config: ServerConfig,
    private val filesystemRepository: FilesystemRepository
) : SlideshowSessionManager {
    private val scope = CoroutineScope(SupervisorJob())
    private var _position: Int = 0
    private val _slideshowMedia = mutableListOf<FileModel.Media>()
    private val position get() = Math.floorMod(_position, _slideshowMedia.size)


    override fun initialize() {
        resetSession()
        addAllItemsToSlideshow()

    }

    private fun addAllItemsToSlideshow() {
        scope.launch {
            mediaRepository.getMedia(
                rootPath = config.rootFolderPath,
                images = true,
                videos = true,
                clips = true,
                clipDuration = 30.seconds
            ).onSuccess {
                Logger.getGlobal().info("Adding ${it.size} items to slideshow session")
                _slideshowMedia.addAll(it)
                _slideshowMedia.shuffle()
                Logger.getGlobal().info("Media Shuffled")
            }.onFailure {
                Logger.getGlobal().severe("Issue adding media!!!")
                Logger.getGlobal().throwing(this::class.simpleName, "addAllItemsToSlideshow", it)
            }
        }
    }

    private fun resetSession() {
        _slideshowMedia.clear()
    }

    override fun getNextItem(): FileModel.Media {
        // Increment counter
        ++_position
        // get Item
        while (!checkCurrentItem() && _slideshowMedia.size > 0) {
            // no-op
            // checkCurrentItem will delete the item in place bringing the
            // next item into _position
        }

        return currentItem()
    }


    private fun checkCurrentItem(): Boolean {
        val currentItem = currentItem()
        if (filesystemRepository.doesItemExist(currentItem()))
            return true
        // TODO this probably doesn't work correctly with getPreviousItem()
        deleteItemAndSubitemsFromSlideshow(currentItem)
        scope.launch {
            deleteItemFromDatabase(currentItem)
        }
        return false
    }

    private fun currentItem(): FileModel.Media {
        return _slideshowMedia[position]
    }

    override fun getPreviousItem(): FileModel.Media {
        // Decrement counter
        --_position
        while (!checkCurrentItem() && _slideshowMedia.size > 0) {
            // If previous item is deleted, the next item will be brought into _position
            // so we decrement again
            --_position
        }
        // Get Item
        return currentItem()
    }

    override suspend fun deleteItem(media: FileModel.Media) {
        if (media is FileModel.Media.Video.Clip) {
            return deleteItem(media.parent)
        }
        deleteItemAndSubitemsFromSlideshow(media)
        deleteItemFromHdd(media)
        deleteItemFromDatabase(media)

    }

    private suspend fun deleteItemFromDatabase(media: FileModel.Media) {
        newSuspendedTransaction {
            mediaRepository.deleteItem(this, media)
        }
    }

    private fun deleteItemAndSubitemsFromSlideshow(toDelete: FileModel.Media) {
        when (toDelete) {
            is FileModel.Media.Video.Full -> deleteVideoFromSlideshow(toDelete)
            is FileModel.Media.Video.Clip -> deleteVideoFromSlideshow(toDelete.parent)
            is FileModel.Media.Image -> deleteItemFromSlideshow(toDelete)
        }
    }

    private fun deleteItemFromSlideshow(toDelete: FileModel.Media) {
        synchronized(_slideshowMedia) {
            val indexToDelete = _slideshowMedia.indexOfFirst {
                it.serverPath == toDelete.serverPath
            }.takeIf { it >= 0 } ?: return

            if (indexToDelete <= position) {
                --_position
            }
            _slideshowMedia.removeAt(indexToDelete).also {
                if (it.serverPath != toDelete.serverPath) {
                    throw IllegalStateException("Removed item $it did not match $toDelete")
                }
            }
        }

    }

    private fun deleteVideoFromSlideshow(toDelete: FileModel.Media.Video.Full) {
        _slideshowMedia.filter {
            it.path == toDelete.path
        }.forEach {
            deleteItemFromSlideshow(it)
        }
    }

    private suspend fun deleteItemFromHdd(media: FileModel.Media) {
        filesystemRepository.deleteFile(media)
    }
}