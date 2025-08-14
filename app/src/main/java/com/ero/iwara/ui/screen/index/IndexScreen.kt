package com.ero.iwara.ui.screen.index

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ero.iwara.R
import com.ero.iwara.ui.public.IndexBar
import com.ero.iwara.ui.screen.index.page.ImageListPage
import com.ero.iwara.ui.screen.index.page.SubPage
import com.ero.iwara.ui.screen.index.page.VideoListPage
import kotlinx.coroutines.launch

@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    indexViewModel.page = { pagerState.currentPage }
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
            topBar = { IndexBar(indexViewModel, navController) },
            bottomBar = {
                BottomBar(pagerState = pagerState)
            }
        )
        {
            HorizontalPager(
                modifier = Modifier.fillMaxSize().padding(it),
                state = pagerState
            ) { it ->
                when (it) {
                    0 -> {
                        VideoListPage(navController, { it ->
                            indexViewModel.video = { it(indexViewModel.sort, indexViewModel.tags) }
                        })
                    }
                    1 -> {
                        SubPage(navController, { it ->
                            indexViewModel.sub = { it(indexViewModel.type) }
                        })
                    }
                    2 -> {
                        ImageListPage(navController,{ it ->
                            indexViewModel.image = { it(indexViewModel.sort, indexViewModel.tags) }
                        })
                    }
                }
            }
        }
    }
}
@Composable
private fun BottomBar(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    // Define your navigation items in a list for easier management
    val navItems = listOf(
        NavigationItem(name = "视频", icon = R.drawable.video_icon),
        NavigationItem(name = "关注", icon = R.drawable.subscriptions),
        NavigationItem(name = "图片", icon = R.drawable.image_icon)
    )
    NavigationBar(modifier = Modifier)
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
                }
            )
        }
    }
}