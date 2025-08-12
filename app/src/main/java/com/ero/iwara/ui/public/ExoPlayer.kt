package com.ero.iwara.ui.public

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ero.iwara.util.cache

private const val TAG = "ExoPlayerCompose"

@Composable
fun ExoPlayer3(modifier: Modifier = Modifier, videoLink: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).cache(context).build().apply {
            playWhenReady = true
        }
    }
    LaunchedEffect(videoLink) {
        if (videoLink.isNotEmpty()) {
            Log.i(TAG, "ExoPlayer: Loading Video: $videoLink")
            exoPlayer.setMediaItem(MediaItem.fromUri(videoLink))
            exoPlayer.prepare()
        }
    }

    AndroidView(modifier = modifier, factory = {
        PlayerView(it).apply {
            player = exoPlayer
        }
    })

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            Log.i(TAG, "ExoPlayer: Released the Player")
        }
    }
}