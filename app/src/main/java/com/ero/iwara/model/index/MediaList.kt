package com.ero.iwara.model.index

import com.ero.iwara.result.MTag

data class TagList(
    val currentPage: Int,
    val hasNext: Boolean,
    val tagList: List<MTag>
)

data class MediaList(
    val currentPage: Int,
    val hasNext: Boolean,
    val mediaList: List<MediaPreview>
)

data class MediaQueryParam(
    var sort: SortType,
    var type: MediaType,
    var tags: List<String>
)

enum class SortType(val value: String) {
    DATE("date"),
    VIEWS("views"),
    LIKES("likes"),
    TREND("trending"),
    POPULARITY("popularity")
}