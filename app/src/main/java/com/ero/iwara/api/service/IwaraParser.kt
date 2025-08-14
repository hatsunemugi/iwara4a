package com.ero.iwara.api.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ero.iwara.api.Response
import com.ero.iwara.model.comment.Comment
import com.ero.iwara.model.comment.CommentList
import com.ero.iwara.model.comment.CommentPosterType
import com.ero.iwara.model.detail.image.ImageDetail
import com.ero.iwara.model.detail.video.MoreVideo
import com.ero.iwara.model.detail.video.VideoDetail
import com.ero.iwara.model.flag.FollowResponse
import com.ero.iwara.model.flag.LikeResponse
import com.ero.iwara.model.index.MediaList
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.index.SubscriptionList
import com.ero.iwara.model.index.TagList
import com.ero.iwara.model.session.Session
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.param.PageParam
import com.ero.iwara.param.UserLogin
import com.ero.iwara.result.MAccessToken
import com.ero.iwara.result.MBase
import com.ero.iwara.result.MComment
import com.ero.iwara.result.MCount
import com.ero.iwara.result.MForum
import com.ero.iwara.result.MImage
import com.ero.iwara.result.MLike
import com.ero.iwara.result.MLink
import com.ero.iwara.result.MLinkInfo
import com.ero.iwara.result.MPost
import com.ero.iwara.result.MProfile
import com.ero.iwara.result.MResult
import com.ero.iwara.result.MTag
import com.ero.iwara.result.MToken
import com.ero.iwara.result.MUser
import com.ero.iwara.result.MUserInfo
import com.ero.iwara.result.MVideo
import com.ero.iwara.sharedPreferencesOf
import com.ero.iwara.util.format
import com.ero.iwara.util.okhttp.CookieJarHelper
import com.ero.iwara.util.okhttp.await
import com.ero.iwara.util.send
import com.ero.iwara.util.toQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

private const val TAG = "IwaraParser"

/**
 * 使用Jsoup来解析出网页上的资源
 *
 * 某些资源无法通过 restful api 直接获取，因此需要
 * 通过jsoup来解析
 *
 * @author RE
 */
