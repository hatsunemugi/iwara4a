package com.ero.iwara.ui.screen.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ero.iwara.ui.common.CustomSpinner
import com.ero.iwara.ui.common.Pagination
import com.ero.iwara.ui.item.LogItem
import com.ero.iwara.ui.screen.base.Template


@Composable
fun LogScreen(viewModel: LogViewModel = hiltViewModel()) {
    Scaffold(
        topBar = { TopBar(viewModel) },
        bottomBar = {
            BottomBar(viewModel)
        }
    )
    {
        Box(modifier = Modifier.fillMaxSize().padding(it)){
            Template( { log -> LogItem(log) }, viewModel ){ scroll ->
                viewModel.scroll = scroll
            }
        }
    }
}
@Composable
fun TopBar(viewModel: LogViewModel)
{

}
@Composable
fun BottomBar(viewModel: LogViewModel)
{
    val page by viewModel.page.collectAsState()
    val pages by viewModel.pages.collectAsState()
    var size by remember { mutableIntStateOf(20) }
    val items = listOf("20" to 20, "100" to 100, "200" to 200, "500" to 500, "1000" to 1000)
    Row(modifier = Modifier.fillMaxWidth().height(64.dp).navigationBarsPadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically)
    {
        IconButton(
            onClick = { viewModel.remove() }
        ) {
            Icon(imageVector = Icons.Filled.Remove, contentDescription = "Remove Page")
        }
        Pagination(Modifier.fillMaxHeight().weight(1f), page, pages, 3){ viewModel.setPage(it) }
        CustomSpinner(
            modifier = Modifier,
            items = items,
            selectedItem = size.toString() to size,
            onItemSelected = {
                size = it.second
                viewModel.setSize(it.second)
            },
            itemToString = { it.first },
            dropdownMaxHeight = 128.dp
        )
        Spacer(Modifier.width(16.dp))
    }
}