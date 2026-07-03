package com.ero.iwara.ui.common


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlin.math.roundToInt

private object CustomSpinnerDefaults {
    val DropdownMaxHeight = 200.dp
    val AnchorMatchWidth = true
    val DropdownPaddingForWidestItem = 32.dp
    val DropdownBorderWidth = 1.dp
    val AnchorVerticalPadding = 8.dp
    val AnchorHorizontalPadding = 12.dp
    val IconMarginStart = 4.dp
}

@Composable
fun DropdownMenuItemContent(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
    ) {
        Text(text = text, style = textStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun <T> ActualDropdownMenuItem(
    item: T,
    itemToString: (T) -> String,
    onClick: () -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    itemContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        DropdownMenuItemContent(
            text = itemToString(item),
            textStyle = textStyle,
            contentPadding = itemContentPadding
        )
    }
}

@Preview
@Composable
fun MyScreenWithCustomSpinner() {
    val items = remember { listOf("Option 1", "Option 2", "Option 3", "Very Long Option 4 that might need to wrap or be truncated", "Option 5", "Option 6", "Option 7", "Option 8") }
    var selectedItem by remember { mutableStateOf(items[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selected: $selectedItem", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))

        // Example 1: Basic CustomSpinner
        CustomSpinner(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Give the anchor some width
                .border(1.dp, Color.Gray),
            items = items,
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it },
            itemToString = { it }, // Simple string items
            dropdownMaxHeight = 150.dp, // Limit dropdown height
            // dropdownWidth = 250.dp // Optionally set a fixed width for the dropdown
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Example 2: Custom data type and custom display strings
        data class User(val id: Int, val name: String, val email: String)
        val users = remember {
            listOf(
                User(1, "Alice Wonderland", "alice@example.com"),
                User(2, "Bob The Builder", "bob@example.com"),
                User(3, "Charlie Brown", "charlie@example.com")
            )
        }
        var selectedUser by remember { mutableStateOf(users[0]) }

        CustomSpinner(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(1.dp, MaterialTheme.colorScheme.primary),
            items = users,
            selectedItem = selectedUser,
            onItemSelected = { selectedUser = it },
            itemToString = { user -> "${user.name} (${user.email})" }, // Display for items in list
            selectedItemToString = { user -> user.name }, // Display for the selected item in anchor
            dropdownMaxHeight = 180.dp,
            dropdownWidth = 300.dp, // Fixed width for this dropdown
            dropdownBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            dropdownBorderColor = MaterialTheme.colorScheme.secondary
        )

        // Add more content below to test scrolling and upward expansion
        Spacer(Modifier.weight(1f)) // Pushes next spinner towards bottom if enough content
        Text("Another spinner near bottom:")

        var selectedItem2 by remember { mutableStateOf(items[1]) }
        CustomSpinner(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Give the anchor some width
                .border(1.dp, Color.Gray),
            items = items,
            selectedItem = selectedItem2,
            onItemSelected = { selectedItem2 = it },
            itemToString = { it },
            dropdownMaxHeight = 250.dp,
        )
        Spacer(Modifier.height(20.dp))


    }
}


@Composable
fun <T> CustomSpinner(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemToString: (T) -> String = { it.toString() },
    selectedItemToString: (T) -> String = itemToString,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    dropdownMaxHeight: Dp = CustomSpinnerDefaults.DropdownMaxHeight,
    dropdownWidth: Dp? = null,
    dropdownAnchorMatchWidth: Boolean = CustomSpinnerDefaults.AnchorMatchWidth,
    // This padding is now primarily for the *content width calculation*,
    // as individual items will have their own padding.
    dropdownHorizontalPaddingForWidestItemText: Dp = CustomSpinnerDefaults.DropdownPaddingForWidestItem,
    dropdownBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    dropdownBorderColor: Color = MaterialTheme.colorScheme.outline,
    dropdownBorderWidth: Dp = CustomSpinnerDefaults.DropdownBorderWidth,
    anchorVerticalPadding: Dp = CustomSpinnerDefaults.AnchorVerticalPadding,
    anchorHorizontalPadding: Dp = CustomSpinnerDefaults.AnchorHorizontalPadding,
    iconMarginStart: Dp = CustomSpinnerDefaults.IconMarginStart,
    dropdownItemContentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp
    ),
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorBoundsInWindow by remember { mutableStateOf(IntRect.Zero) } // Store anchor bounds as IntRect
    var expandUpwards by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val textMeasurer = rememberTextMeasurer()

    val widestItemTextOnlyWidthDp by remember(items, itemToString, textStyle, density) {
        derivedStateOf {
            if (items.isEmpty()) 0.dp else {
                val widestTextPx = items.maxOfOrNull { item ->
                    textMeasurer.measure(
                        text = AnnotatedString(itemToString(item)),
                        style = textStyle,
                        overflow = TextOverflow.Visible, softWrap = false, maxLines = 1
                    ).size.width
                } ?: 0
                with(density) { widestTextPx.toDp() }
            }
        }
    }
    val widestFullItemWidthDp = widestItemTextOnlyWidthDp + dropdownHorizontalPaddingForWidestItemText
    Box(modifier) {
        ConstraintLayout(
            modifier = modifier
                .onGloballyPositioned { coordinates ->
                    // Get bounds in window coordinates
                    val positionInWindow = coordinates.positionInWindow()
                    val size = coordinates.size
                    anchorBoundsInWindow = IntRect(
                        left = positionInWindow.x.roundToInt(),
                        top = positionInWindow.y.roundToInt(),
                        right = (positionInWindow.x + size.width).roundToInt(),
                        bottom = (positionInWindow.y + size.height).roundToInt()
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ){
                    val windowContainerHeightPx = windowInfo.containerSize.height
                    // Use anchorBoundsInWindow for y and height
                    val spaceBelow = windowContainerHeightPx - anchorBoundsInWindow.bottom
                    val spaceAbove = anchorBoundsInWindow.top
                    val requiredPopupHeightPx =
                        with(density) { dropdownMaxHeight.toPx().roundToInt() }

                    expandUpwards =
                        if (spaceBelow < requiredPopupHeightPx && requiredPopupHeightPx <= spaceAbove) {
                            true
                        } else if (spaceAbove < requiredPopupHeightPx && requiredPopupHeightPx <= spaceBelow) {
                            false
                        } else {
                            spaceAbove >= spaceBelow
                        }
                    expanded = !expanded
                }
                .padding(vertical = anchorVerticalPadding, horizontal = anchorHorizontalPadding)
        ) {
            val (textRef, iconRef) = createRefs()
            Text(
                text = selectedItemToString(selectedItem), style = textStyle, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(textRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                }
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.constrainAs(iconRef) {
                    start.linkTo(textRef.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
        }

        if (expanded) {
            val anchorWidthDp = with(density) { anchorBoundsInWindow.width.toDp() }
            val popupWidthDp = dropdownWidth ?: if (dropdownAnchorMatchWidth) {
                anchorWidthDp
            } else {
                max(anchorWidthDp, widestFullItemWidthDp)
            }

            val lazyListState = rememberLazyListState()
            val dropdownMaxHeightPx = with(density) { dropdownMaxHeight.toPx().roundToInt() }

            // Create the PopupPositionProvider instance
            // It will use the latest anchorBoundsInWindow when calculatePosition is called
            val positionProvider =
                remember(expandUpwards, dropdownMaxHeightPx) {
                    SpinnerPopupPositionProvider(
                        expandUpwards = expandUpwards,
                        dropdownMaxHeightPx = dropdownMaxHeightPx
                    )
                }

            Popup(
                popupPositionProvider = positionProvider, // Use the custom provider
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false, excludeFromSystemGesture = true)
            ) {
                Box(
                    modifier = Modifier
                        .width(popupWidthDp)
                        .heightIn(max = dropdownMaxHeight) // Content height is still constrained here
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(dropdownBackgroundColor)
                        .border(
                            dropdownBorderWidth,
                            dropdownBorderColor,
                            MaterialTheme.shapes.extraSmall
                        )
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(items) { item ->
                            ActualDropdownMenuItem(
                                item = item, itemToString = itemToString, textStyle = textStyle,
                                itemContentPadding = dropdownItemContentPadding,
                                onClick = { onItemSelected(item); expanded = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

// DropdownMenuItem remains the same
@Composable
private fun <T> DropdownMenuItem(
    item: T,
    itemToString: (T) -> String,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = itemToString(item), style = textStyle)
    }
}