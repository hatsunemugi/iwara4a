package com.ero.iwara.ui.screen.index

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ero.iwara.util.set
import kotlinx.coroutines.launch

@Composable
fun IndexDrawer(navController: NavController, indexViewModel: IndexViewModel) {
    fun isLoading() = indexViewModel.loadingSelf
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Profile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Profile Pic
                Box(modifier = Modifier.padding(horizontal = 32.dp)){
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                navController.navigate("login")
                            }
                    ) {
                        AsyncImage(model = indexViewModel.self.profilePic,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = null,
                            onError = {
                                scope.launch {
                                    clipboard.set(indexViewModel.self.profilePic)
                                }
                            }
                        )
                    }
                }

                // Profile Info
                Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)) {
                    // UserName
                    Text(
                        text = if(isLoading()) "加载中" else indexViewModel.self.nickname,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Email
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(modifier = Modifier.weight(1f), text = indexViewModel.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        IconButton(modifier = Modifier.size(25.dp), onClick = { indexViewModel.refreshSelf() }) {
                            Icon(modifier = Modifier.size(25.dp), imageVector = Icons.Default.Refresh, contentDescription = "刷新个人信息")
                        }
                    }
                }
            }
        }

        // Navigation List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Surface(Modifier.fillMaxSize()) {

            }
        }
    }
}