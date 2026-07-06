package com.ero.iwara.stroage

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.ero.iwara.model.session.Session
import com.ero.iwara.model.user.Self
import com.ero.iwara.util.AppPreferencesDelegates

class Config(context: Context) {
    val salt = AppPreferencesDelegates.string(
        context = context,
        name = "config", // SharedPreferences 文件名
        key = "salt",       // 存储的键
        defaultValue = "mSvL05GfEmeEmsEYfGCnVpEjYgTJraJN"     // 默认值
    )
    val ready = mutableStateOf(false)
    val debug = mutableStateOf(false)
    val id = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "id",       // 存储的键
        defaultValue = "未登录"     // 默认值
    )
    val email = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "email",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val avatar = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "avatar",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val nickname = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "nickname",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val username = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "username",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val password = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "password",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val token = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "token",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val accessToken = AppPreferencesDelegates.string(
        context = context,
        name = "session", // SharedPreferences 文件名
        key = "accessToken",       // 存储的键
        defaultValue = ""     // 默认值
    )
    val user = mutableStateOf(Self.Companion.GUEST)
    fun header(): Map<String, String>
    {
        val value by accessToken
        if(value.isEmpty()) throw Exception("token 为空")
        return mapOf("Authorization" to "Bearer $value")
    }
    fun header(pair: Pair<String, String>): Map<String, String>
    {
        val value by accessToken
        if(value.isEmpty()) throw Exception("token 为空")
        return mapOf("Authorization" to "Bearer $value", pair)
    }
}