class IwaraParser() {
    val api = "https://api.iwara.tv"
    val file = "https://i.iwara.tv"
    fun getClient(): OkHttpClient
    {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cookieJar(CookieJarHelper())
            .build()
        return client
    }
    inline fun <reified T> CoroutineScope.decode(json: String?):T?
    {
        if(json.isNullOrEmpty()) return null
        try
        {
            val model = Json.decodeFromString<T>(json)
            return model
        }
        catch (ex: Exception)
        {
            return null.apply { send("结果：${json.take(100)}-错误:${ex.toString()}", true) }
        }
    }
    suspend inline fun <reified E> get(url: String, param: Map<String, String>? = null, headers: Map<String, String>? = null): E? =
         withContext(Dispatchers.IO) {
            try {
                val httpClient = getClient()
                val builder = url.toHttpUrlOrNull()?.newBuilder()
                    ?: return@withContext null
                param?.forEach { (field, value) ->
                    builder.addQueryParameter(field, value)
                }
                val request = Request.Builder()
                    .url(builder.build())
                    .get()
                headers?.forEach { (name, value) ->
                    request.header(name, value) // 或者使用 addHeader 如果需要支持同名多值
                }
                val response = httpClient.newCall(request.build()).await()
                val json = response.body.string()
                val model = decode<E>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-参数${param.toQuery()}-标头${headers.toQuery()}", true) }

            } catch (ex: Exception)
            {
                return@withContext null.apply { send("接口地址：$url-参数${param.toQuery()}-标头${headers.toQuery()}-错误${ex.toString()}", true) }
            }
        }
    suspend inline fun <reified E> delete(url: String, param: Map<String, String>? = null, headers: Map<String, String>? = null): E? =
        withContext(Dispatchers.IO) {
            try
            {
                val httpClient = getClient()
                val builder = url.toHttpUrlOrNull()?.newBuilder()
                    ?: return@withContext null
                param?.forEach { (field, value) ->
                    builder.addQueryParameter(field, value)
                }
                val request = Request.Builder()
                    .url(builder.build())
                    .delete()
                headers?.forEach { (name, value) ->
                    request.header(name, value) // 或者使用 addHeader 如果需要支持同名多值
                }
                val response = httpClient.newCall(request.build()).await()
                val json = response.body.string()
                val model = decode<E>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-参数${param.toQuery()}-标头${headers.toQuery()}", true) }
            }
            catch (ex: Exception)
            {
                return@withContext null.apply { send("接口地址：$url-参数${param.toQuery()}-标头${headers.toQuery()}-错误${ex.toString()}", true) }
            }
        }
    suspend inline fun <reified T, reified E> post(url: String, param: T, headers: Map<String, String>? = null): E? =
        withContext(Dispatchers.IO) {
            val encode = Json.encodeToString(param)
            try
            {
                val httpClient = getClient()
                val body: RequestBody = encode.toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                headers?.forEach { (name, value) ->
                    request.header(name, value) // 或者使用 addHeader 如果需要支持同名多值
                }
                val response = httpClient.newCall(request.build()).await()
                val json = response.body.string()
                val model = decode<E>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-参数${param}-标头${headers.toQuery()}",true) }
            }
            catch (ex: Exception)
            {
                return@withContext null.apply { send("接口地址：$url-参数${param}-标头${headers.toQuery()}-错误${ex.toString()}",true) }
            }
        }
    suspend inline fun <reified T> post(url: String, headers: Map<String, String>? = null): T? =
        withContext(Dispatchers.IO) {
            try
            {
                val httpClient = getClient()
                val body = "".toRequestBody(null)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                headers?.forEach { (name, value) ->
                    request.header(name, value) // 或者使用 addHeader 如果需要支持同名多值
                }
                val response = httpClient.newCall(request.build()).await()
                val json = response.body.string()
                val model = decode<T>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-标头${headers.toQuery()}",true) }
            }
            catch (ex: Exception)
            {
                return@withContext null.apply { send("接口地址：$url-标头${headers.toQuery()}-错误${ex.toString()}",true) }
            }
        }
    suspend fun login(param: UserLogin): Response<String> {
        try {
            val response = post<UserLogin,MToken>("$api/user/login", param) ?: return Response.failed("登录失败")
            return Response.success(response.token)
        }catch (ex: Exception)
        {
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }
    suspend fun getToken(token: String): Response<String> {
        try {
            val response = post<MAccessToken>("$api/user/token", mapOf("Authorization" to "Bearer $token")) ?: return Response.failed("token获取失败")
            return Response.success(response.accessToken)
        }catch (ex: Exception)
        {
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }

    suspend fun getSelf(session: Session): Response<Self>
    {
        try {
            val user = get<MUserInfo>("$api/user", null, session.map()) ?: return Response.failed("接口请求失败")
            val id = user.user.id
            val email = user.user.email ?: ""
            val username = user.user.username
            val nickname = user.user.name
            val profilePic = user.user.getAvatar(file)
            Log.i(TAG, "getSelf: (nickname=$nickname, profilePic=$profilePic)")
            return Response.success(Self(id = id, email = email, username = username, nickname = nickname, avatar = profilePic))
        } catch (exception: Exception) {
            exception.printStackTrace()
            return Response.failed(exception.javaClass.name)
        }
    }

    suspend fun getTag(filter: String, page: Int): Response<TagList>
    {
        try {
            val param = PageParam(filter = filter, page = page)
            val response = get<MResult<MTag>>("$api/tags", param.map(), null) ?: return Response.failed("接口请求失败")
            val hasNext = response.limit * (page+1) < response.count
            return Response.success(TagList(currentPage = page, hasNext = hasNext, tagList = response.results))
        } catch (exception: Exception) {
            exception.printStackTrace()
            return Response.failed(exception.javaClass.name)
        }
    }

    suspend fun getSubscriptionList(session: Session, type: MediaType, page: Int): Response<SubscriptionList>
    {
        try {
            val param = PageParam(rating = "ecchi", page = page, limit = 32, subscribed = true)
            val response = when(type){
                MediaType.VIDEO -> get<MResult<MVideo>>("$api/videos", param.map(), session.map())?.transform { it.mediaView(file)}
                MediaType.IMAGE -> get<MResult<MImage>>("$api/images", param.map(), session.map())?.transform { it.mediaView(file)}
                MediaType.POST -> get<MResult<MPost>>("$api/posts", param.map(), session.map())?.transform { it.mediaView(file)}
                else -> null
            }?: return Response.failed("接口请求失败")
            val hasNextPage = response.limit * (page +1) < response.count

            return Response.success(
                SubscriptionList(
                    page,
                    hasNextPage,
                    response.results
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }
    suspend fun getImagePageDetail(session: Session, imageId: String): Response<ImageDetail>
    {
        try {
            Log.i(TAG, "getImagePageDetail: start load image detail: $imageId")
            val response = get<MImage>("$api/image/$imageId",null, session.map()) ?: return Response.failed("${imageId}为空")
            val title = response.title
            val imageLinks = response.files.map {
                it.getLargeImage(file)
            }
            val authorId = response.user.id
            val authorName = response.user.name
            val authorPic = response.user.getAvatar(file)
            val watches = response.numViews.toString()

            return Response.success(
                ImageDetail(
                    id = imageId,
                    title = title,
                    imageLinks = imageLinks,
                    authorId = authorId,
                    authorName = authorName,
                    authorProfilePic = authorPic,
                    watches = watches
                )
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            return Response.failed(exception.javaClass.name)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun getVideoPageDetail(session: Session, videoId: String): Response<VideoDetail>
    {
        try {
            Log.i(TAG, "getVideoPageDetail: Start load video detail (id:$videoId)")
            val response = get<MVideo>("$api/video/$videoId",null, session.map()) ?: return Response.failed("${videoId}为空")
            val links = response.fileUrl?.let { get<List<MLinkInfo>>(it, null,session.map("xversion" to response.version())) }?.filter { it.name != "preview" }?.apply { forEach { it.src  = MLink("https:"+it.src.view, "https:"+it.src.download)  } } ?: listOf()
            val title = response.title
            val likes = response.numLikes.toString()
            val watches = response.numViews.toString()
            // 解析出上传日期
            val postDate = response.createdAt.format("yyyy-MM-dd HH:mm")

            // 视频描述
            val description = response.body ?: ""
            val authorId = response.user.id
            val nickname = response.user.name
            val username = response.user.username
            val authorPic = response.user.getAvatar(file)
            val related = get<MResult<MVideo>>("$api/videos", PageParam(rating = "ecchi", user = authorId, exclude = response.id, limit = 32).map(), session.map())
//            val related = get<MResult<MVideo>>("$api/video/${response.id}/related",null, session.map())
            // 更多视频
            val moreVideo = related?.results?.map {
                val id = it.id
                val title = it.title
                val pic =  it.getPreviewPic(file)
                val likes = it.numLikes.toString()
                val watches = it.numViews.toString()
                MoreVideo(
                    id = id,
                    title = title,
                    pic = pic,
                    likes = likes,
                    watches = watches
                )
            } ?: listOf()
            // 喜欢
            val isLike = response.liked
            val likeLink = "$api/video/${response.id}/like"

            // 关注UP主
            val isFollow = response.user.following
            val followLink = "$api/user/${response.user.id}/followers"

            return Response.success(
                VideoDetail(
                    id = videoId,
                    title = title,
                    likes = likes,
                    watches = watches,
                    postDate = postDate,
                    description = description,
                    authorPic = authorPic,
                    authorName = username,
                    authorNickname = nickname,
                    authorId = authorId,
                    links = links,
                    moreVideo = moreVideo,
                    isLike = isLike,
                    likeLink = likeLink,
                    follow = isFollow,
                    followLink = followLink
                )
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.i(TAG, "getVideoPageDetail: Failed to load video detail")
            return Response.failed(exception.javaClass.name)
        }
    }

    suspend fun like(session: Session, like: Boolean, likeLink: String): Response<LikeResponse>
    {
        try {
            val response = if(like) post<MLike>(likeLink, session.map()) else delete<MLike>(likeLink, session.map())
            val likeResponse = LikeResponse(status = response != null)
            return Response.success(likeResponse)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }

    suspend fun follow(session: Session, follow: Boolean, followLink: String): Response<FollowResponse>
    {
        try {
            val response = if(follow) post<MBase>(followLink, session.map()) else delete<MBase>(followLink, session.map())

            val followResponse = FollowResponse(status = response != null)
            return Response.success(followResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            return Response.failed(e.javaClass.name)
        }
    }
    suspend fun getReply(session: Session, mediaType: MediaType, mediaId: String, comment: MComment): List<Comment> {
        try {
            if(comment.numReplies == 0) return listOf()
            val param = PageParam(parent = comment.id, page = 0)
            val response = get<MResult<MComment>>("$api/${mediaType.value}/$mediaId/comments", param.map(), session.map())
            val result = response?.results?.map {
                val authorId = it.user?.id ?: ""
                val authorName = it.user?.name ?: ""
                val authorPic = it.user?.getAvatar(file) ?: "https://www.iwara.tv/images/default-avatar.jpg"
                val content = it.body
                val date = it.createdAt.format()
                Comment(
                    id = it.id,
                    authorId = authorId,
                    authorName = authorName,
                    authorPic = authorPic,
                    posterType= CommentPosterType.OWNER,
                    content = content,
                    date = date,
                    reply = getReply(session, mediaType, mediaId, it)
                )
            } ?: listOf()
            return result
        } catch (ex: Exception)
        {
            ex.printStackTrace()
            return listOf()
        }
    }
    suspend fun getCommentList(
        session: Session,
        mediaType: MediaType,
        author: String,
        mediaId: String,
        page: Int
    ): Response<CommentList>
    {
        try {
            val param = PageParam(page = page)
            val response = get<MResult<MComment>>("$api/${mediaType.value}/$mediaId/comments", param.map(), session.map()) ?: return Response.failed("接口请求失败")
            val self = sharedPreferencesOf("session").getString("id","未登录")
            val commentList = response.results.map {
                val authorId = it.user?.id ?: ""
                val authorName = it.user?.name ?: ""
                val authorPic = it.user?.getAvatar(file) ?: "https://www.iwara.tv/images/default-avatar.jpg"
                val content = it.body
                val date = it.createdAt.format()

                Comment(
                    id = it.id,
                    authorId = authorId,
                    authorName = authorName,
                    authorPic = authorPic,
                    posterType= when(authorId){
                        author -> CommentPosterType.OWNER
                        self -> CommentPosterType.SELF
                        else -> CommentPosterType.NORMAL
                    },
                    content = content,
                    date = date,
                    reply = getReply(session, mediaType, mediaId, it)
                )
            }

            val total = response.count
            val hasNext = response.limit * (page + 1) < response.count

            return Response.success(
                CommentList(
                    total = total,
                    page = page,
                    hasNext = hasNext,
                    comments = commentList
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            return  Response.failed(ex.javaClass.name)
        }
    }

    suspend fun getMediaList(session: Session, mediaType: MediaType, page: Int, sort: SortType, tags: List<String>): Response<MediaList>
    {
        try {
            val param = PageParam(page = page, rating = "ecchi", sort = sort.value, tags = tags.joinToString(","))
            val result = when(mediaType)
            {
                MediaType.IMAGE -> get<MResult<MImage>>("$api/images", param.map(), session.map())?.transform { it.mediaView(file) }
                MediaType.VIDEO -> get<MResult<MVideo>>("$api/videos", param.map(), session.map())?.transform { it.mediaView(file) }
                else -> get<MResult<MVideo>>("$api/videos", param.map(), session.map())?.transform { it.mediaView(file) }
            } ?: return Response.failed("接口请求失败")
            val list = result.results
            val hasNext = result.limit * (page) < result.count

            return Response.success(
                MediaList(
                    currentPage = page,
                    hasNext = hasNext,
                    mediaList = list
                )
            )
        }catch (e: Exception){
            e.printStackTrace()
            return Response.failed(e.javaClass.name)
        }
    }
    suspend fun getCount(session: Session): Response<MCount>
    {
        try{
            val response = get<MCount>("$api/user/counts", null, session .map()) ?: return Response.failed("接口请求失败")

            return Response.success(response)
        }catch (ex: Exception){
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }
    suspend fun getUser(session: Session, username: String): Response<UserData>
    {
        try{
            val response = get<MProfile>("$api/profile/$username", null, session .map()) ?: return Response.failed("接口请求失败")
            val user = response.user
            return Response.success(
                UserData(
                    userId = user?.username ?: "",
                    username = user?.name ?: "",
                    pic = user?.getAvatar(file) ?: "",
                    joinDate = user?.createdAt.format(),
                    lastSeen = user?.seenAt.format(),
                    about = response.body ?: ""
                )
            )
        }catch (ex: Exception){
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }

    suspend fun search(session: Session, query: String, page: Int, type: MediaType): Response<MediaList>
    {
        try {
            val param = PageParam(page = page, type = type.value, query = query)
            val result = when(type)
            {
                MediaType.IMAGE -> get<MResult<MImage>>("$api/search", param.map(), session.map())?.transform { it.mediaView(file) }
                MediaType.VIDEO -> get<MResult<MVideo>>("$api/search", param.map(), session.map())?.transform { it.mediaView(file) }
                MediaType.POST ->  get<MResult<MPost>>("$api/search", param.map(), session.map())?.transform { it.mediaView(file) }
                MediaType.USER -> get<MResult<MUser>>("$api/search", param.map(), session.map())?.transform { it.mediaView(file) }
                MediaType.FORUM -> get<MResult<MForum>>("$api/search", param.map(), session.map())?.transform { it.mediaView(file) }
            } ?: return Response.failed("接口请求失败")
            val list = result.results
            val hasNext = result.limit * (page) < result.count

            return Response.success(
                MediaList(
                    currentPage = page,
                    hasNext = hasNext,
                    mediaList = list
                )
            )
        }catch (e: Exception){
            e.printStackTrace()
            return Response.failed(e.javaClass.name)
        }
    }
}