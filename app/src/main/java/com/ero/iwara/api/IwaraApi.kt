package com.ero.iwara.api

import androidx.annotation.IntRange
import com.ero.iwara.model.comment.CommentList
import com.ero.iwara.model.detail.image.ImageDetail
import com.ero.iwara.model.detail.video.VideoDetail
import com.ero.iwara.model.flag.FollowResponse
import com.ero.iwara.model.flag.LikeResponse
import com.ero.iwara.model.index.MediaList
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.index.SubscriptionList
import com.ero.iwara.model.session.Session
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.result.MCount

/**
 * 提供远程资源API, 通过连接IWARA来获取数据
 */
interface IwaraApi {
    /**
     * 尝试登录Iwara
     *
     * @param username 登录用户名
     * @param password 登录密码
     * @return session cookie
     */
    suspend fun login(username: String, password: String): Response<String>

    /**
     * 尝试登录Iwara
     *
     * @param token 用户口令
     * @return accessToken 接口口令
     */
    suspend fun getToken(token: String): Response<String>

    /**
     * 获取基础的个人信息
     *
     * @param session 登录凭据
     * @return 简短的个人信息
     */
    suspend fun getSelf(session: Session): Response<Self>

    /**
     * 获取订阅列表
     *
     * @param session 登录凭据
     * @param page 页数
     * @return 订阅列表
     */
    suspend fun getSubscriptionList(session: Session, @IntRange(from = 0) page: Int): Response<SubscriptionList>

    /**
     * 获取图片页面信息
     *
     * @param session 登录凭据
     * @param imageId 图片ID
     * @return 图片页面信息
     */
    suspend fun getImagePageDetail(session: Session, imageId: String): Response<ImageDetail>

    /**
     * 获取视频页面信息
     *
     * @param session 登录凭据
     * @param videoId 视频ID
     * @return 视频页面信息
     */
    suspend fun getVideoPageDetail(session: Session, videoId: String): Response<VideoDetail>

    /**
     * 喜欢某个视频/图片
     *
     */
    suspend fun like(session: Session, like: Boolean, likeLink: String): Response<LikeResponse>

    /**
     * 关注某人
     */
    suspend fun follow(session: Session, follow: Boolean, followLink: String): Response<FollowResponse>

    /**
     * 解析评论列表
     *
     * @param session 登录凭据
     * @param mediaType 媒体类型
     * @param mediaId 媒体ID
     * @param page 评论页数
     *
     * @return 评论列表
     */
    suspend fun getCommentList(session: Session, mediaType: MediaType, authorId: String, mediaId: String, @IntRange(from = 0) page: Int): Response<CommentList>

    /**
     * 获取资源列表
     *
     * @param session 登录凭据
     * @param mediaType 媒体类型
     * @param page 页数
     * @param sort 排序条件
     * @param tags 过滤器
     *
     * @return 资源列表
     */
    suspend fun getMediaList(session: Session, mediaType: MediaType, @IntRange(from = 0) page: Int, sort: SortType, tags: List<String>): Response<MediaList>

    /**
     * 加载用户资料
     *
     * @param session 登录凭据
     * @param username 用户ID
     * @return 用户数据
     */
    suspend fun getUser(session: Session, username: String): Response<UserData>

    /**
     * 加载用户资料
     *
     * @param session 登录凭据
     * @return 用户统计数据
     */
    suspend fun getCount(session: Session): Response<MCount>

    /**
     * 搜索视频和图片
     *
     * @param session 登录凭据
     * @param query 搜索关键词
     * @param page 页数
     * @param sort 排序条件
     * @param filter 过滤条件
     * @return 资源列表
     */
    suspend fun search(session: Session, query: String, @IntRange(from = 0) page: Int, type: MediaType): Response<MediaList>
}