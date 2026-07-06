package com.ero.iwara.event

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ero.iwara.util.now
import com.ero.iwara.stroage.entity.LogBase


sealed interface AppEvent {
    data class LogEvent(val value: LogBase): AppEvent
    data class RouteEvent(val route: String): AppEvent
    data class BaseEvent<T,E>(val target: T? = null, val value: E):  AppEvent
    data class TagEvent<T: ViewModel>(val value: String, val target: T? = null):  AppEvent
    data class Clipboard(val message: String): AppEvent
    data class MessageEvent(val message: String, val copy: Boolean) : AppEvent
    object NetworkStatusChanged : AppEvent // 无参数事件
    // 可以添加更多具体事件
}

fun clipboard(value: String)
{
    publish(AppEvent.Clipboard(value))
}

fun log(uid: Long, type: Long, uname: String, action: String, message: String)
{
    Log.d(uname, "$action: $message")
    val datetime = now("yyyy-MM-dd HH:mm:ss")
    publish(AppEvent.LogEvent(
        LogBase(
            uid = uid,
            type = type,
            uname = uname,
            action = action,
            message = message,
            datetime = datetime
        )
    ))
}

fun error(error: Throwable, action: String)
{
    val datetime = now("yyyy-MM-dd HH:mm:ss")
    publish(AppEvent.LogEvent(LogBase(uid= 0, type = 0, uname= "系统", action = action, message=error.toString(), datetime=datetime)))
}

fun route(route: String)
{
    publish(AppEvent.RouteEvent(route))
}
