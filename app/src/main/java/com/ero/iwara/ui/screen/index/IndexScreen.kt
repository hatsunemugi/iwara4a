package com.ero.iwara.ui.screen.index

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import com.ero.iwara.R
import com.ero.iwara.ui.public.FullScreenTopBar
import com.ero.iwara.ui.screen.index.page.ImageListPage
import com.ero.iwara.ui.screen.index.page.SubPage
import com.ero.iwara.ui.screen.index.page.VideoListPage
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { Int.MAX_VALUE })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { // Provides standard M3 drawer visuals and handles gestures
                IndexDrawer(navController = navController, indexViewModel = indexViewModel)
            }
        },
        gesturesEnabled = true // Enable swipe gestures to open/close the drawer
    )
    {
        Scaffold(
            topBar = { TopBar(drawerState, indexViewModel, navController) },
            bottomBar = {
                BottomBar(pagerState = pagerState)
            }
        )
        {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                state = pagerState
            ) { it ->
                when (it) {
                    0 -> {
                        VideoListPage(navController, indexViewModel)
                    }
                    1 -> {
                        SubPage(navController, indexViewModel)
                    }
                    2 -> {
                        ImageListPage(navController, indexViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun TopBar(drawerState: DrawerState, indexViewModel: IndexViewModel, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    FullScreenTopBar(
        modifier = Modifier.height(32.dp),
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }) {
                Box(modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)) {
                    AsyncImage(
                        model = indexViewModel.self.profilePic,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Default.Search, null )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomBar(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    // Define your navigation items in a list for easier management
    val navItems = listOf(
        NavigationItem(name = "视频", icon = R.drawable.video_icon),
        NavigationItem(name = "关注", icon = R.drawable.subscriptions),
        NavigationItem(name = "图片", icon = R.drawable.image_icon)
    )
    NavigationBar(modifier = Modifier,
        // Add if you are not using Scaffold or if it's explicitly needed.
        containerColor = MaterialTheme.colorScheme.surface)
    {
        navItems.forEachIndexed { index, data -> // screenData is your NavigationItem instance
            val selected = pagerState.currentPage == index // Use appropriate page index

            NavigationBarItem( // Explicitly use M3 NavigationBarItem
                selected = selected,
                onClick = {
                    if (pagerState.currentPage != index) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = data.icon),
                        contentDescription = data.name
                    )
                },
                label = {
                    Text(text = data.name)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}