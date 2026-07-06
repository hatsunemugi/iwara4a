package com.ero.iwara.ui.public

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ero.iwara.R
import com.ero.iwara.model.index.MediaPreview
import com.ero.iwara.model.index.MediaType.IMAGE
import com.ero.iwara.model.index.MediaType.VIDEO
import com.ero.iwara.util.send

@Composable
fun MediaPreviewCard(navController: NavController, mediaPreview: MediaPreview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (mediaPreview.type == VIDEO) {
                    navController.navigate("video/${mediaPreview.mediaId}")
                } else if (mediaPreview.type == IMAGE) {
                    navController.navigate("image/${mediaPreview.mediaId}")
                }
            }
        ) {
            Box(modifier = Modifier.wrapContentHeight(), contentAlignment = Alignment.BottomCenter) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(mediaPreview.previewPic)
                        .crossfade(true) // 💡 开启默认时长（100ms）的淡入淡出
                        .build(), // 直接传递图片 URL 或数据模型
                    contentDescription = null, // 或者提供有意义的描述
                    onError = {
                        send(mediaPreview.previewPic, true)
                    },
                    modifier = Modifier
                        .aspectRatio(1.77f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop, // 其他 Image 参数可以直接在 AsyncImage 上设置
                )
                CompositionLocalProvider(
                    LocalTextStyle provides TextStyle.Default.copy(color = Color.White),
                    LocalContentColor provides Color.White
                ) {
                    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                        val (plays, likes) = createRefs()

                        Row(modifier = Modifier.constrainAs(plays) {
                            start.linkTo(parent.start, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.play_icon), contentDescription = null, modifier = Modifier.size(12.dp))
                            Text(text = mediaPreview.watches, fontSize = 11.sp)
                        }

                        Row(modifier = Modifier.constrainAs(likes) {
                            start.linkTo(plays.end, 8.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.like_icon), contentDescription = null, modifier = Modifier.size(12.dp))
                            Text(text = mediaPreview.likes, fontSize = 11.sp)
                        }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = mediaPreview.title, fontSize = 14.sp , lineHeight = 17.sp, fontWeight = FontWeight.Normal, maxLines = 2)
                Text(text = mediaPreview.author, fontSize = 13.sp, fontWeight = FontWeight.Light, maxLines = 1,color = MaterialTheme.colorScheme.onSurfaceVariant //表示次要/禁用文本的颜色
                )
            }
        }
    }
}