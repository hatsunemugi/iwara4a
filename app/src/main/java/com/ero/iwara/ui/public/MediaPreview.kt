package com.ero.iwara.ui.public

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ero.iwara.R
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.MediaType.*
import com.ero.iwara.util.set
import kotlinx.coroutines.launch

@Composable
fun MediaPreviewCard(navController: NavController, mediaPreview: MediaPreview) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (mediaPreview.type == MediaType.VIDEO) {
                    navController.navigate("video/${mediaPreview.mediaId}")
                } else if (mediaPreview.type == MediaType.IMAGE) {
                    navController.navigate("image/${mediaPreview.mediaId}")
                }
            }
        ) {
            Box(modifier = Modifier.height(150.dp), contentAlignment = Alignment.BottomCenter) {
                AsyncImage(
                    model = mediaPreview.previewPic, // 直接传递图片 URL 或数据模型
                    contentDescription = null, // 或者提供有意义的描述
                    onError = {
                        Toast.makeText(context,mediaPreview.previewPic, Toast.LENGTH_SHORT).show()
                        scope.launch {
                            clipboard.set(mediaPreview.previewPic)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth, // 其他 Image 参数可以直接在 AsyncImage 上设置
                    // placeholder = painterResource(R.drawable.placeholder), // 可选：占位图
                    // error = painterResource(R.drawable.error_image),       // 可选：错误图
                    // fallback = painterResource(R.drawable.fallback_image)  // 可选：当 model 为 null 时的后备图
                )
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle.Default.copy(color = Color.White),
                    LocalContentColor provides Color.White
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (plays, likes, type) = createRefs()

                        Row(modifier = Modifier.constrainAs(plays) {
                            start.linkTo(parent.start, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.play_icon), null)
                            Text(text = mediaPreview.watches)
                        }

                        Row(modifier = Modifier.constrainAs(likes) {
                            start.linkTo(plays.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.like_icon), null)
                            Text(text = mediaPreview.likes)
                        }

                        Row(modifier = Modifier.constrainAs(type) {
                            end.linkTo(parent.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (mediaPreview.type) {
                                    VIDEO -> "视频"
                                    IMAGE -> "图片"
                                    else -> "视频"
                                }
                            )
                        }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = mediaPreview.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = mediaPreview.author,maxLines = 1,color = MaterialTheme.colorScheme.onSurfaceVariant //表示次要/禁用文本的颜色
                )
            }
        }
    }
}