package com.ero.iwara.stroage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface BaseDao<T> {
    @RawQuery()
    suspend fun query(query: SupportSQLiteQuery): List<T>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<T>): List<Long>

    @Upsert()
    suspend fun upsert(entity: T)

    @Upsert()
    suspend fun upsert(list: List<T>)

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int
}