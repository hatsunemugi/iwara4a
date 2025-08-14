package com.ero.iwara.ui.public

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ero.iwara.ui.screen.index.IndexViewModel
import com.ero.iwara.util.send
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexBar(viewModel: IndexViewModel, navController: NavController) {
    val user by remember { derivedStateOf { viewModel.self } }
    var selector: (String)-> Unit = {}
    val border = if(isSystemInDarkTheme()) Color.White else Color.Black
    Column(modifier = Modifier.statusBarsPadding().padding(start = 8.dp).fillMaxWidth().wrapContentHeight(), horizontalAlignment = Alignment.Start)
    {
        Row(modifier = Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            AsyncImage(
                model = user.avatar,
                modifier = Modifier.size(30.dp).clip(CircleShape),
                contentDescription = null,
                onError = {
                    send(user.avatar)
                }
            )
            QueryParamSelector(modifier = Modifier.weight(1f), viewModel){ selector = it }
            IconButton(modifier = Modifier, onClick = { navController.navigate("search") }) {
                Icon(Icons.Default.Search, null )
            }
        }
        if(viewModel.tags.isEmpty()) return
        FlowRow(
            modifier = Modifier.fillMaxWidth(), // 让 FlowRow 占据可用宽度
            horizontalArrangement = Arrangement.spacedBy(4.dp), // 标签之间的水平间距
            verticalArrangement = Arrangement.spacedBy(8.dp),   // 标签行之间的垂直间距
            // maxItemsInEachRow = 5, // 如果你想限制每行的最大项目数 (可选)
        ) {
            viewModel.tags.forEach { item ->
                TagItem(
                    tag = item,
                    border = border,
                    onClick = { selector(it) },
                    onDelete = {
                        viewModel.tags.remove(it)
                        viewModel.search()
                    }
                )
            }
        }


    }
}