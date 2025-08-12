package com.ero.iwara.model.detail.image

data class ImageDetail(
    val id: String,
    val title: String,
    val imageLinks: List<String>,

    val authorId: String,
    val authorName: String,
    val authorProfilePic: String,

    val watches: String
) {
    companion object {
        val LOADING = ImageDetail(
            "",
            "",
            emptyList(),
            "",
            "",
            "",
            ""
        )
    }
}