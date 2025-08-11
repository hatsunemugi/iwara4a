package com.ero.iwara.ui.public

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ero.iwara.model.comment.Comment
import com.ero.iwara.model.comment.CommentPosterType.NORMAL
import com.ero.iwara.model.comment.CommentPosterType.OWNER
import com.ero.iwara.model.comment.CommentPosterType.SELF
import com.ero.iwara.ui.theme.PINK
import com.ero.iwara.util.noRippleClickable
import com.ero.iwara.util.set
import kotlinx.coroutines.launch

@Composable
fun CommentItem(navController: NavController, comment: Comment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(0.5.dp, Color.Gray), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        val context = LocalContext.current
        val clipboard = LocalClipboard.current
        val scope = rememberCoroutineScope()
        Column(Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = comment.authorPic,
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                navController.navigate("user/${comment.authorId}")
                            },
                        onError = {
                            Toast.makeText(context, comment.authorPic, Toast.LENGTH_SHORT).show()
                            scope.launch {
                                clipboard.set(comment.authorPic)
                            }
                        },
                        contentDescription = null
                    )
                }
                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.padding(end = 8.dp).noRippleClickable {
                                navController.navigate("user/${comment.authorId}")
                            },
                            text = comment.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        when (comment.posterType) {
                            OWNER -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PINK)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "UP主", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            SELF -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Yellow)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "你", color = Color.Black, fontSize = 12.sp)
                                }
                            }

                            NORMAL -> {}
                        }
                    }
                    Text(comment.date, color = MaterialTheme.colorScheme.onSurfaceVariant,style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = comment.content)
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                comment.reply.forEach {
                    CommentItem(navController, it)
                }
            }
        }
    }
}