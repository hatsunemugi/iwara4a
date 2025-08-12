package com.ero.iwara.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.ero.iwara.result.MTag
import com.ero.iwara.ui.screen.index.page.TagListPage
import com.ero.iwara.util.ripple
import com.ero.iwara.util.textFieldColors
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun <T> QueryParamSelector(
    field: String,
    current: T,
    list: List<T>,
    items: LazyPagingItems<MTag>,
    onEdit: (String) -> Unit,
    onChangeType: (type: T) -> Unit,
    onChangeFilters: (filters: List<String>) -> Unit
)where T: Enum<T> {
    var tag by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf<MTag>() }
    var edit by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(current) }
    val focus = LocalFocusManager.current
    var dialog by remember { mutableIntStateOf(1) }
    val state = rememberMaterialDialogState()
    MaterialDialog(state) {
        when(dialog)
        {
            1 -> {
                TypeSelector(field, type, list) {
                    type = list[it]
                    onChangeType(type)
                    state.hide()
                }
            }
            2 -> {
                CharSelector {
                    tag = it
                    onEdit(it)
                    state.hide()
                }
            }
            4 -> {
                TagListPage(items) {
                    tags.add(it)
                    onChangeFilters(tags.map { tag -> tag.id })
                    state.hide()
                    edit = false
                    tag = ""
                }
            }
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp))
    {
        Text(text = "$field:")
        Box(
            modifier = Modifier
                .clickable {
                    dialog = 1;
                    state.show()
                }
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(2.dp))
                .padding(4.dp)
        ) {
            Text(text = type.name)
        }
        if(edit){
            OutlinedTextField(
                value = tag,
                onValueChange = {
                    tag = it
                },
                colors = textFieldColors(Color.Transparent),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        Icon(
            modifier = Modifier
                .size(24.dp)
                .border(BorderStroke(1.dp, Color.Black))
                .clickable {
                    if (!edit) {
                        dialog = 2
                        state.show()
                        edit = true
                    } else {
                        dialog = 4
                        focus.clearFocus()
                        onEdit(tag)
                        state.show()
                    }
                },
            imageVector = if (edit) Icons.Default.Done else Icons.Default.Add,
            contentDescription = null
        )
    }
    if(tags.isEmpty()) return
    FlowRow(
        modifier = Modifier
            .padding(16.dp) // 给 FlowRow 一些内边距
            .fillMaxWidth(), // 让 FlowRow 占据可用宽度
        horizontalArrangement = Arrangement.spacedBy(8.dp), // 标签之间的水平间距
        verticalArrangement = Arrangement.spacedBy(8.dp),   // 标签行之间的垂直间距
        // maxItemsInEachRow = 5, // 如果你想限制每行的最大项目数 (可选)
    ) {
        tags.forEach { item ->
            TagItem(
                tag = item.id,
                onClick = { result ->
                    tag = result
                    edit = true
                },
                onDelete = { tag ->
                    tags.removeIf { it.id == tag }
                    onChangeFilters(tags.map { it.id })
                })
        }
    }
}

@Composable
fun <T> QueryParamSelector(
    field: String,
    current: T,
    list: List<T>,
    onChangeType: (type: T) -> Unit
)where T: Enum<T> {
    var type by remember { mutableStateOf(current) }
    val state = rememberMaterialDialogState()
    MaterialDialog(state) {
        TypeSelector(field, type, list) {
            type = list[it]
            onChangeType(type)
            state.hide()
        }
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp))
    {
        Text(text = "$field:")
        Box(
            modifier = Modifier
                .clickable {
                    state.show()
                }
                .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(2.dp))
                .padding(4.dp)
        ) {
            Text(text = type.name)
        }
    }
}

@Composable
fun <T> MaterialDialogScope.TypeSelector(
    field: String,
    type: T,
    list: List<T>,
    onSelect: (Int)->Unit)
where T: Enum<T>
{
    title("选择${field}条件")
    listItemsSingleChoice(
        list = list.map { it.name },
        onChoiceChange = onSelect,
        initialSelection = type.ordinal,
        waitForPositiveButton = false,
    )
}

@Preview
@Composable
fun CharacterSelectorPreview() {
    MaterialTheme { // 确保预览被 MaterialTheme 包裹
        CharSelector{}
    }
}
@Composable
fun CharSelector(onSelected: (String) -> Unit) {
    val characters = ('A'..'Z').map { it.toString() } + ('0'..'9').map { it.toString() }
    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f))
    {
        FlowRow(
            modifier = Modifier.align(Alignment.Center), // 在 FlowRow 外围添加 padding
            maxItemsInEachRow = 6, // 项目之间的主轴（水平）间距
            maxLines = 6
        ) {
            characters.forEach { char ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.onSurface)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .ripple { onSelected(char) },
                ) {
                    Text(char, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 18.sp, modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center), textAlign = TextAlign.Center) // 稍微调整字体大小以便更好地适应按钮
                }
            }
        }
    }

}
