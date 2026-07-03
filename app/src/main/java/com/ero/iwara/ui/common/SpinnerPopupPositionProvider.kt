package com.ero.iwara.ui.common

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.math.min


class SpinnerPopupPositionProvider(
    private val expandUpwards: Boolean,
    private val dropdownMaxHeightPx: Int
    // No need to pass density if all calculations are based on Px values
    // private val density: Density
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect, // These are the updated anchor bounds at the time of calculation
        windowSize: IntSize,   // Size of the window the popup is in
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize // The actual measured size of the popup content
    ): IntOffset {
        // The popupContentSize is the fully measured size of the LazyColumn (or its content).
        // We still need to respect the dropdownMaxHeight.
        val actualPopupHeight = min(popupContentSize.height, dropdownMaxHeightPx)

        val xPosition = anchorBounds.left // Align start of popup with start of anchor

        val yPosition = if (expandUpwards) {
            // Place bottom of popup at the top of the anchor
            anchorBounds.top - actualPopupHeight
        } else {
            // Place top of popup at the bottom of the anchor
            anchorBounds.bottom
        }
        return IntOffset(xPosition, yPosition)
    }
}