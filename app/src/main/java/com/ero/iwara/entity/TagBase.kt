package com.ero.iwara.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags") // 定义表名
data class TagBase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 主键，自动生成
    val name: String,
    val type: String,
    val count: Int,
    val sensitive: Boolean
)