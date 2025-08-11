package com.ero.iwara.util

import android.content.Context
import android.content.Intent
import com.ero.iwara.model.index.MediaType

fun shareMedia(context: Context, mediaType: MediaType, mediaId: String){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "https://ecchi.iwara.tv/${mediaType.value}/$mediaId")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}