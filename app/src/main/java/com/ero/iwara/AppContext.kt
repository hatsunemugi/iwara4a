package com.ero.iwara

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.ero.iwara.crash.CrashHandler
import dagger.hilt.android.HiltAndroidApp
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
                    .header("Referer", "https://www.iwara.tv/")
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
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler())
    }
}