package com.ero.iwara.ui.screen.index.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ero.iwara.R
import com.ero.iwara.ui.public.MediaPreviewCard
import com.ero.iwara.ui.screen.index.IndexViewModel
import com.ero.iwara.util.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPage(navController: NavController, indexViewModel: IndexViewModel) {
    val subscriptionList = indexViewModel.subscriptionPager.collectAsLazyPagingItems()
    val refreshState = rememberPullToRefreshState()
    val isRefreshing = subscriptionList.loadState.refresh is LoadState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
        if (subscriptionList.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        subscriptionList.retry()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier
                        .size(160.dp)
                        .padding(10.dp)
                        .clip(CircleShape)) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.anime_1),
                            contentDescription = null
                        )
                    }
                    Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            PullToRefreshBox(isRefreshing = isRefreshing, state = refreshState, onRefresh = { subscriptionList.refresh() })
            {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        count = subscriptionList.itemCount,
                        key = subscriptionList.itemKey { it.id }, // 提供稳定的 key
                        contentType = subscriptionList.itemContentType { "subscriptionItem" } // 提供内容类型
                    ) { index ->
                        val mediaPreview = subscriptionList[index] // 获取项
                        mediaPreview?.let {
                            MediaPreviewCard(navController, it)
                        }
                    }
                    when (subscriptionList.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(Modifier.size(30.dp))
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = "加载中..."
                                    )
                                }
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .noRippleClickable { subscriptionList.retry() }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(140.dp)
                                                .padding(10.dp)
                                                .clip(CircleShape)
                                        ) {
                                            Image(
                                                modifier = Modifier.fillMaxSize(),
                                                painter = painterResource(R.drawable.anime_2),
                                                contentDescription = null
                                            )
                                        }
                                        Text(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            text = "加载失败: ${(subscriptionList.loadState.append as LoadState.Error).error.message}"
                                        )
                                        Text(text = "点击重试")
                                    }
                                }
                            }
                        }

                        is LoadState.NotLoading -> {}
                    }
                }
            }
        }
    }
}