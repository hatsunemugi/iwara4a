package com.ero.iwara.param

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

data class BaseParam(
    var page: Int,
    val size: Int,
    val table: String,
    val query: List<QueryParam>,
    val order: List<OrderParam>,
){
    fun pages(count: Int): Int
    {
        return (count + size - 1) / size
    }
    fun hasNext(count: Int): Boolean
    {
        return (page + 1) * size < count
    }
    fun sql(): SupportSQLiteQuery {
        val offset = (page - 1) * size
        val builder = StringBuilder("select * from $table ")
        val args = mutableListOf<Any>()
        if (query.isNotEmpty())
        {
            builder.append("where ${query.joinToString("and "){ it.format(args) } } ")
        }
        if (order.isNotEmpty()) {
            builder.append("order by ");
            val list = order.map {
                "${it.field} ${if (it.desc) "desc" else "asc"}"
            }
            builder.append(list.joinToString(", "))
        } else {
            // 如果没有提供排序参数，但有默认排序
            builder.append("order by id desc ")
        }
        builder.append(" limit ? offset ?")
        args.add(size)
        args.add(offset)
        val sqlQuery = builder.toString()
        return SimpleSQLiteQuery(sqlQuery, args.toTypedArray())
    }
}