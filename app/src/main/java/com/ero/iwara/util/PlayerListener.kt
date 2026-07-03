package com.ero.iwara.util

import androidx.media3.common.Player
import androidx.media3.common.VideoSize

class PlayerListener(
    private val position: ((Long)->Unit)? = null,
    private val duration: ((Long)->Unit)? = null,
    private val buffer: ((Long)->Unit)? = null,
    private val playing: ((Boolean)->Unit)? = null,
    private val loading: ((Boolean)->Unit)? = null,
    private val size: ((Int, Int)->Unit)? = null,
): Player.Listener
{
    override fun onRenderedFirstFrame() {
        loading?.invoke(false)
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        buffer?.invoke(player.bufferedPosition)
        position?.invoke(player.currentPosition)
        duration?.invoke(player.duration)
        playing?.invoke(player.isPlaying)
        loading?.invoke(player.isLoading)
    }
    override fun onVideoSizeChanged(videoSize: VideoSize) {
        size?.invoke(videoSize.width, videoSize.height)
    }
}