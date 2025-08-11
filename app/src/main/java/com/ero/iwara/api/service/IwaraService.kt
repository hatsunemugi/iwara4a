package com.ero.iwara.api.service

import com.ero.iwara.model.detail.video.VideoLink
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 使用Retrofit直接获取 RESTFUL API 资源
 */
interface IwaraService {
    @POST("api/video/{videoId}")
    suspend fun getVideoInfo(@Path("videoId") videoId: String): VideoLink
}