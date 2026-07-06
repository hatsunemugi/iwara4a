package com.ero.iwara

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.subscribe
import com.ero.iwara.ui.local.LocalScreenOrientation
import com.ero.iwara.ui.screen.image.ImageScreen
import com.ero.iwara.ui.screen.index.IndexScreen
import com.ero.iwara.ui.screen.log.LogScreen
import com.ero.iwara.ui.screen.login.LoginScreen
import com.ero.iwara.ui.screen.search.SearchScreen
import com.ero.iwara.ui.screen.splash.SplashScreen
import com.ero.iwara.ui.screen.user.UserScreen
import com.ero.iwara.ui.screen.video.VideoScreen
import com.ero.iwara.ui.theme.IwaraTheme
import com.ero.iwara.util.set
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow

@AndroidEntryPoint
class AppActivity : ComponentActivity() {
    var screenOrientation by mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT)
    val message = MutableSharedFlow<AppEvent.MessageEvent>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalScreenOrientation provides screenOrientation) {
                Index()
            }
        }

    }

    @Composable
    fun Index()
    {
        val context = LocalContext.current
        var toast by remember { mutableStateOf<Toast?>(null) }
        val clipboard = LocalClipboard.current
        IwaraTheme {
            val useDarkIcons = !isSystemInDarkTheme()
            val navController = rememberNavController()
            LaunchedEffect(navController) { // key 是 navController 实例，effect 本身只启动一次
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightStatusBars = useDarkIcons
                controller.isAppearanceLightNavigationBars = useDarkIcons
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                lifecycleScope.subscribe<AppEvent.MessageEvent> {
//                    toast?.cancel()
//                    toast = Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
//                    toast?.show()
//                }
                lifecycleScope.subscribe<AppEvent.Clipboard> {
                    toast?.cancel()
                    clipboard.set(it.message)
                    toast = Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                    toast?.show()
                }
                lifecycleScope.subscribe<AppEvent.RouteEvent> {
                    navController.navigate(it.route)
                }
            }
            NavHost(
                modifier = Modifier.Companion.fillMaxSize(),
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
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
                            type = NavType.Companion.StringType
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
                            type = NavType.Companion.StringType
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
                            type = NavType.Companion.StringType
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

                composable("search") {
                    SearchScreen(navController)
                }

                composable("log") {
                    LogScreen()
                }
            }
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        screenOrientation = newConfig.orientation
    }
}