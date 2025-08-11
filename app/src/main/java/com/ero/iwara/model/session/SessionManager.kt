package com.ero.iwara.model.session

import androidx.core.content.edit
import com.ero.iwara.sharedPreferencesOf

class SessionManager {
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