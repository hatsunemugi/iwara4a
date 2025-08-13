package com.ero.iwara.ui.screen.video

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.ero.iwara.R
import com.ero.iwara.model.detail.video.VideoDetail
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.ui.local.LocalScreenOrientation
import com.ero.iwara.ui.public.CommentItem
import com.ero.iwara.ui.public.ExoPlayer3
import com.ero.iwara.ui.public.FullScreenTopBar
import com.ero.iwara.ui.public.TabItem
import com.ero.iwara.ui.theme.PINK
import com.ero.iwara.util.noRippleClickable
import com.ero.iwara.util.shareMedia

@Composable
fun VideoScreen(
    navController: NavController,
    videoId: String,
    videoViewModel: VideoViewModel = hiltViewModel()
) {
    val orientation = LocalScreenOrientation.current
    val context = LocalActivity.current

    val isVideoLoaded by remember { derivedStateOf { videoViewModel.videoDetail != VideoDetail.LOADING && !videoViewModel.error && !videoViewModel.isLoading } }
    val getTitle by remember { derivedStateOf { if (videoViewModel.isLoading) "加载中" else if (isVideoLoaded) videoViewModel.videoDetail.title else if (videoViewModel.error) "加载失败" else "视频页面" } }
    val videoLink by remember { derivedStateOf { videoViewModel.videoDetail.links.firstOrNull()?.src?.view ?: "" } }

    // 加载视频
    LaunchedEffect(Unit) {
        videoViewModel.loadVideo(videoId)
    }

    // 响应旋转
    BackHandler(isVideoLoaded && orientation == Configuration.ORIENTATION_LANDSCAPE) {
        context?.let { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    Scaffold(
        topBar = {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                FullScreenTopBar(
                    modifier = Modifier.height(32.dp),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    title = {
                        Text(text = getTitle, maxLines = 1)
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            ExoPlayer3(
                modifier = if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .requiredHeightIn(max = 210.dp)
                        .background(Color.Black)
                else
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                videoLink = videoLink
            )

            when {
                isVideoLoaded -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        VideoInfo(navController, videoViewModel, videoViewModel.videoDetail)

                    }
                }
                videoViewModel.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(text = "加载中")
                        }
                    }
                }
                videoViewModel.error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { videoViewModel.loadVideo(videoId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .padding(10.dp)
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.anime_4),
                                    contentDescription = null
                                )
                            }
                            Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemUiController(
    activity: Activity?,
    view: View,
    orientation: Int,
    isVideoLoaded: Boolean
) {
    val window = activity?.window ?: return
    val insetsController = remember(window, view) { // 记住 insetsController
        WindowCompat.getInsetsController(window, view)
    }

    LaunchedEffect(orientation, isVideoLoaded, insetsController) {
        if (isVideoLoaded) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        } else {
            // 确保在视频未加载或加载失败时，系统栏是可见的
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Composable
private fun VideoInfo(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val pagerState = rememberPagerState(pageCount = {2}, initialPage = 0)
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(pagerState, 0, "简介")
            TabItem(pagerState, 1, "评论")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth(),
                state = pagerState
            ) {
                when (it) {
                    0 -> VideoDescription(navController, videoViewModel, videoDetail)
                    1 -> CommentPage(navController, videoViewModel)
                }
            }
        }
    }
}

@Composable
private fun VideoDescription(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            // 视频简介
            Card(modifier = Modifier.padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(
                    modifier = Modifier
                        .animateContentSize()
                        .padding(16.dp)
                ) {
                    // 作者信息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 作者头像
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .noRippleClickable {
                                    navController.navigate("user/${videoDetail.authorName}")
                                }
                        ) {
                            AsyncImage(
                                model = videoDetail.authorPic,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = null
                            )
                        }

                        // 作者名字
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .noRippleClickable {
                                    navController.navigate("user/${videoDetail.authorName}")
                                },
                            text = videoDetail.authorNickname,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = PINK
                        )

                        // 关注
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    videoViewModel.handleFollow { action, success ->
                                        if (action) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "关注了该UP主！ ヾ(≧▽≦*)o" else "关注失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "已取消关注" else "取消关注失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                }
                                .background(
                                    if (videoDetail.follow) Color.LightGray else Color(
                                        0xfff45a8d
                                    )
                                )
                                .padding(4.dp),
                        ) {
                            Text(
                                text = if (videoDetail.follow) "已关注" else "+ 关注",
                                color = if (videoDetail.follow) Color.Black else Color.White
                            )
                        }
                    }
                    // 视频信息
                    Row(Modifier.padding(vertical = 4.dp)) {
                        Text(text = "播放: ${videoDetail.watches} 喜欢: ${videoDetail.likes}")
                    }

                    // 视频介绍
                    var expand by remember {
                        mutableStateOf(false)
                    }
                    Crossfade(expand) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = videoDetail.description,
                                maxLines = if (expand) 10 else 1,
                                color= MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .noRippleClickable { expand = !expand }
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    modifier = Modifier.size(20.dp),
                                    onClick = { expand = !expand }) {
                                    Icon(
                                        imageVector = if (it) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }

                    // 操作按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    videoViewModel.handleLike { action, success ->
                                        if (action) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "点赞大成功！ ヾ(≧▽≦*)o" else "点赞失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    if (success) "已取消点赞" else "取消点赞失败",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (videoDetail.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (videoDetail.isLike) Color(0xfff45a8d) else Color.LightGray
                            )
                            Text(text = if (videoDetail.isLike) "已喜欢" else "喜欢")
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Subscriptions, null)
                            Text(text = "收藏")
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { shareMedia(context, MediaType.VIDEO, videoDetail.id) },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Share, null)
                            Text(text = "分享")
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { }, horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Download, null)
                            Text(text = "下载")
                        }
                    }
                }
            }
        }
        // 更多视频
        item {
            Text(
                text = "该作者的其他视频:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(videoDetail.moreVideo) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            println(it.id)
                            navController.navigate("video/${it.id}")
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        AsyncImage(
                            model = it.pic,
                            modifier = Modifier.fillMaxHeight(),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = it.title, fontWeight = FontWeight.Bold)
                        Text(text = "播放: ${it.watches} 喜欢: ${it.likes}",color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentPage(navController: NavController, videoViewModel: VideoViewModel) {
    val pager = videoViewModel.commentPager.collectAsLazyPagingItems()
    val state = rememberPullToRefreshState()
    val isRefresh = pager.loadState.refresh is LoadState.Loading
    if (pager.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { pager.retry() }, contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .padding(10.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(R.drawable.anime_4),
                        contentDescription = null
                    )
                }
                Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Column(Modifier.fillMaxSize()) {
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = state,
                onRefresh = { pager.refresh() },
                isRefreshing = isRefresh,
            )
            {
                LazyColumn(modifier = Modifier.fillMaxSize())
                {
                    if (pager.itemCount == 0 && pager.loadState.refresh is LoadState.NotLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(text = "暂无评论", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(
                        count = pager.itemCount,
                        key = pager.itemKey { it.id }, // 提供稳定的 key
                        contentType = pager.itemContentType { "commentItem" } // 提供内容类型
                    ) { index ->
                        val comment = pager[index] // 获取项
                        comment?.let {
                            CommentItem(navController, it)
                        }
                    }

                    when (pager.loadState.append) {
                        LoadState.Loading -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Text(text = "加载中", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .noRippleClickable { pager.retry() }
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        is LoadState.NotLoading -> {}
                    }
                }
            }
            ReplyBox()
        }
    }
}

@Composable
fun ReplyBox() {
    var content by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier.weight(1f),
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(text = "回复请注意礼仪哦~")
                },
                maxLines = 3,
                label = {
                    Text(text = "评论视频")
                }
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.EmojiEmotions, null)
            }
        }
    }
}