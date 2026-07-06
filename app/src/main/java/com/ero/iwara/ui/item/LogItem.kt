package com.ero.iwara.ui.item

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ero.iwara.event.clipboard
import com.ero.iwara.stroage.entity.LogBase
import com.ero.iwara.util.clickSound


@Preview
@Composable
fun LogItem(log: LogBase = LogBase(1,6666, 1, "测试用户","测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作测试操作","测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息测试信息","2025-08-29 11:50:30"))
{
    Card(modifier = Modifier
        .fillMaxWidth().wrapContentHeight()
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    clipboard("${log.action}-${log.message}")
                }
            )
        },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        ExpandableText(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 8.dp), text = "${log.uname}-${log.action}", expandable = log.message.isNotBlank(), body = {
            ExpandableText(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(8.dp), text = log.message)
        })
    }
}

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    expandable: Boolean = false,
    style: TextStyle = LocalTextStyle.current,
    collapsedMaxLines: Int = 1,
    expandIconComposable: @Composable () -> Unit = {
        Icon(Icons.Filled.ArrowDropDown, "展开", modifier = Modifier.size(18.dp))
    },
    collapseIconComposable: @Composable () -> Unit = {
        Icon(Icons.Filled.ArrowDropUp, "折叠", modifier = Modifier.size(18.dp))
    },
    body: @Composable ()->Unit = {}
) {
    var calculated by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var canExpand by remember { mutableStateOf(expandable) }
    var hasPerformedInitialMeasurement by remember { mutableStateOf(false) }
    var layoutResultFromCollapsedState by remember { mutableStateOf<TextLayoutResult?>(null) }

    // 新增状态：用于缓存计算好的截断文本
    var truncatedText by remember { mutableStateOf(text) } // 初始可以设为 content

    LaunchedEffect(text, style, collapsedMaxLines) {
        hasPerformedInitialMeasurement = false
        calculated = false
        expanded = false
        layoutResultFromCollapsedState = null
        truncatedText = text // 当核心内容变化时，也重置截断文本
    }

    // 当 canExpand 或 layoutResultFromCollapsedState (在 canExpand 为 true 时) 变化时，
    // 重新计算 truncatedText。
    // 使用 LaunchedEffect 是为了避免在每次重组时都执行这段逻辑，只在依赖项变化时执行。
    LaunchedEffect(canExpand, layoutResultFromCollapsedState, text, collapsedMaxLines) {
        if (canExpand && layoutResultFromCollapsedState != null && !expanded) {
            val layoutResult = layoutResultFromCollapsedState!!

            val relevantLineIndex = (collapsedMaxLines - 1).coerceAtLeast(0).coerceAtMost(layoutResult.lineCount - 1)
            val lineEndOffset = layoutResult.getLineEnd(relevantLineIndex, true)
            var newTruncatedText: String

            if (lineEndOffset == 0 && text.isNotEmpty()) {
                newTruncatedText = if (text.length > 1) text.first() + ".." else text.ifEmpty { "..." }
            } else if (lineEndOffset < text.length) {
                val estimatedCharsToReserveForEllipsisAndIcon = 2
                val fittingChars = (lineEndOffset - estimatedCharsToReserveForEllipsisAndIcon)

                newTruncatedText = if (fittingChars <= 0) {
                    if (lineEndOffset > 0) {
                        text.substring(0, lineEndOffset.coerceAtMost(text.length)) + if (lineEndOffset < text.length - 1 && lineEndOffset > 1) "…" else ""
                    } else {
                        "..."
                    }
                } else {
                    text.substring(0, fittingChars.coerceAtMost(text.length)) + "..."
                }
            } else {
                newTruncatedText = text // 没有发生截断
            }

            if (truncatedText != newTruncatedText) {
                truncatedText = newTruncatedText
                calculated = true
            }
        } else if (!canExpand && truncatedText != text) {
            // 如果不能展开了（例如内容变短了），确保 truncatedText 是原始 content
            truncatedText = text
        }
    }

    val textToDisplay = if (expanded || !canExpand) text else truncatedText
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().clickSound(context = LocalContext.current, enabled = canExpand, onClick = { expanded = !expanded; }), verticalAlignment = Alignment.Top) {
            Text(
                text = textToDisplay,
                style = style,
                maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
                overflow = TextOverflow.Clip,
                onTextLayout = { textLayoutResult ->
                    if (calculated) return@Text
                    if (!expanded) {
                        if (!hasPerformedInitialMeasurement) {
                            val wouldOverflowInCollapsedState =
                                textLayoutResult.lineCount > collapsedMaxLines ||
                                        (textLayoutResult.lineCount == collapsedMaxLines && textLayoutResult.didOverflowHeight)
                            if (!canExpand) {
                                canExpand = wouldOverflowInCollapsedState
                            }
                            hasPerformedInitialMeasurement = true
                            if (canExpand) { // 初始测量后，如果能展开，则设置布局结果
                                layoutResultFromCollapsedState = textLayoutResult
                            } else { // 如果不能展开，确保 layoutResult 为 null
                                if (layoutResultFromCollapsedState != null) layoutResultFromCollapsedState = null
                            }
                        } else { // 已经进行过初始测量
                            if (canExpand) {
                                val previousLayoutResult = layoutResultFromCollapsedState
                                if (previousLayoutResult == null ||
                                    previousLayoutResult.size != textLayoutResult.size ||
                                    previousLayoutResult.lineCount != textLayoutResult.lineCount ||
                                    previousLayoutResult.didOverflowHeight != textLayoutResult.didOverflowHeight ||
                                    previousLayoutResult.getLineEnd((collapsedMaxLines - 1).coerceAtLeast(0).coerceAtMost(previousLayoutResult.lineCount - 1), true) !=
                                    textLayoutResult.getLineEnd((collapsedMaxLines - 1).coerceAtLeast(0).coerceAtMost(textLayoutResult.lineCount - 1), true)
                                ) {
                                    layoutResultFromCollapsedState = textLayoutResult
                                }
                            } else { // canExpand 为 false
                                if (layoutResultFromCollapsedState != null) {
                                    layoutResultFromCollapsedState = null
                                }
                            }
                        }
                    }
                    // 展开状态下不改变任何与折叠相关的状态
                },
                modifier = Modifier.weight(1f)
            )

            if (canExpand) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    if (expanded) collapseIconComposable() else expandIconComposable()
                }
            }
        }
        if (expanded) {
            body()
        }
    }
}