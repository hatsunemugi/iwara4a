package com.ero.iwara.util

import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

class AutoLayoutModifier(private val aspectRatioValue: Float?) :
    LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val currentAspectRatio = aspectRatioValue ?: if (constraints.hasBoundedWidth && constraints.hasBoundedHeight && constraints.maxHeight > 0 && constraints.maxWidth > 0) {
            constraints.maxWidth.toFloat() / constraints.maxHeight.toFloat()
        } else {
            1f // Default aspect ratio
        }

        val calculatedWidth: Int
        val calculatedHeight: Int

        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            if (constraints.maxWidth / currentAspectRatio <= constraints.maxHeight) {
                calculatedWidth = constraints.maxWidth
                calculatedHeight = (constraints.maxWidth / currentAspectRatio).toInt().coerceAtMost(constraints.maxHeight)
            } else {
                calculatedHeight = constraints.maxHeight
                calculatedWidth = (constraints.maxHeight * currentAspectRatio).toInt().coerceAtMost(constraints.maxWidth)
            }
        } else if (constraints.hasBoundedWidth) {
            calculatedWidth = constraints.maxWidth
            calculatedHeight = (constraints.maxWidth / currentAspectRatio).toInt()
            // If height becomes 0 or negative, it might need adjustment based on requirements
            // For now, this is a direct calculation.
        } else if (constraints.hasBoundedHeight) {
            calculatedHeight = constraints.maxHeight
            calculatedWidth = (constraints.maxHeight * currentAspectRatio).toInt()
        } else {
            // Both unbounded: This is a tricky case.
            // Option 1: Measure with original constraints (might be too big or small).
            // Option 2: Try to use intrinsic measurements if the child has them.
            // Option 3: Fallback to a default size or aspect ratio behavior.
            // For now, let's measure with original constraints and let the child decide its size,
            // then this modifier doesn't really enforce an aspect ratio in this unbounded case.
            // Or, if we must enforce, we need a sensible default size.
            // A more robust solution might involve providing preferred sizes.
            // Let's assume for now, if completely unbounded, we can't reliably apply the "auto" logic.
            // We'll measure the child and use its dimensions.
            val placeable = measurable.measure(constraints)
            return layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }

        // Ensure calculated dimensions are not negative or zero if they are to be fixed
        val finalWidth = calculatedWidth.coerceAtLeast(0)
        val finalHeight = calculatedHeight.coerceAtLeast(0)

        val placeable = measurable.measure(Constraints.fixed(finalWidth, finalHeight))

        return layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }

    // Implementing equals and hashCode is important for modifier correctness,
    // especially when they have parameters, to ensure proper recomposition
    // and skipping.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AutoLayoutModifier) return false
        return aspectRatioValue == other.aspectRatioValue
    }

    override fun hashCode(): Int {
        return aspectRatioValue.hashCode()
    }
}