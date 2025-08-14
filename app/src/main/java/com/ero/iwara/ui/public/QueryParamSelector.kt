package com.ero.iwara.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.model.index.SortType
import com.ero.iwara.ui.screen.index.IndexViewModel
import com.ero.iwara.ui.screen.index.page.TagListPage
import com.ero.iwara.util.ripple
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun QueryParamSelector(
    modifier: Modifier,
    viewModel: IndexViewModel,
    selector: ((String)->Unit)->Unit
){
    var tag by remember { mutableStateOf("") }
    var edit by remember { mutableStateOf(false) }
    var dialog by remember { mutableIntStateOf(1) }
    val border = if(isSystemInDarkTheme()) Color.White else Color.Black
    val focus = LocalFocusManager.current
    var editor:(String)->Unit = {}
    val type by remember { derivedStateOf { viewModel.page() == 1 } }
    selector{
        tag = it
        edit = true
    }
    val state = rememberMaterialDialogState()
    MaterialDialog(state) {
        when(dialog)
        {
            1 -> {
                if(type) {
                    TypeSelector("分类",viewModel.type,listOf(MediaType.VIDEO, MediaType.IMAGE,
                            MediaType.POST))
                    {
                        viewModel.type = MediaType.entries[it]
                        viewModel.search()
                        state.hide()
                    }
                }else {
                    TypeSelector("排序", viewModel.sort, SortType.entries) {
                        viewModel.sort = SortType.entries[it]
                        viewModel.search()
                        state.hide()
                    }
                }
            }
            2 -> {
                CharSelector {
                    tag = it
                    edit = true
                    editor(it)
                    dialog = 4
                }
            }
            4 -> {
                TagListPage(editor = { editor = it }) {
                    tag = it
                    viewModel.tags.add(it)
                    viewModel.search()
                    state.hide()
                    edit = false
                    tag = ""
                }
            }
        }
    }
    Row(modifier = modifier
        .height(56.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp))
    {
        Box(
            modifier = Modifier
                .clickable {
                    dialog = 1
                    state.show()
                }
                .border(BorderStroke(1.dp, border), RoundedCornerShape(2.dp))
                .height(32.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(modifier = Modifier.wrapContentSize(), text = if(type) viewModel.type.name else viewModel.sort.name)
        }
        if(edit){
            SearchTag(tag, modifier = Modifier.weight(1f).height(56.dp), focus)
            {
                tag = it
                dialog = 4
                state.show()
                editor(tag)
            }
        }
        if(!type)
        {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .border(BorderStroke(1.dp, Color.Black))
                    .clickable {
                        if (!edit) {
                            dialog = 2
                            state.show()
                        } else {
                            focus.clearFocus()
                        }
                    },
                imageVector = if (edit) Icons.Default.Done else Icons.Default.Add,
                contentDescription = null
            )
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
