package com.ero.iwara.util

fun formatCount(count: Int): String {
    return when {
        count < 10000 -> count.toString()
        else -> {
            val wan = count / 10000.0
            // 使用 String.format 保持一位小数，例如 1.2万
            // 注意：前面我们讨论过 Locale 问题，这里建议加上 Locale.getDefault()
            "%.1fw".format(wan).replace(".0", "") // 如果是 1.0万，则显示为 1万
        }
    }
}