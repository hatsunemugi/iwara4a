package com.ero.iwara.model.session

import android.content.Context
import androidx.core.content.edit
import com.ero.iwara.sharedPreferencesOf
import com.ero.iwara.util.AppPreferencesDelegates

class SessionManager(context: Context) {
    var salt = AppPreferencesDelegates.string(
        context = context,
        name = "config", // SharedPreferences 文件名
        key = "salt",       // 存储的键
        defaultValue = "mSvL05GfEmeEmsEYfGCnVpEjYgTJraJN"     // 默认值
    )
    val session: Session by lazy {
        val sharedPreferences = sharedPreferencesOf("session")
        Session(sharedPreferences.getString("key","")!!, sharedPreferences.getString("value","")!!)
    }

    fun update(key: String, value: String) {
        session.key = key
        session.value = value
        sharedPreferencesOf("session").edit {
            putString("key", key)
            putString("value", value)
        }
    }
}