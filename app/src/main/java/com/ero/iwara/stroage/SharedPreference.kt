package com.ero.iwara.stroage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


class IntDelegate(
    private val context: Context,
    private val name: String,
    private val key: String,
    private val defaultValue: Int = 0, // 默认值设为空字符串
) : ReadWriteProperty<Any?, Int> { // T (接收者类型) 可以是 Any? 如果委托用在顶层属性或非特定类中

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }
}

class LongDelegate(
    private val context: Context,
    private val name: String,
    private val key: String,
    private val defaultValue: Long = 0, // 默认值设为空字符串
) : ReadWriteProperty<Any?, Long> { // T (接收者类型) 可以是 Any? 如果委托用在顶层属性或非特定类中

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }
}

class StringDelegate(
    private val context: Context,
    private val name: String,
    private val key: String,
    private val defaultValue: String = "", // 默认值设为空字符串
) : ReadWriteProperty<Any?, String> { // T (接收者类型) 可以是 Any? 如果委托用在顶层属性或非特定类中

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }
}

class IntValueEnumDelegate<E : Enum<E>>( // E 仍然需要是 Enum，但不强制特定接口
    private val context: Context,
    private val name: String,
    private val key: String,
    private val defaultValue: E,
    private val toIntValue: (E) -> Int,     // (E) -> Int
    private val fromIntValue: (Int) -> E?   // (Int) -> E?
) : ReadWriteProperty<Any?, E> {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): E {
        if (!sharedPreferences.contains(key)) {
            return defaultValue
        }
        // 如果 defaultValue 本身没有对应的 intValue，这里可能会有问题，
        // 但因为我们检查了 contains，所以存储的 int 应该是存在的。
        val storedInt = sharedPreferences.getInt(key, toIntValue(defaultValue) /* 仅作为 getInt 的备用，实际不会用 */)
        return fromIntValue(storedInt) ?: defaultValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: E) {
        sharedPreferences.edit {
            putInt(key, toIntValue(value))
        }
    }
}