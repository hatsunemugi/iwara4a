package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MLink(
    val view: String,
    val download: String
)