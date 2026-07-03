package com.ero.iwara.stroage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs") // 定义表名
data class LogBase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uid: Long,
    val type: Long,
    val uname: String,
    val action: String,
    val message: String,
    val datetime: String
)