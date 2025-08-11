package com.ero.iwara.model.session

import okhttp3.Cookie

data class Session(
    var key: String,
    var value: String
){
    fun toCookie() = Cookie.Builder()
        .name(key)
        .value(value)
        .domain("iwara.tv")
        .build()

    fun isNotEmpty() = key.isNotEmpty() && value.isNotEmpty()
    fun map(): Map<String, String>
    {
        if(value.isEmpty()) throw Exception("token 为空")
        return mapOf("Authorization" to "Bearer $value")
    }
    fun map(pair: Pair<String, String>): Map<String, String>
    {
        if(value.isEmpty()) throw Exception("token 为空")
        return mapOf("Authorization" to "Bearer $value", pair)
    }
}