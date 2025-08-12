package com.ero.iwara.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.ero.iwara.ui.local.LocalScreenOrientation
import com.ero.iwara.ui.screen.image.ImageScreen
import com.ero.iwara.ui.screen.search.SearchScreen
import com.ero.iwara.ui.screen.user.UserScreen
import com.ero.iwara.ui.screen.video.VideoScreen
import com.ero.iwara.ui.theme.IwaraTheme
import com.ero.iwara.ui.screen.index.IndexScreen
import com.ero.iwara.ui.screen.login.LoginScreen
import com.ero.iwara.ui.screen.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var screenOrientation by mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContent {
            val useDarkIcons = !isSystemInDarkTheme()
            DisposableEffect(useDarkIcons) { // key 为 useDarkIcons，当主题切换时会重新执行
                // 2. 获取 WindowInsetsControllerCompat
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                // 5. 控制状态栏内容的颜色 (图标等)
                // true 表示浅色背景，需要深色图标；false 表示深色背景，需要浅色图标
                controller.isAppearanceLightStatusBars = useDarkIcons

                // 6. 控制导航栏内容的颜色 (按钮等)
                // true 表示浅色背景，需要深色按钮；false 表示深色背景，需要浅色按钮
                // 注意：导航栏图标颜色的可定制性在不同 Android 版本和设备上可能有限
                controller.isAppearanceLightNavigationBars = useDarkIcons


                // 如果需要完全隐藏系统栏 (通常用于特定全屏内容，如视频播放)
                 controller.hide(WindowInsetsCompat.Type.statusBars())
                 controller.hide(WindowInsetsCompat.Type.navigationBars())
                 controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                onDispose {
                    // 可选：如果需要在 Composable 离开时恢复默认设置，可以在这里操作
                    // 对于 Activity 级别的设置，通常不需要
                }
            }
            CompositionLocalProvider(LocalScreenOrientation provides screenOrientation) {
                Index()
            }
        }
    }
    @Preview
    @Composable
    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
    fun Index()
    {
        IwaraTheme {
            val navController = rememberNavController()
            WindowCompat.getInsetsController(window, window.decorView)
                .isAppearanceLightStatusBars = !isSystemInDarkTheme()

            NavHost(modifier = Modifier.fillMaxSize(), navController = navController, startDestination = "splash") {
                composable("splash"){
                    SplashScreen(navController)
                }

                composable("index") {
                    IndexScreen(navController)
                }

                composable("login") {
                    LoginScreen(navController)
                }

                composable(
                    route = "video/{videoId}",
                    arguments = listOf(
                        navArgument("videoId") {
                            type = NavType.StringType
                        }
                    ),
                    deepLinks = listOf(
                        navDeepLink { // <--- 使用 navDeepLink DSL 构建器
                            uriPattern = "https://www.iwara.tv/video/{videoId}"
                            // action = "ACTION_VIEW" // 可选，如果需要指定 action
                            // mimeType = "type/subtype" // 可选，如果需要指定 mimeType
                        }
                    )
                ) {
                    VideoScreen(navController, it.arguments?.getString("videoId")!!)
                }


                composable(
                    route = "image/{imageId}",
                    arguments = listOf(
                        navArgument("imageId") {
                            type = NavType.StringType
                        }
                    ),
                    deepLinks = listOf(
                        navDeepLink { // <--- 使用 navDeepLink DSL 构建器
                            uriPattern = "https://www.iwara.tv/image/{imageId}"
                        }
                    )
                ) {
                    ImageScreen(navController, it.arguments?.getString("imageId")!!)
                }


                composable(
                    route = "user/{username}",
                    arguments = listOf(
                        navArgument("username") {
                            type = NavType.StringType
                        }
                    ),
                    deepLinks = listOf(
                        navDeepLink { // <--- 使用 navDeepLink DSL 构建器
                            uriPattern = "https://www.iwara.tv/profile/{username}"
                        }
                    )
                ) {
                    UserScreen(navController, it.arguments?.getString("username")!!)
                }

                composable("search"){
                    SearchScreen(navController)
                }
            }
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        screenOrientation = newConfig.orientation
    }
}