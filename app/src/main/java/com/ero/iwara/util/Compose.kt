package com.ero.iwara.util

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

fun Modifier.clickSound(
    context: Context,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit,
): Modifier{
    return clickable(enabled, onClickLabel, role, interactionSource, onClick.withSound(context))
}

fun (() -> Unit).withSound(context: Context): () -> Unit = {
    (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        .playSoundEffect(AudioManager.FX_KEY_CLICK)
    this()
}