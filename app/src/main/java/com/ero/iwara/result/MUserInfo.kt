package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MUserInfo(
    val user: MUser,
    val notifications: MNotifications,
    val balance: Int,
    val profile: MProfile,
    val tagBlacklist: List<String>
)