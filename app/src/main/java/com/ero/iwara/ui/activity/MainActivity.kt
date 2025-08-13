package com.ero.iwara.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContent {
            CompositionLocalProvider(LocalScreenOrientation provides screenOrientation) {
                Index()
            }
        }
    }
    @Composable
    fun Index()
    {
        IwaraTheme {
            val useDarkIcons = !isSystemInDarkTheme()
            val navController = rememberNavController()
            LaunchedEffect(navController) { // key 是 navController 实例，effect 本身只启动一次
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightStatusBars = useDarkIcons
                controller.isAppearanceLightNavigationBars = useDarkIcons
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                navController.currentBackStackEntryFlow.collect { backStackEntry ->
//                    // 这个 collect 块会在每次导航事件导致 backStackEntryFlow 发出新值时执行
//
//                }
            }
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