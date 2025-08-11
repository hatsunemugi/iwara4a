package com.ero.iwara.result

import kotlinx.serialization.Serializable

@Serializable
data class MResult<T>(
    val count: Int = 0,
    val limit: Int = 0,
    val page: Int = 0,
    val pendingCount: Int = 0,
    val type: String = "",
    val results: List<T>
){
    fun <R> transform(transform: (T) -> R): MResult<R> {
        return MResult(
            count = this.count,
            limit = this.limit,
            page = this.page,
            pendingCount = this.pendingCount,
            type = this.type,
            results = this.results.map(transform) // 这里应用了转换
        )
    }
}