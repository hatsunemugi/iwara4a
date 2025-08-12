package com.ero.iwara.ui.screen.index

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ero.iwara.event.AppEvent
import com.ero.iwara.event.postFlowEvent
import com.ero.iwara.model.user.Self
import com.ero.iwara.util.HandleMessage
import com.ero.iwara.util.send

data class Profile(
    val avatar: String?,
    val nickname: String,
    val email: String,
    val loading: Boolean
)

data class NavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun IndexDrawerContent(
    profile: Profile,
    items: List<NavItem>,
    onProfileClick: () -> Unit,
    onRefreshProfile: () -> Unit,
    onNavItemClick: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // 如果 HandleMessage 存在且需要预览，你需要提供一个假的 StateFlow<Message?>
        // HandleMessage(messageFlow.collectAsState().value)

        // Profile
        Surface(
            modifier = Modifier
                .wrapContentSize(), // 使用 wrapContentSize 而不是 wrapContentHeight 如果你想它基于内容调整大小
//                .windowInsetsTopHeight(WindowInsets.statusBars), // 确保内容在状态栏下方
            tonalElevation = 4.dp,
            shadowElevation = 4.dp, // 调整阴影以获得更好的视觉效果
            color = MaterialTheme.colorScheme.surface // 使用 surface 或 surfaceContainerLow
        )
        {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 16.dp), // 为整个 Profile 部分添加底部 padding
                verticalArrangement = Arrangement.Top // 默认就是 Top
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // 顶部留白，替代 windowInsetsTopHeight 的部分效果，使其更灵活
                // Profile Pic
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 统一使用 16.dp 左右边距
                        .align(Alignment.CenterHorizontally) // 使头像居中（如果需要）或 Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant) // 占位符背景
                            .clickable(onClick = onProfileClick)
                    ) {
                        if (profile.loading && !profile.avatar.isNullOrEmpty()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        } else if (profile.avatar != null) {
                            AsyncImage(
                                model = profile.avatar,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                onError = { send(profile.avatar) }
                            )
                        } else {
                            // 简单的占位符图标或颜色
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondaryContainer))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Info
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp) // 统一使用 16.dp
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally // 文本居中
                ) {
                    Text(
                        text = if (profile.loading) "加载中..." else profile.nickname,
                        style = MaterialTheme.typography.titleMedium, // 考虑使用 titleMedium
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // 使邮件和刷新按钮组合居中
                    ) {
                        Text(
                            text = profile.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp)) // 在邮件和按钮之间添加一些间隔
                        IconButton(
                            modifier = Modifier.size(25.dp),
                            onClick = onRefreshProfile
                        ) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "刷新个人信息",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Navigation List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .weight(1f)
                .padding(top = 8.dp) // 在个人资料和导航列表之间添加一点间隔
        ) {
            if (items.isEmpty()) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("无导航项", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                items.forEach { item ->
                    NavigationDrawerItemPreview( // 使用一个简单的预览导航项
                        item = item,
                        onClick = { onNavItemClick(item) }
                    )
                }
            }
        }
    }
}

/**
 * 简单的导航项预览 Composable
 */
@Composable
fun NavigationDrawerItemPreview(
    item: NavItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface( // 或者直接用 Row，取决于你想要的效果
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = Color.Transparent // 使其透明，除非被选中
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant // 使用 onSurfaceVariant 或 primary
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


/**
 * 预览函数
 */
@Preview(showBackground = true, name = "IndexDrawer Logged In")
@Composable
fun IndexDrawerLoggedInPreview() {
    MaterialTheme { // 包裹在 MaterialTheme 中以应用颜色和排版
        IndexDrawerContent(
            profile = Profile(
                avatar = Self.GUEST.avatar,
                nickname = "预览用户",
                email = "preview@example.com",
                loading = false
            ),
            items = listOf(
                NavItem("视频", "video", Icons.Default.Videocam),
                NavItem("订阅", "subscribe", Icons.Default.Subscriptions),
                NavItem("图片", "image", Icons.Default.Image)
            ),
            onProfileClick = {},
            onRefreshProfile = {},
            onNavItemClick = {}
        )
    }
}



/**
 * 预览函数
 */
@Composable
fun IndexDrawer(
    navController: NavController,
    indexViewModel: IndexViewModel
) {
    val user by remember { derivedStateOf { indexViewModel.self } }
    val loading by remember { derivedStateOf { indexViewModel.loadingSelf } }
    HandleMessage(indexViewModel.message)
    MaterialTheme { // 包裹在 MaterialTheme 中以应用颜色和排版
        IndexDrawerContent(
            profile = Profile(
                avatar = user.avatar,
                nickname = user.nickname,
                email = user.email,
                loading = loading
            ),
            items = listOf(
                NavItem("视频", "video", Icons.Default.Videocam),
                NavItem("订阅", "subscribe", Icons.Default.Subscriptions),
                NavItem("图片", "image", Icons.Default.Image)
            ),
            onProfileClick = {
                navController.navigate("login")
            },
            onRefreshProfile = {
                indexViewModel.refreshSelf()
            },
            onNavItemClick = {}
        )
    }
}