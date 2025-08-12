package com.ero.iwara.cache

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object MediaCache {

    private var value: SimpleCache? = null
    private const val MAX_CACHE_SIZE_BYTES: Long = 100 * 1024 * 1024 // 例如：100MB

    @Synchronized
    fun cache(context: Context): Cache {
        var result = value
        if (result == null) {
            val cacheFolder = File(context.cacheDir, "media")
            val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE_BYTES)
            val databaseProvider = StandaloneDatabaseProvider(context)
            result = SimpleCache(cacheFolder, evictor, databaseProvider)
            value = result
        }
        return result
    }

    fun factory(context: Context, upstreamFactory: DataSource.Factory): CacheDataSource.Factory
    {
        val cache = cache(context.applicationContext) // 使用 application context
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            // 可选：在缓存写入失败时不中断播放
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        // 可选：如果希望缓存读取也通过网络进行（例如，用于测试或更新缓存策略），但不推荐用于常规播放
        // .setCacheReadDataSourceFactory(null) // 默认为从缓存文件读取
        // 可选：如果希望缓存写入也通过网络进行（通常与 upstreamFactory 相同）
        // .setCacheWriteDataSourceFactory(upstreamFactory)
    }
}