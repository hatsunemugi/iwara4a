package com.ero.iwara.ui.public

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ero.iwara.util.noRippleClickable
import kotlinx.coroutines.launch

@Composable
fun TabRow(content: @Composable ()->Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun TabItem(pagerState: PagerState, index: Int, text: String) {
    val coroutineScope = rememberCoroutineScope()
    val selected = pagerState.currentPage == index
    Box(
        modifier = Modifier
            .noRippleClickable { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary // 或者 onSurface，取决于你的设计
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant // 或者 onSurface.copy(alpha = 0.74f) 等
                }
            )

            AnimatedVisibility(selected) {
                Spacer(
                    modifier = Modifier
                        .width(32.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}