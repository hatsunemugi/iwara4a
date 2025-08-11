package com.ero.iwara.model.index

data class SubscriptionList(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val subscriptionList: List<MediaPreview>
)

