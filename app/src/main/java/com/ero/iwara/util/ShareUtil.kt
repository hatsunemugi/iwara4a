package com.ero.iwara.util

import android.content.Context
import android.content.Intent
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.stroage.IntDelegate
import com.ero.iwara.stroage.IntValueEnumDelegate
import com.ero.iwara.stroage.LongDelegate
import com.ero.iwara.stroage.StringDelegate
import kotlin.properties.ReadWriteProperty

fun shareMedia(context: Context, mediaType: MediaType, mediaId: String){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "https://www.iwara.tv/${mediaType.value}/$mediaId")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}


// --- 或者，如果你的 AppContext.instance 总是可用 ---
object AppPreferencesDelegates {

    fun long(
        context: Context,
        name: String,
        key: String,
        defaultValue: Long = 0
    ): ReadWriteProperty<Any?, Long> {
        // 假设 AppContext.instance 总是可用的 ApplicationContext
        // 如果 AppContext 未初始化，这里可能会出问题，需要确保它在委托使用前已初始化
        return LongDelegate(context, name, key, defaultValue)
    }

    fun int(
        context: Context,
        name: String,
        key: String,
        defaultValue: Int = 0
    ): ReadWriteProperty<Any?, Int> {
        // 假设 AppContext.instance 总是可用的 ApplicationContext
        // 如果 AppContext 未初始化，这里可能会出问题，需要确保它在委托使用前已初始化
        return IntDelegate(context, name, key, defaultValue)
    }
    fun string(
        context: Context,
        name: String,
        key: String,
        defaultValue: String = ""
    ): ReadWriteProperty<Any?, String> {
        // 假设 AppContext.instance 总是可用的 ApplicationContext
        // 如果 AppContext 未初始化，这里可能会出问题，需要确保它在委托使用前已初始化
        return StringDelegate(context, name, key, defaultValue)
    }
    fun <E : Enum<E>> intValueEnum(
        context: Context,
        name: String,
        key: String,
        defaultValue: E,
        toIntValue: (E) -> Int,
        fromIntValue: (Int) -> E?
    ): ReadWriteProperty<Any?, E> {
        return IntValueEnumDelegate(
            context,
            name,
            key,
            defaultValue,
            toIntValue,
            fromIntValue
        )
    }
    // 你也可以在这里为 Int, Boolean 等类型创建类似的委托和便捷函数
    // fun int(...) ...
    // fun boolean(...) ...
}
