package com.ero.iwara.ui.local

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.compositionLocalOf

val LocalPagerState = compositionLocalOf<PagerState> {
    error("No PagerState provided")
}