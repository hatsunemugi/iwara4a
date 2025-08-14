package com.ero.iwara.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ero.iwara.result.MTag

@Composable
fun TagItem(tag: MTag, onClick: (MTag) -> Unit)
{ // 改名以避免与 LazyColumn items 块中的变量冲突
    FilterChip(
        selected = false,
        onClick = { onClick(tag) },
        label = { Text(tag.id, fontSize = 16.sp, modifier = Modifier.wrapContentSize()) },
        shape = CircleShape // 或者你喜欢的其他形状
    )
}

@Preview
@Composable
fun TagItem(tag: String = "yae_miko", border: Color = Color.Black, onClick: (String) -> Unit = {}, onDelete: (String) -> Unit = {}) {
    Row(modifier = Modifier
        .height(32.dp)
        .border(BorderStroke(1.dp, border), RoundedCornerShape(16.dp)), verticalAlignment = Alignment.CenterVertically)
    {
        Text(tag, modifier = Modifier
            .wrapContentSize()
            .padding(start = 12.dp)
            .clickable(onClick = { onClick(tag) }), fontSize = 18.sp)
        IconButton(modifier = Modifier.size(24.dp).padding(horizontal = 4.dp), onClick = { onDelete(tag)}) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}