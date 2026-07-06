package com.ero.iwara.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min


@Preview
@Composable
fun Preview()
{
    var current by remember { mutableIntStateOf(1) }
    Pagination(Modifier, current, 10, 3){
        current = it
    }
}

@Composable
fun Pagination(modifier: Modifier, current: Int, total: Int, max: Int = 5, onClick: (Int)->Unit = {})
{

    if (total <= 1) { // 如果只有一页或没有页，则不显示分页控件
        return
    }
    val lazyListState = rememberLazyListState()
    val pageNumbers = remember(current, total, max) {
        generatePageNumbers(current, total, max)
    }
    LaunchedEffect(current, pageNumbers) {
        val currentIndexInList = pageNumbers.indexOf(current)
        if (currentIndexInList != -1) {
            // This is a simple scroll, might need to calculate offset for centering
            lazyListState.animateScrollToItem(currentIndexInList)
        }
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 上一页按钮
        IconButton(
            onClick = { if (current > 1) onClick(current - 1) },
            enabled = current > 1
        ) {
            Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Previous Page")
        }

        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .weight(1f) // 让 LazyRow 占据中间的可用空间
                .wrapContentHeight(), // 高度包裹内容
            horizontalArrangement = Arrangement.Center, // 尝试在LazyRow内部居中内容
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(
                items = pageNumbers
            ) { pageNum ->
                if (pageNum == -1) { // -1 代表省略号
                    Text(
                        text = "...",
                        modifier = Modifier
                            .align(Alignment.CenterVertically), // 确保省略号垂直对齐
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    TextButton(
                        onClick = { onClick(pageNum) },
                        colors = if (pageNum == current) ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) else ButtonDefaults.textButtonColors(contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        modifier = Modifier
                            .sizeIn(minWidth = 36.dp, minHeight = 36.dp)
                    ) {
                        Text(
                            text = pageNum.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        // 下一页按钮
        IconButton(
            onClick = { if (current < total) onClick(current + 1) },
            enabled = current < total
        ) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Next Page")
        }
    }
}
fun generatePageNumbers(currentPage: Int, totalPages: Int, windowSize: Int): List<Int> {
    if (totalPages <= 1) return emptyList()
    if (totalPages <= windowSize + 2) return (1..totalPages).toList()
    val result = mutableListOf<Int>()
    result.add(1)
    when {
        currentPage < windowSize + 1 -> {
            // 显示开头的连续页码
            val end = max(currentPage + 1 , windowSize)
            for (i in 2..end) {
                result.add(i)
            }
            if (windowSize + 2 < totalPages) result.add(-1)
            result.add(totalPages)
        }

        currentPage > totalPages - windowSize -> {
            if (windowSize + 2 < totalPages) result.add(-1)
            val begin = min(currentPage - 1, totalPages - windowSize + 1)
            for (i in begin until totalPages) {
                result.add(i)
            }
            result.add(totalPages)
        }

        else -> {
            result.add(-1)
            val start = currentPage - windowSize / 2
            val end = currentPage + windowSize / 2
            for (i in start..end) {
                result.add(i)
            }
            result.add(-1)
            result.add(totalPages)
        }
    }
    return result
}