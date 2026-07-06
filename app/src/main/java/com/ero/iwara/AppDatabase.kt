package com.ero.iwara

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ero.iwara.stroage.dao.LogDao
import com.ero.iwara.stroage.dao.TagDao
import com.ero.iwara.stroage.dao.UserDao
import com.ero.iwara.stroage.entity.LogBase
import com.ero.iwara.stroage.entity.TagBase
import com.ero.iwara.stroage.entity.UserBase

@Database(entities = [LogBase::class, TagBase::class, UserBase::class], version = 1, exportSchema = false) // 列出实体，定义版本
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun tagDao(): TagDao
    abstract fun logDao(): LogDao

    companion object {
        const val DATABASE_NAME = "iwara.db"
    }
}