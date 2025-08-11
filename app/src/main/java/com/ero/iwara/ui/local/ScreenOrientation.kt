package com.ero.iwara.ui.local

import android.content.res.Configuration
import androidx.compose.runtime.compositionLocalOf

val LocalScreenOrientation = compositionLocalOf { Configuration.ORIENTATION_PORTRAIT }