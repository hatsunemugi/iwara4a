package com.ero.iwara.api.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
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
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.model.index.SubscriptionList
import com.ero.iwara.model.index.TagList
import com.ero.iwara.model.user.Self
import com.ero.iwara.model.user.UserData
import com.ero.iwara.param.PageParam
import com.ero.iwara.param.UserLogin
import com.ero.iwara.api.result.MAccessToken
import com.ero.iwara.api.result.MBase
import com.ero.iwara.api.result.MComment
import com.ero.iwara.api.result.MCount
import com.ero.iwara.api.result.MForum
import com.ero.iwara.api.result.MImage
import com.ero.iwara.api.result.MLike
import com.ero.iwara.api.result.MLink
import com.ero.iwara.api.result.MLinkInfo
import com.ero.iwara.api.result.MPost
import com.ero.iwara.api.result.MProfile
import com.ero.iwara.api.result.MResult
import com.ero.iwara.api.result.MTag
import com.ero.iwara.api.result.MThread
import com.ero.iwara.api.result.MToken
import com.ero.iwara.api.result.MUser
import com.ero.iwara.api.result.MUserInfo
import com.ero.iwara.api.result.MVideo
import com.ero.iwara.event.log
import com.ero.iwara.stroage.Config
import com.ero.iwara.util.format
import com.ero.iwara.util.formatCount
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
class IwaraParser(
    private val config: Config
) {
    val api = "https://api.iwara.tv"
    val file = "https://i.iwara.tv"
    val id by config.id
    val salt by config.salt
    val debug by config.debug
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
            return null.apply { log(0,8, "格式化", json,ex.toString()) }
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
                if(debug) log(0,4, "调试", url,json)
                val model = decode<E>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-参数${param.toQuery()}-标头${headers.toQuery()}", true) }

            } catch (ex: Exception)
            {
                return@withContext null.apply { log(0,1, "网络", url,ex.toString()) }
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
                if(debug) log(0,4, "调试", url,json)
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
                if(debug) log(0,4, "调试", url,json)
                val model = decode<E>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-参数${param}-标头${headers.toQuery()}",true) }
            }
            catch (ex: Exception)
            {
                return@withContext null.apply { log(0,2, "网络", url,ex.toString()) }
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
                if(debug) log(0,4, "调试", url,json)
                val model = decode<T>(json)
                return@withContext model.apply { if(model == null) send("接口地址：$url-标头${headers.toQuery()}",true) }
            }
            catch (ex: Exception)
            {
                return@withContext null.apply {  log(0,2, "网络", url,ex.toString())  }
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

    suspend fun getSelf(): Response<Self>
    {
        try {
            val user = get<MUserInfo>("$api/user", null, config.header()) ?: return Response.failed("接口请求失败")
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

    suspend fun getSubscriptionList(type: MediaType, page: Int): Response<SubscriptionList>
    {
        try {
            val param = PageParam(rating = "ecchi", page = page, limit = 32, subscribed = true)
            val response = when(type){
                MediaType.VIDEO -> get<MResult<MVideo>>("$api/videos", param.map(), config.header())?.transform { it.mediaView(file)}
                MediaType.IMAGE -> get<MResult<MImage>>("$api/images", param.map(), config.header())?.transform { it.mediaView(file)}
                MediaType.POST -> get<MResult<MPost>>("$api/posts", param.map(), config.header())?.transform { it.mediaView(file)}
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
    suspend fun getImagePageDetail(imageId: String): Response<ImageDetail>
    {
        try {
            Log.i(TAG, "getImagePageDetail: start load image detail: $imageId")
            val response = get<MImage>("$api/image/$imageId",null, config.header()) ?: return Response.failed("${imageId}为空")
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
    suspend fun getVideoPageDetail(videoId: String): Response<VideoDetail>
    {
        try {
            Log.i(TAG, "getVideoPageDetail: Start load video detail (id:$videoId)")
            val response = get<MVideo>("$api/video/$videoId",null, config.header()) ?: return Response.failed("${videoId}为空")
            val links = response.fileUrl?.let { get<List<MLinkInfo>>(it, null,config.header("x-version" to response.version(salt))) }?.filter { it.name != "preview" }?.apply { forEach { it.src  = MLink("https:"+it.src.view, "https:"+it.src.download)  } } ?: listOf()
            Log.i(TAG, "links:$links")
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
            val related = get<MResult<MVideo>>("$api/videos", PageParam(rating = "ecchi", user = authorId, exclude = response.id, limit = 32).map(), config.header())
//            val related = get<MResult<MVideo>>("$api/video/${response.id}/related",null, session.map())
            // 更多视频
            val moreVideo = related?.results?.map {
                val id = it.id
                val title = it.title
                val pic =  it.getPreviewPic(file)
                val likes = formatCount(it.numLikes)
                val watches = formatCount(it.numViews)
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

    suspend fun like(like: Boolean, likeLink: String): Response<LikeResponse>
    {
        try {
            val response = if(like) post<MLike>(likeLink, config.header()) else delete<MLike>(likeLink, config.header())
            val likeResponse = LikeResponse(status = response != null)
            return Response.success(likeResponse)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }

    suspend fun follow(follow: Boolean, followLink: String): Response<FollowResponse>
    {
        try {
            val response = if(follow) post<MBase>(followLink, config.header()) else delete<MBase>(followLink, config.header())

            val followResponse = FollowResponse(status = response != null)
            return Response.success(followResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            return Response.failed(e.javaClass.name)
        }
    }
    suspend fun getReply(mediaType: MediaType, author: String, mediaId: String, comment: MComment): List<Comment> {
        try {
            if(comment.numReplies == 0) return listOf()
            val param = PageParam(parent = comment.id, page = 0)
            val response = get<MResult<MComment>>("$api/${mediaType.value}/$mediaId/comments", param.map(), config.header())
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
                    posterType= when(authorId){
                        author -> CommentPosterType.OWNER
                        id -> CommentPosterType.SELF
                        else -> CommentPosterType.NORMAL
                    },
                    content = content,
                    date = date,
                    reply = getReply(mediaType, author, mediaId, it)
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
        mediaType: MediaType,
        author: String,
        mediaId: String,
        page: Int
    ): Response<CommentList>
    {
        try {
            val param = PageParam(page = page)
            val response = get<MResult<MComment>>("$api/${mediaType.value}/$mediaId/comments", param.map(), config.header()) ?: return Response.failed("接口请求失败")
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
                        id -> CommentPosterType.SELF
                        else -> CommentPosterType.NORMAL
                    },
                    content = content,
                    date = date,
                    reply = getReply(mediaType, author, mediaId, it)
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

    suspend fun getMediaList(mediaType: MediaType, page: Int, sort: SortType, tags: List<String>): Response<MediaList>
    {
        try {
            val param = PageParam(page = page, rating = "ecchi", sort = sort.value, tags = tags.joinToString(","))
            val result = when(mediaType)
            {
                MediaType.IMAGE -> get<MResult<MImage>>("$api/images", param.map(), config.header())?.transform { it.mediaView(file) }
                MediaType.VIDEO -> get<MResult<MVideo>>("$api/videos", param.map(), config.header())?.transform { it.mediaView(file) }
                else -> get<MResult<MVideo>>("$api/videos", param.map(), config.header())?.transform { it.mediaView(file) }
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
    suspend fun getCount(): Response<MCount>
    {
        try{
            val response = get<MCount>("$api/user/counts", null, config.header()) ?: return Response.failed("接口请求失败")

            return Response.success(response)
        }catch (ex: Exception){
            ex.printStackTrace()
            return Response.failed(ex.javaClass.name)
        }
    }
    suspend fun getUser(username: String): Response<UserData>
    {
        try{
            val response = get<MProfile>("$api/profile/$username", null, config.header()) ?: return Response.failed("接口请求失败")
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

    suspend fun search(query: String, page: Int, type: MediaType): Response<MediaList>
    {
        try {
            val param = PageParam(page = page, type = "${type.value}s" , query = query)
            val result = when(type)
            {
                MediaType.IMAGE -> get<MResult<MImage>>("$api/search", param.map(), config.header())?.transform { it.mediaView(file) }
                MediaType.VIDEO -> get<MResult<MVideo>>("$api/search", param.map(), config.header())?.transform { it.mediaView(file) }
                MediaType.POST ->  get<MResult<MPost>>("$api/search", param.map(), config.header())?.transform { it.mediaView(file) }
                MediaType.USER -> get<MResult<MUser>>("$api/search", param.map(), config.header())?.transform { it.mediaView(file) }
                MediaType.FORUM -> get<MResult<MForum>>("$api/search", param.map(), config.header())?.transform { it.mediaView(file) }
                MediaType.THREAD -> get<MResult<MThread>>("$api/search", param.map(), config.header())?.transform { it.mediaView(file) }
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