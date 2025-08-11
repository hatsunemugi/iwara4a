package com.ero.iwara.util

import androidx.compose.foundation.pager.PagerState
import kotlin.math.roundToInt


val PagerState.currentVisualPage: Int
    get() {
        if(currentPageOffsetFraction != 0f){
            return (currentPage + currentPageOffsetFraction.roundToInt()).coerceIn(0 until pageCount)
        }
        return this.currentPage
    }