package com.ero.iwara.util

import android.content.ClipData
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.*
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import androidx.core.net.toUri


@OptIn(FormatStringsInDatetimeFormats::class, ExperimentalTime::class)
fun String?.format(format: String = "yyyy-MM-dd HH:mm", zone: TimeZone = TimeZone.UTC): String
{
    if(this == null) return ""
    val instant = Instant.parse(this) // 'this' 是输入的 UTC 字符串
    val formatter = LocalDateTime.Format {
        // 2023-01-20T23:53:16.312+03:30[Asia/Tehran]
        byUnicodePattern(format)
    }
    val local = instant.toLocalDateTime(zone)
    return local.format(formatter)
}


fun String?.query(): Map<String, String>
{
    if(this == null) return mapOf()
    val uri = this.toUri()
    return uri.queryParameterNames.associateWith { key ->
        uri.getQueryParameter(key) ?: ""
    }
}

suspend fun Clipboard?.set(text: String?)
{
    if(this == null) return
    if(text.isNullOrEmpty()) return
    val clipData = ClipData.newPlainText(text, text)
    this.setClipEntry(clipData.toClipEntry())
}