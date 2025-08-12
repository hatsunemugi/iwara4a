package com.ero.iwara.ui.screen.index.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.ero.iwara.R
import com.ero.iwara.result.MTag
import com.ero.iwara.ui.public.TagItem
import com.ero.iwara.util.noRippleClickable

@Preview
@Composable
fun TagItemComposablePreview() { // 改名以避免与 LazyColumn items 块中的变量冲突
    TagItem(MTag("cc","dd",true)){}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagListPage(items: LazyPagingItems<MTag>, onClick: (MTag)-> Unit) {
//    val tagListItems = indexViewModel.tagPager.collectAsLazyPagingItems() // 重命名以更清晰
    val isRefreshing by remember { derivedStateOf { items.loadState.refresh is LoadState.Loading } }
    val refreshState = rememberPullToRefreshState() // M3 的 PullToRefresh
    Box(modifier = Modifier.fillMaxSize()) {
        // 初始加载错误且没有数据时
        if (items.loadState.refresh is LoadState.Error && items.itemCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable { // 确保你有这个 Modifier 扩展
                        items.retry()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .padding(10.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.anime_1), // 确保资源存在
                            contentDescription = null
                        )
                    }
                    Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            PullToRefreshBox( // M3 PullToRefreshBox
                isRefreshing = isRefreshing,
                state = refreshState,
                onRefresh = { items.refresh() },
                // modifier = Modifier, // PullToRefreshBox 通常不需要自己的 Modifier.fillMaxSize()
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        // 使用 M3 默认指示器
                        state = refreshState,
                        isRefreshing = isRefreshing,
//                        modifier = Modifier.align(Alignment.TopCenter), // 可以明确指定，但通常默认
                        // containerColor = MaterialTheme.colorScheme.primaryContainer, // 可以自定义
                        // color = MaterialTheme.colorScheme.onPrimaryContainer        // 可以自定义
                    )
                })
            { // 这个 lambda 是 PullToRefreshBox 的内容
                // 初始加载中且没有数据时
                if (items.loadState.refresh is LoadState.Loading && items.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // 加载完成但没有数据时
                else if (items.itemCount == 0 && items.loadState.refresh is LoadState.NotLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("没有找到相关标签~")
                    }
                }
                // 有数据或正在加载（但已有部分数据）
                else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()) // FlowRow 需要外部滚动容器
                    ) {
                        FlowRow(
                            modifier = Modifier
                                .padding(16.dp) // 给 FlowRow 一些内边距
                                .fillMaxWidth(), // 让 FlowRow 占据可用宽度
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // 标签之间的水平间距
                            verticalArrangement = Arrangement.spacedBy(8.dp),   // 标签行之间的垂直间距
                            // maxItemsInEachRow = 5, // 如果你想限制每行的最大项目数 (可选)
                        ) {
                            // 遍历当前 LazyPagingItems 中已加载的项
                            for (index in 0 until items.itemCount) {
                                val tagData = items.peek(index) // 使用 peek 获取项，避免触发加载
                                tagData?.let {
                                    TagItem(tag = it /* 这里应该是 MTag 类型 */) { tag -> // 处理标签点击事件
                                        // 例如: navController.navigate("tagDetails/${clickedTag.id}")
                                        onClick(tag)
                                    }
                                }
                            }
                        }
                        // 分页加载状态 (append) - 显示在 FlowRow 下方
                        when (val appendState = items.loadState.append) {
                            is LoadState.Loading -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
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
                            is LoadState.Error -> {
                                Column( // 改为 Column 使内容垂直排列
                                    modifier = Modifier
                                        .fillMaxWidth() // 改为 fillMaxWidth
                                        .padding(16.dp), // 增加内边距
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                                ) {
                                    // 可以选择是否显示图片
                                     Box(
                                         modifier = Modifier
                                             .size(100.dp) // 缩小图片
                                             .clip(CircleShape)
                                     ) {
                                         Image(
                                             modifier = Modifier.fillMaxSize(),
                                             painter = painterResource(R.drawable.anime_2), // 确保资源存在
                                             contentDescription = null
                                         )
                                     }
                                    Text(
                                        text = "加载更多失败: ${appendState.error.message}",
                                        textAlign = TextAlign.Center,
                                    )
                                    Button(onClick = { items.retry() }) {
                                        Text(text = "点击重试")
                                    }
                                }
                            }
                            is LoadState.NotLoading -> {
                                if (appendState.endOfPaginationReached && items.itemCount > 0) {
                                    Text(
                                        text = "没有更多标签了~",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                // 如果不是末尾，你可能需要一个“加载更多”按钮，
                                // 因为 FlowRow 不会自动触发 PagingDataAdapter 的进一步加载。
                                else if (!appendState.endOfPaginationReached && items.itemCount > 0) {
                                    Button(
                                        onClick = {
                                            // 尝试通过访问最后一个元素附近来触发加载 (不保证在所有情况下都可靠)
                                            // 更好的方式是如果你的 PagingSource 有方法可以显式加载下一页，
                                            // 或者 ViewModel 提供一个 loadNextPage() 方法。
                                            // 对于 LazyPagingItems，通常访问接近末尾的项会触发。
                                            // if (tagListItems.itemCount > 0) {
                                            //     tagListItems.getAsState(tagListItems.itemCount - 1)
                                            // }
                                            items.retry()
                                            println("Load More button clicked - Paging's auto-load might not trigger easily with FlowRow.")
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(16.dp)
                                    ) {
                                        Text("查看更多")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}