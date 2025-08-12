package com.ero.iwara.ui.screen.search

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.ui.public.FullScreenTopBar
import com.ero.iwara.ui.public.MediaPreviewCard
import com.ero.iwara.ui.public.QueryParamSelector
import com.ero.iwara.util.HandleMessage
import com.ero.iwara.util.noRippleClickable

@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            FullScreenTopBar(
                modifier = Modifier.height(48.dp),
                title = {
                    Text(text = "搜索")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        val result = searchViewModel.pager.collectAsLazyPagingItems()

        Column(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            SearchBar(searchViewModel, result)
            Result(navController, searchViewModel, result)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Result(
    navController: NavController,
    searchViewModel: SearchViewModel,
    list: LazyPagingItems<MediaPreview>
) {
    HandleMessage(searchViewModel.message)
    if (list.loadState.refresh !is LoadState.Error) {
        Crossfade(searchViewModel.query) {
            if (it.isNotBlank()) {
                PullToRefreshBox(
                    isRefreshing = list.loadState.refresh == LoadState.Loading,
                    state = rememberPullToRefreshState(),
                    onRefresh = { list.refresh() }
                ) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        item {
                            QueryParamSelector(
                                "类型",
                                current = MediaType.VIDEO,
                                list = MediaType.entries,
                                onChangeType = { it ->
                                    searchViewModel.searchParam.type = it
                                    list.refresh()
                                }
                            )
                        }
                        
                        items(
                            count = list.itemCount,
                            key = list.itemKey { it -> it.id }, // 提供稳定的 key
                            contentType = list.itemContentType { "searchItem" } // 提供内容类型
                        ) { index ->
                            val mediaPreview = list[index] // 获取项
                            mediaPreview?.let {
                                MediaPreviewCard(navController, mediaPreview)
                            }
                        }
                        when (list.loadState.append) {
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
                                            .noRippleClickable { list.retry() }
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
            } else {
                // 也许可以加个搜索推荐？
            }
        }
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { list.refresh() }, contentAlignment = Alignment.Center
        ) {
            Text(text = "加载错误，点击重新尝试搜索", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchRecommend(text: String, onClick: (text: String) -> Unit) {
    Box(modifier = Modifier
        .padding(horizontal = 8.dp)
        .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
        .clickable { onClick(text) }
        .padding(4.dp)) {
        Text(text = text)
    }
}

@Composable
private fun SearchBar(searchViewModel: SearchViewModel, list: LazyPagingItems<MediaPreview>) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Card(modifier = Modifier.padding(8.dp), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(6.dp)) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchViewModel.query,
                    onValueChange = { searchViewModel.query = it.replace("\n", "") },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "搜索视频和图片")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent, // 如果你需要处理错误状态

                        // --- 指示器颜色 ---
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent, // 如果你需要处理错误状态
                    ),
                    trailingIcon = {
                        if (searchViewModel.query.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.query = "" }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchViewModel.query.isBlank()) {
                                Toast.makeText(context, "不能搜索空内容哦！", Toast.LENGTH_SHORT).show()
                            } else {
                                focusManager.clearFocus()
                                list.refresh()
                            }
                        }
                    )
                )
            }
            IconButton(onClick = {
                if (searchViewModel.query.isBlank()) {
                    Toast.makeText(context, "不能搜索空内容哦！", Toast.LENGTH_SHORT).show()
                } else {
                    focusManager.clearFocus()
                    list.refresh()
                }
            }) {
                Icon(Icons.Default.Search, null)
            }
        }
    }
}