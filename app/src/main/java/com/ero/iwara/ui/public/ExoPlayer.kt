package com.ero.iwara.ui.public

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ero.iwara.api.result.MLinkInfo
import com.ero.iwara.model.detail.video.VideoDetail
import com.ero.iwara.ui.common.CustomSpinner
import com.ero.iwara.ui.local.LocalScreenOrientation
import com.ero.iwara.util.PlayerListener
import com.ero.iwara.util.cache
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "ExoPlayerCompose"

private fun enterFullScreen(activity: Activity?) {
    activity?.let {
        it.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        val window = it.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

/**
 * 退出全屏：恢复竖屏并显示系统栏
 */
@SuppressLint("SourceLockedOrientationActivity")
private fun exitFullScreen(activity: Activity?) {
    activity?.let {
        it.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        it.requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
    }
}

@Composable
fun VideoPlayer(video: VideoDetail)
{
    val orientation = LocalScreenOrientation.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val links = video.links
    val index = links.indexOfFirst { it.name == "Source" }.let { if (it != -1) it else 0 }
    val context = LocalContext.current
    val exoPlayer = remember(video) {
        ExoPlayer.Builder(context).cache(context).build().apply {
            playWhenReady = false
            if (links.isNotEmpty() && index in links.indices) {
                val firstUrl = links[index].src.view
                if (firstUrl.isNotEmpty()) {
                    Log.i(TAG, "ExoPlayer: Loading Default Video: $firstUrl")
                    setMediaItem(MediaItem.fromUri(firstUrl))
                    prepare() // 准备好第一帧，让画面停在第一帧或者至少蓄势待发
                }
            }
        }
    }
    DisposableEffect(lifecycleOwner, exoPlayer) {
        onDispose {
            Log.i(TAG, "ExoPlayer: Releasing instance $exoPlayer")
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
    ExoPlayerContainer(
        player = exoPlayer,
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
        links = links,
        index = index
    )
}

@Composable
fun ExoPlayerContainer(
    player: ExoPlayer,
    modifier: Modifier = Modifier,
    links: List<MLinkInfo>,
    index: Int,
) {
    val activity = LocalActivity.current

    // 探测当前 Activity 是否是横屏状态
    var isFullScreen by remember { mutableStateOf(false) }

    // 核心重写：如果已经是全屏（横屏），按下系统返回键时拦截它，改成退出全屏
    BackHandler(enabled = isFullScreen) {
        exitFullScreen(activity)
        isFullScreen = false
    }
    var lastIndex by remember { mutableIntStateOf(index) }
    // 状态保持与播放器内核初始化...（保持你之前的代码不变）
    var currentQualityIndex by remember(links, index) {
        mutableIntStateOf(index.coerceIn(0, links.lastIndex.coerceAtLeast(0)))
    }
    var isControllerVisible by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var bufferedPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    DisposableEffect(player) {
        val listener = PlayerListener(
            buffer = { bufferedPosition = it },
            position = { currentPosition = it },
            duration = { duration = it },
            loading = { isLoading = it }
        )
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    LaunchedEffect(links, currentQualityIndex) {
        // 只有用户在看视频的过程中切换了画质，才执行带进度的换源
        if (currentQualityIndex != lastIndex && links.isNotEmpty() && currentQualityIndex in links.indices) {
            val targetUrl = links[currentQualityIndex].src.view
            if (targetUrl.isNotEmpty()) {
                Log.i(TAG, "ExoPlayer: Loading Another Video: $targetUrl")
                val currentPosition = player.currentPosition
                val isVideoPlaying = player.isPlaying

                player.setMediaItem(MediaItem.fromUri(targetUrl))
                player.prepare()

                if (currentPosition > 0) player.seekTo(currentPosition)
                if (isVideoPlaying) player.play()

                lastIndex = currentQualityIndex
            }
        }
    }

    // （此处省略监听器同步和 LaunchedEffect 换源的逻辑，与之前一致）

    Box(
        modifier = modifier
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { isControllerVisible = !isControllerVisible })
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                }
            },
            update = { view -> view.player = player }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center).size(48.dp), // 屏幕中央用 48.dp 大小正合适
                color = Color(0x66FFFFFF),       // 保持经典的红色主题
                strokeWidth = 4.dp
            )
        }

        // 组装解耦后的新面板
        PlayerControlPanel(
            modifier = Modifier.fillMaxSize(),
            isVisible = isControllerVisible,
            isFullScreen = isFullScreen, // 👈 告诉面板现在应该渲染横屏还是竖屏样式
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            bufferedPosition = bufferedPosition,
            links = links,
            currentQualityIndex = currentQualityIndex,
            onPlayPauseToggle = {
                when {
                    isPlaying -> player.pause()
                    else -> player.play()
                }
                isPlaying = !isPlaying
            },
            onSeek = { targetPos ->
                isLoading = true
                player.seekTo(targetPos)
            },
            onQualitySelected = { newIndex ->
                lastIndex = currentQualityIndex
                currentQualityIndex = newIndex
            },
            onFullscreenToggle = {
                // 如果用户在竖屏下点了全屏按钮，进入全屏
                if (!isFullScreen) enterFullScreen(activity)
                isFullScreen = true
            }
        )
    }
}

