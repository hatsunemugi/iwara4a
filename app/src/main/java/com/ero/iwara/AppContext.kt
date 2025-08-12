package com.ero.iwara

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
//import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
//import xyz.doikki.videoplayer.player.VideoViewConfig
//import xyz.doikki.videoplayer.player.VideoViewManager

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
        // 使用ExoPlayer解码
//        VideoViewManager.setConfig(VideoViewConfig.newBuilder().setPlayerFactory(ExoMediaPlayerFactory.create()).build())
    }
}

/**
 * 使用顶层函数直接获取 SharedPreference
 *
 * @param name SharedPreference名字
 * @return SharedPreferences实例
 */
fun sharedPreferencesOf(name: String): SharedPreferences = AppContext.instance.getSharedPreferences(name, Context.MODE_PRIVATE)
