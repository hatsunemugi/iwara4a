package com.ero.iwara.util

import android.content.ClipData
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.*
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.ero.iwara.cache.MediaCache
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.postFlowEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest


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

fun send(message: String)
{
    postFlowEvent(AppEvent.GenericMessageEvent(message))
}

@Composable
fun HandleMessage(
    flow: Flow<String>
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    LaunchedEffect(flow) { // 如果 messagesFlow 实例是稳定的，也可以用 Unit
        flow.collectLatest { Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            clipboard.set(it)
        }
    }
}

fun Map<String, String>?.toQuery(): String?
{
    if(this.isNullOrEmpty()) return null
    return map { "${it.key}=${it.value}" }.joinToString("&")
}

@androidx.annotation.OptIn(UnstableApi::class)
fun ExoPlayer.Builder.cache(context: Context): ExoPlayer.Builder
{
    // 1. 创建上游数据源工厂 (例如，从网络加载)
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()

    // 2. 创建缓存数据源工厂
    val cacheDataSourceFactory = MediaCache.factory(context, httpDataSourceFactory)

    // 3. 创建 MediaSource 时使用缓存数据源工厂
    val mediaSourceFactory = DefaultMediaSourceFactory(context)
        .setDataSourceFactory(cacheDataSourceFactory) // 关键步骤

    return setMediaSourceFactory(mediaSourceFactory)
}
