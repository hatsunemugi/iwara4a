package com.ero.iwara.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
fun now(format: String, zone: TimeZone = TimeZone.of("Asia/Shanghai")): String
{
    val instant = now()
    val formatter = LocalDateTime.Format {
        // 2023-01-20T23:53:16.312+03:30[Asia/Tehran]
        byUnicodePattern(format)
    }
    val local = instant.toLocalDateTime(zone)
    return local.format(formatter)
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun String?.convert(format: String = "yyyy-MM-dd HH:mm:ss"): String
{
    if(this == null) return ""
    val patterns = listOf(
        // 常见空格分隔模式
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd HH:mm:ss.SSS",

        // ISO-like patterns (T是最常见的)
        "yyyy-MM-ddTHH:mm:ss",
        "yyyy-MM-ddTHH:mm",
        "yyyy-MM-ddTHH:mm:ss.SSS", // 带毫秒

        // 包含 Z 或偏移量的 ISO Instant 格式 (如果输入可能是 Instant 字符串)
        // 注意：如果输入是 Instant 字符串，解析为 LocalDateTime 会丢失时区信息，除非你先解析为 Instant
        "yyyy-MM-ddTHH:mm:ssZ",
        "yyyy-MM-ddTHH:mm:ssXXX", // +01:00
        // 其他常见格式
        "MM/dd/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss",
        "MM-dd-yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm:ss",
        "MM/dd/yyyy",
        "dd/MM/yyyy",
        "yyyy-MM-dd"
        // ... 你可以继续添加更多格式
    )

    for (pattern in patterns) {
        try {
            val formatter = LocalDateTime.Format { byUnicodePattern(pattern) }
            val time = formatter.parse(this)
            return time.format(LocalDateTime.Format {byUnicodePattern(format)})
        } catch (_: Exception) {
            continue
        }
    }
    return ""
}