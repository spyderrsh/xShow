package com.spyderrsh.xshow.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hamama.kwhi.HtmlView
import com.spyderrsh.app.generated.resources.Res
import com.spyderrsh.app.generated.resources.ic_play_128
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.dom.addClass
import org.jetbrains.compose.resources.painterResource
import org.w3c.dom.HTMLVideoElement

enum class TimeUpdateSource {
    User,
    Video
}

data class TimeUpdate(val currentTime: Double, val source: TimeUpdateSource)

data class VideoPlaybackData(
    val isPaused: Boolean,
    val timeData: TimeUpdate?,
    val duration: Double?
)

@Composable
fun XShowVideo(path: String, modifier: Modifier) {
    val videoData =
        remember { MutableStateFlow(VideoPlaybackData(isPaused = true, timeData = null, duration = null)) }
    val isPaused = remember(videoData) { videoData.map { it.isPaused } }
    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = modifier
        .clickable(interactionSource, indication = null) {
            videoData.update { it.copy(isPaused = !it.isPaused) }
        }) {
        InnerVideo(path, videoData)
        PauseButton(isPaused, Modifier.align(Alignment.Center))
        VideoControls(videoData, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun VideoControls(videoPlaybackData: MutableStateFlow<VideoPlaybackData>, modifier: Modifier) {
    val duration by videoPlaybackData.map { it.duration }.distinctUntilChanged().collectAsState(null)
    val currentPosition by videoPlaybackData.map { it.timeData?.currentTime?.toFloat() }.distinctUntilChanged()
        .collectAsState(null)
//    val userSliderValue: MutableState<Float?> = remember { mutableStateOf(null) }
    Row(modifier = modifier.fillMaxWidth()) {
        currentPosition?.let { time ->
            duration?.let { totalDuration ->
                Slider(
                    value = time,
                    onValueChange = { newTime ->
                        videoPlaybackData.update {
                            it.copy(
                                timeData = TimeUpdate(
                                    newTime.toDouble(),
                                    TimeUpdateSource.User
                                )
                            )
                        }
                    },
//                    onValueChangeFinished = {
//                        videoPlaybackData.update {
//                            it.copy(
//                                timeData = TimeUpdate(
//                                    userSliderValue.value!!.toDouble(),
//                                    TimeUpdateSource.User
//                                )
//                            )
//                        }
//                        userSliderValue.value = null
//                    },
                    valueRange = 0f..totalDuration.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PauseButton(paused: Flow<Boolean>, modifier: Modifier) {
    val isPaused by paused.collectAsState(false)
    if (isPaused) {
        Image(
            painter = painterResource(Res.drawable.ic_play_128),
            contentDescription = "Play",
            modifier = modifier
        )
    }
}

@Composable
private fun InnerVideo(path: String, videoPlaybackData: MutableStateFlow<VideoPlaybackData>) {
    val scope = rememberCoroutineScope()
    HtmlView(
        factory = { createElement("video") },
        update = { videoElement ->
            if (videoElement is HTMLVideoElement) {
                videoElement.setAttribute("src", path)
                videoElement.setAttribute("autoplay", "true")
                videoElement.addClass("no-touch")
                videoElement.addEventListener("play") {
                    videoPlaybackData.update { it.copy(isPaused = false) }
                }
                videoElement.addEventListener("pause") {
                    videoPlaybackData.update { it.copy(isPaused = true) }
                }
                videoElement.addEventListener("timeupdate") {
                    videoPlaybackData.update {
                        it.copy(
                            timeData = TimeUpdate(
                                videoElement.currentTime,
                                TimeUpdateSource.Video
                            )
                        )
                    }
                }
                videoElement.addEventListener("loadedmetadata") {
                    videoPlaybackData.update { it.copy(duration = videoElement.duration) }
                }
                scope.launch {
                    videoPlaybackData.map { it.isPaused }.distinctUntilChanged().drop(1)
                        .collect { if (it) videoElement.pause() else videoElement.play() }
                }
                scope.launch {
                    videoPlaybackData.map { it.timeData }
                        .filterNotNull()
                        .distinctUntilChanged()
                        .filter { it.source == TimeUpdateSource.User }
                        .collect { timeUpdate ->
                            videoElement.currentTime = timeUpdate.currentTime
                        }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
