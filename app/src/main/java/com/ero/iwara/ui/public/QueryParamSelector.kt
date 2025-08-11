package com.ero.iwara.ui.public

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ero.iwara.model.index.MediaQueryParam
import com.ero.iwara.model.index.SortType
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogButtons
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun QueryParamSelector(
    queryParam: MediaQueryParam,
    onChangeSort: (sort: SortType) -> Unit,
    onChangeFilters: (filters: List<String>) -> Unit
) {
    var sort by remember { mutableStateOf(SortType.TREND) }
    val tags by remember { mutableStateOf(listOf<String>()) }
    val state = rememberMaterialDialogState()
    val sortDialog =  MaterialDialog(state) {
        title("选择排序条件")
        listItemsSingleChoice(
            list = SortType.entries.map { it.name },
            onChoiceChange = {
                sort = SortType.entries[it]
                onChangeSort(sort)
                state.hide()
            },
            initialSelection = sort.ordinal,
            waitForPositiveButton = false,
        )
        MaterialDialogButtons(this).button("确定") { }
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "排序:  ")
            Box(
                modifier = Modifier
                    .clickable { state.show() }
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(2.dp))
                    .padding(4.dp)
            ) {
                Text(text = queryParam.sort.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    QueryParamSelector(
        queryParam = MediaQueryParam(SortType.DATE, listOf("created:2021")),
        onChangeSort = { /*TODO*/ }) {

    }
}