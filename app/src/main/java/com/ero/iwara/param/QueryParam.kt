package com.ero.iwara.param

import com.ero.iwara.param.Operator.IN
import com.ero.iwara.param.Operator.NOT_IN
import com.ero.iwara.param.Operator.BETWEEN
import com.ero.iwara.param.Operator.LIKE
import com.ero.iwara.param.Operator.NOT_LIKE
import com.ero.iwara.param.Operator.IS_NOT_NULL
import com.ero.iwara.param.Operator.IS_NULL
import java.util.Collections

enum class Operator(val value: String) {
    EQUALS("="),
    NOT_EQUALS("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    LIKE("like"),
    NOT_LIKE("not like"),
    IN("in"),
    NOT_IN("not in"),
    BETWEEN("between"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null");

    // 辅助方法判断是否需要值
    fun requiresValue(): Boolean {
        return this != IS_NULL && this != IS_NOT_NULL
    }

    // 辅助方法判断是否是列表类型的值 (IN, NOT_IN)
    fun isListValueOperator(): Boolean {
        return this == IN || this == NOT_IN
    }

    // 辅助方法判断是否是范围类型的值 (BETWEEN)
    fun isRangeValueOperator(): Boolean {
        return this == BETWEEN
    }
}

interface IQuery{
    val field: String
    val operator: Operator
    fun format(list: MutableList<Any>): String
}

data class QueryParam(
    override val field: String,
    val alias: String,
    override val operator: Operator,
    val value: Any
): IQuery{
    override fun format(list: MutableList<Any>): String {
        return when(operator) {
            IS_NULL, IS_NOT_NULL -> { // 例如 IS NULL, IS_NOT_NULL
                "$field ${operator.value}"
            }
            LIKE, NOT_LIKE -> {
                list.add("%$value%")
                // 对于 LIKE，通常参数值需要包含 '%'
                // 调用者负责在 value 中添加 %，例如: QueryParam("name", QueryOperator.LIKE, "%john%")
                "$field ${operator.value} ? "
            }
            else -> {
                list.add(value)
                "$field ${operator.value} ? "
            }
        }
    }
}

data class QueryRange(
    override val field: String,
    val alias: String,
    override val operator: Operator,
    val value: List<Any>
): IQuery{
    override fun format(list: MutableList<Any>): String {
        return when(operator) {
            BETWEEN -> {
                list.addAll(listOf(value.take(2)))
                "$field ${operator.value} ? and ? "
            }
            IN, NOT_IN -> {
                list.addAll(value)
                "$field ${operator.value} (${Collections.nCopies(value.size, "?").joinToString(", ")}) "
            }
            else -> {
                ""
            }
        }
    }
}