package com.spyderrsh.xshow.slideshow

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.media.MediaRepository
import com.spyderrsh.xshow.model.FileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.logging.Logger
import kotlin.time.Duration.Companion.seconds

class DefaultSlideshowSessionManager(
    private val mediaRepository: MediaRepository,
    private val config: ServerConfig,
) : SlideshowSessionManager {
    private val scope = CoroutineScope(SupervisorJob())
    private var position: Int = 0
    private val _slideshowMedia = mutableListOf<FileModel.Media>()


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
        ++position
        // get Item
        return currentItem()
    }

    private fun currentItem(): FileModel.Media {
        return _slideshowMedia[position % _slideshowMedia.count()]
    }

    override fun getPreviousItem(): FileModel.Media {
        // Decrement counter
        --position
        // Get Item
        return currentItem()
    }
}