package com.ero.iwara

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.OkHttpClient


@HiltAndroidApp
class AppContext : Application() {
    companion object {
        lateinit var instance : Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithReferer = originalRequest.newBuilder()
                    .header("Referer", "https://www.iwara.tv/") // 添加 Referer
                    .build()
                chain.proceed(requestWithReferer)
            }
            .build()
        // 创建 Coil 的 ImageLoader
        val imageLoader = ImageLoader.Builder(this)
            .okHttpClient(okHttpClient) // 使用自定义的 OkHttpClient
            .build()
        // 设置为全局默认 ImageLoader
        Coil.setImageLoader(imageLoader)
    }
}

/**
 * 使用顶层函数直接获取 SharedPreference
 *
 * @param name SharedPreference名字
 * @return SharedPreferences实例
 */
fun sharedPreferencesOf(name: String): SharedPreferences = AppContext.instance.getSharedPreferences(name, Context.MODE_PRIVATE)


val handler = CoroutineExceptionHandler { _, throwable -> Log.e("捕获", throwable.message ?: "未知异常") }