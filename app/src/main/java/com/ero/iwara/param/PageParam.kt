package com.ero.iwara.param


data class PageParam(
    val exclude: String? = null,
    val parent: String? = null,
    val rating: String? = null,
    val tags: String? = null,
    val user: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val type: String? = null,
    val sort: String? = null,
    val query: String? = null,
    val subscribed: Boolean? = null
){
    fun map(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        exclude?.let { map["exclude"] = it }
        parent?.let { map["parent"] = it }
        rating?.let { map["rating"] = it }
        tags?.ifEmpty { null }?.let { map["tags"] = it }
        sort?.let { map["sort"] = it }
        type?.let { map["type"] = it}
        page?.let { map["page"] = it.toString() }
        limit?.let { map["limit"] = it.toString() }
        user?.let { map["user"] = it }
        page?.let { map["page"] = it.toString() }
        limit?.let { map["limit"] = it.toString() }
        query?.let { map["query"] = it }
        subscribed?.let { map["subscribed"] = it.toString() }
        return map
    }
}