package com.ero.iwara.ui.screen.user

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.ero.iwara.R
import com.ero.iwara.model.user.UserData
import com.ero.iwara.ui.public.FullScreenTopBar
import com.ero.iwara.ui.public.TabItem
import com.ero.iwara.ui.theme.PINK
import com.ero.iwara.util.noRippleClickable


@ExperimentalAnimationApi
@Composable
fun UserScreen(
    navController: NavController,
    username: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        userViewModel.load(username)
    }

    Scaffold(
        topBar = {
            TopBar(navController, userViewModel)
        }
    )
    {
        Box(modifier = Modifier.padding(it).fillMaxSize()){
            if (userViewModel.isLoaded()) {
                UserInfo(navController, userViewModel.userData)
            } else if(userViewModel.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = "加载中", fontWeight = FontWeight.Bold)
                    }
                }
            } else if(userViewModel.error){
                Box(modifier = Modifier.fillMaxSize().noRippleClickable { userViewModel.load(username) }, contentAlignment = Alignment.Center) {
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
                        Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
@Composable
private fun UserInfo(navController: NavController, userData: UserData) {
    Column {
        // 用户信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = userData.pic,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = null
                        )
                    }

                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = userData.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PINK
                        )
                        Text(text = "注册日期: ${userData.joinDate}",color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "最后在线: ${userData.lastSeen}",color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = userData.about, maxLines = 5,color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        // 评论/ 视频 / 图片
        val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = {3})
        com.ero.iwara.ui.public.TabRow {
            TabItem(pagerState = pagerState, index = 0, text = "评论")
            TabItem(pagerState = pagerState, index = 1, text = "发布的视频")
            TabItem(pagerState = pagerState, index = 2, text = "发布的图片")
        }
        HorizontalPager(modifier = Modifier.fillMaxWidth().weight(1f), state = pagerState) {
            when(it){
                0 -> {
                    Box(modifier = Modifier.fillMaxSize()){

                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize()){

                    }
                }
                2 -> {
                    Box(modifier = Modifier.fillMaxSize()){

                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController, userViewModel: UserViewModel) {
    FullScreenTopBar(
        modifier = Modifier.height(32.dp),
        title = {
            Text(text = if(userViewModel.isLoaded()) userViewModel.userData.username else "用户信息")
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }
    )
}