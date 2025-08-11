package com.ero.iwara.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") // 定义表名
data class UserBase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 主键，自动生成
    val state: Int = 1,
    val username: String,
    val password: String,
    val token: String? = null,
    val accessToken: String? = null
)