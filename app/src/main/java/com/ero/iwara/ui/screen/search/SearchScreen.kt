package com.ero.iwara.ui.screen.search

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
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
import com.ero.iwara.ui.public.MediaPreviewCard
import com.ero.iwara.ui.public.SearchBar
import com.ero.iwara.ui.public.TypeSelector
import com.ero.iwara.util.noRippleClickable
import com.ero.iwara.util.send
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel = hiltViewModel()) {
    val result = searchViewModel.pager.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            SearchBar(navController, searchViewModel, result)
        }
    ) {
        Result(it, navController, searchViewModel, result)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Result(
    padding: PaddingValues,
    navController: NavController,
    searchViewModel: SearchViewModel,
    list: LazyPagingItems<MediaPreview>
) {
    if (list.loadState.refresh !is LoadState.Error) {
        Crossfade(searchViewModel.query) {
            if (it.isNotBlank()) {
                PullToRefreshBox(
                    isRefreshing = list.loadState.refresh == LoadState.Loading,
                    state = rememberPullToRefreshState(),
                    onRefresh = { list.refresh() }
                ) {
                    LazyColumn(Modifier.padding(padding).fillMaxSize()) {
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
fun SearchBar(navController: NavController, searchViewModel: SearchViewModel, list: LazyPagingItems<MediaPreview>)
{
    var type by remember { mutableStateOf(searchViewModel.searchParam.type) }
    val dialog = rememberMaterialDialogState()
    val manager = LocalFocusManager.current
    val border = if(isSystemInDarkTheme()) Color.White else Color.Black
    MaterialDialog(dialog) {
        TypeSelector("类型", type, MediaType.entries) {
            type = MediaType.entries[it]
            searchViewModel.searchParam.type = type
            list.refresh()
            dialog.hide()
        }
    }
    Row(modifier = Modifier.statusBarsPadding().fillMaxWidth().height(56.dp),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(modifier = Modifier.size(32.dp), onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }
        Box(
            modifier = Modifier
                .clickable {
                    dialog.show()
                }
                .border(BorderStroke(1.dp, border), RoundedCornerShape(2.dp))
                .padding(horizontal = 8.dp)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = type.name)
        }
        SearchBar(Modifier.weight(1f).height(56.dp), manager)
        {
            searchViewModel.query = it
            if (it.isBlank()) {
                send("不能搜索空内容哦！")
            } else {
                list.refresh()
            }
        }
        IconButton(modifier = Modifier.size(32.dp), onClick = {
            manager.clearFocus()
        }) {
            Icon(Icons.Default.Search, null)
        }
    }
}

