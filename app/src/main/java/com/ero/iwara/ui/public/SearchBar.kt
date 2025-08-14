package com.ero.iwara.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ero.iwara.util.send
import com.ero.iwara.util.textFieldColors

@Composable
fun SearchTag(value: String, modifier: Modifier, manager: FocusManager, onEdit: (String)->Unit) { // Renamed for clarity
    val color = if (isSystemInDarkTheme()) Color.White else Color.Black // Renamed for clarity
    var tag by remember { mutableStateOf(value) }
    ConstraintLayout(modifier = modifier)
    {
        val (text, box) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(box) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                    width = Dimension.fillToConstraints // 填满约束定义的空间 (即父 ConstraintLayout)
                }
                .height(32.dp)
                .border(BorderStroke(1.dp, color))
        )
        TextField(
            modifier = Modifier
                .constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start) // 左边距 16.dp
                    end.linkTo(parent.end)     // 右边距 16.dp
                    width = Dimension.fillToConstraints // 宽度填满约束（父级宽度减去边距）
                    height = Dimension.fillToConstraints // 高度包裹内容，使其在父级32.dp内垂直居中
                }.onFocusChanged{
                    if(it.isFocused) return@onFocusChanged
                    if (tag.isBlank()) {
                        send("不能搜索空内容哦！")
                    } else {
                        manager.clearFocus()
                        onEdit(tag)
                    }
                },
            value = tag,
            onValueChange =
            {
                tag = it.replace("\n", "")
            },
            maxLines = 1,
            placeholder = {
                Text(text = "搜索标签")
            },
            colors = textFieldColors(Color.Transparent),
            trailingIcon = {
                if (tag.isNotEmpty()) {
                    IconButton(onClick = { tag = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    manager.clearFocus()
                }
            )
        )
    }
}

@Composable
fun SearchBar(modifier: Modifier, manager: FocusManager, onSearch: (String)->Unit) { // Renamed for clarity
    val color = if (isSystemInDarkTheme()) Color.White else Color.Black // Renamed for clarity
    var value by remember { mutableStateOf("") }
    ConstraintLayout(modifier = modifier)
    {        // 创建 ID 以便约束
        val (text, box, icon) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(box) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints // 填满约束定义的空间 (即父 ConstraintLayout)
                }
                .height(40.dp)
                .border(BorderStroke(1.dp, color), shape = RoundedCornerShape(20.dp))
        )
        TextField(
            modifier = Modifier
                .constrainAs(text) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start,20.dp) // 左边距 16.dp
                end.linkTo(parent.end)     // 右边距 16.dp
                width = Dimension.fillToConstraints // 宽度填满约束（父级宽度减去边距）
                height = Dimension.fillToConstraints // 高度包裹内容，使其在父级32.dp内垂直居中
            }.onFocusChanged{
                if(it.isFocused) return@onFocusChanged
                if (value.isBlank()) {
                    send("不能搜索空内容哦！")
                } else {
                    onSearch(value)
                }
            },
            value = value,
            onValueChange =
            {
                value = it.replace("\n", "")
            },
            maxLines = 1,
            placeholder = {
                Text(text = "搜索视频和图片")
            },
            colors = textFieldColors(Color.Transparent),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { value = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    manager.clearFocus()
                }
            )
        )
        Icon(modifier = Modifier.size(20.dp).constrainAs(icon) {
            top.linkTo(parent.top,4.dp)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start,12.dp)
            width = Dimension.fillToConstraints
        }, imageVector = Icons.Default.Search, contentDescription = null, tint = color)
    }
}
@Preview
@Composable
fun RowScope.SearchTagPreview()
{
    val manager = LocalFocusManager.current
    SearchTag("", Modifier.weight(1f).height(56.dp),manager){
        
    }
}
@Preview
@Composable
fun SearchBarPreview()
{
    val manager = LocalFocusManager.current
    SearchBar(Modifier.fillMaxWidth().height(56.dp),manager){

    }
}