package com.ero.iwara.api.result

import kotlinx.serialization.Serializable

@Serializable
data class MError(val message: String)