@Composable
fun PlayerControlPanel(
    modifier: Modifier = Modifier,
    isVisible: Boolean,              // 整体控制栏是否通过点击被呼出
    isFullScreen: Boolean,            // 当前是否是横屏全屏状态
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long = 0L, // 接入你的缓冲进度
    links: List<MLinkInfo>,
    currentQualityIndex: Int,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onQualitySelected: (Int) -> Unit,
    onFullscreenToggle: () -> Unit
) {
    val progressRatioState = remember(currentPosition, duration) {
        derivedStateOf {
            if (duration > 0) currentPosition.toFloat() / duration else 0f
        }
    }

    // 💡 核心拦截器：由于 VideoTimeBar 内部在拖拽时会高频触发 onSeek，
    // 我们在 Panel 层用一个临时变量接管它。只有松手或者点击完成后，才真正传给底层的 ExoPlayer。
    var isInteracting by remember { mutableStateOf(false) }
    var localPositionChange by remember { mutableLongStateOf(0L) }

    // 决定传递给时间条的究竟是 ExoPlayer 的实时位置，还是用户手指搓出来的临时位置
    val displayPosition = if (isInteracting) localPositionChange else currentPosition

    Box(modifier = modifier.fillMaxSize()) {

        // ==========================================
        // 分支 A：竖屏模式（Portrait）
        // ==========================================
        if (!isFullScreen) {

            // 1. 默认常驻底部的超细进度条（当控制面板未呼出时显示）
            AnimatedVisibility(
                visible = !isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                // 💡 修正 1：将其对齐到最底部
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                // 💡 修正 2：加一个防御性的底层 Box 确保它绝不会被切掉，同时安全地避开可能存在的系统底栏
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // 👈 如果你有沉浸式底栏，这行能把它顶上去，防止被白条挡住
                        .padding(bottom = 1.dp)   // 👈 微微往上提 1dp 绝杀各种剪裁边缘
                ) {
                    LinearProgressIndicator(
                        // 💡 修正 3：如果你的 progressRatio 上层不是 DerivedState，
                        // 建议先直接用普通入参形式 progress = progressRatio 确保能触发重组渲染。
                        // 如果你想保持 lambda 性能，必须确保 progressRatio 是由 deriveStateOf 包装的。
                        progress = { progressRatioState.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp), // 👈 稍微加粗到 3dp，2dp 在高分辨率屏幕（如平板）上真的一不留神就看不见
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }

            // 2. 点击呼出的完整竖屏控制面板
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    // 底部控制：进度条 + 全屏按钮
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f))
                            // 💡 修正 4：完整面板的底部也必须加上系统底栏的 Padding
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 中间播放暂停
                        IconButton(onClick = onPlayPauseToggle) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // 💡 1. 竖屏直接使用你的 VideoTimeBar
                        VideoTimeBar(
                            position = displayPosition,
                            duration = duration,
                            bufferedPosition = bufferedPosition,
                            onSeek = { targetPosition ->
                                // 手指触碰/点击时，立刻锁定状态，不让外部的 currentPosition 干扰滑块
                                isInteracting = true
                                localPositionChange = targetPosition

                                // 💡 如果你的 VideoTimeBar 内部 Slider 缺少 onValueChangeFinished，
                                // 也可以选择在这里直接触发 onSeek，但为了防抖，建议配合 PointerInput 监听松手。
                                // 如果只是点击或者短距离拖拽，可以直接向外同步：
                                onSeek(targetPosition)
                                isInteracting = false
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onFullscreenToggle) {
                            Icon(Icons.Default.Fullscreen, "Full", tint = Color.White)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 分支 B：横屏全屏模式（Landscape）
        // ==========================================
        if (isFullScreen) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                // 💡 修正 1：整个控制面板应该自带有层次的渐变黑底，而不是一刀切的纯灰蒙层
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                ) {
                    // 1. 上方时间显示
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .statusBarsPadding() // 💡 避开刘海屏或状态栏
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {

                    }

                    // 💡 修正 2：将进度条、播放键、画质选项合并放入底下一体化控制区
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .navigationBarsPadding() // 💡 适配横屏下的虚拟导航栏
                            .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 12.dp)
                    ) {
                        Text(
                            text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        // 🎯 进度条直接架在按钮上面，热区和位置都非常自然
                        VideoTimeBar(
                            position = currentPosition, // 💡 彻底卸下包袱，内部封装了防抖，外面直接传当前内核进度
                            duration = duration,
                            bufferedPosition = bufferedPosition,
                            onSeek = onSeek,            // 💡 直接透传，松手的那一下才会真正回调
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 🎯 底部按钮行
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 左下角：开始/暂停
                            IconButton(
                                onClick = onPlayPauseToggle,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // 右下角：画质选择
                            if (links.isNotEmpty() && currentQualityIndex in links.indices) {
                                CustomSpinner(
                                    modifier = Modifier.wrapContentSize(), // 让宽度自适应，不要写死 64.dp 导致文字被切
                                    items = links,
                                    selectedItem = links[currentQualityIndex],
                                    onItemSelected = { item ->
                                        val targetIndex = links.indexOf(item)
                                        if (targetIndex != -1) onQualitySelected(targetIndex)
                                    },
                                    itemToString = { it.name }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoTimeBar(
    position: Long,
    duration: Long,
    bufferedPosition: Long, // 💡 支持像原生那样的缓冲条
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    // 监听手势：是否正在被拖拽或按下
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = isDragged || isPressed


    // 动画缩放：平时滑块小或者隐藏，按住时放大（复刻原生触感）
    val thumbScale = if (isActive) 0.8f else 0.6f

    // 颜色配置（精准对齐 ExoPlayer 原生经典红白/红灰配色）
    val playedColor = Color(0xFFE50914)     // 已播放：经典高亮红（或本地主题色）
    val bufferedColor = Color(0x66FFFFFF) // 已缓冲：半透明白/浅灰
    val unplayedColor = Color(0x33FFFFFF) // 未播放：深底色

    val totalTime = duration.coerceAtLeast(1L)
    var localSliderValue by remember { mutableFloatStateOf(0f) }

    // 💡 绝杀：当用户没有触碰时，本地滑块强制对齐 ExoPlayer 传进来的真实物理进度
    if (!isActive) {
        localSliderValue = position.toFloat().coerceIn(0f, totalTime.toFloat())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp), // 扩大点击热区，防止滑块太小点不动
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = localSliderValue,
            onValueChange = { newValue ->
                // 手指滑到哪，本地 UI 就跟到哪，绝不往外高频发 seek
                localSliderValue = newValue
            },
            onValueChangeFinished = {
                onSeek(localSliderValue.toLong())
            },
            valueRange = 0f..totalTime.toFloat(),
            interactionSource = interactionSource,
            // 💡 1. 自定义滑块 (Thumb)
            thumb = {
                Surface(
                    shape = CircleShape,
                    color = playedColor,
                    modifier = Modifier
                        .size(12.dp) // 原生滑块非常小巧
                        .align(Alignment.Center)
                        .graphicsLayer {
                            scaleX = thumbScale
                            scaleY = thumbScale
                            transformOrigin = TransformOrigin.Center
                        }
                ) {}
            },
            // 💡 2. 自定义轨道 (Track)
            // 💡 2. 自定义轨道 (Track) —— 核心支持缓冲条
            track = { sliderState ->
                // 计算各个线段的比例
                val total = totalTime.toFloat()
                val playedFraction = (sliderState.value / total).coerceIn(0f, 1f)
                val bufferedFraction = (bufferedPosition.toFloat() / total).coerceIn(0f, 1f)

                // 💡 原生 TimeBar 绝活：平时细（4.dp），激活时变粗（6.dp），这里用动画或手势直接控制高度
                val trackHeight = if (isActive) 5.dp else 3.dp

                // 我们直接用一个自定义的 Canvas 绘制三层线段，极其干净且不失真
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(trackHeight)
                ) {
                    val width = size.width
                    val height = size.height
                    val radius = height / 2f // 两头圆角

                    // 1. 绘制底色：未播放区域 (全长)
                    drawRoundRect(
                        color = unplayedColor,
                        topLeft = Offset(0f, 0f),
                        size = size,
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // 2. 绘制中间层：已缓冲区域 (从 0 到 bufferedFraction)
                    if (bufferedFraction > 0f) {
                        drawRoundRect(
                            color = bufferedColor,
                            topLeft = Offset(0f, 0f),
                            size = size.copy(width = width * bufferedFraction),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                    }

                    // 3. 绘制最上层：已播放区域 (从 0 到 playedFraction)
                    if (playedFraction > 0f) {
                        drawRoundRect(
                            color = playedColor,
                            topLeft = Offset(0f, 0f),
                            size = size.copy(width = width * playedFraction),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun formatTime(ms: Long): String {
    val duration = ms.milliseconds
    return duration.toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            // 如果超过一小时
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            // 普通分钟:秒
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
    }
}