package com.ero.iwara.stroage.dao

import androidx.room.Dao
import androidx.room.Query
import com.ero.iwara.stroage.entity.LogBase

@Dao
interface LogDao: BaseDao<LogBase> {
    @Query("delete from logs where (uid = 0 and type & 3 > 0) or datetime(datetime, '+7 days')  < datetime('now', 'localtime')")
    suspend fun clear(): Int

    @Query("select count(1) from logs")
    suspend fun count(): Int
}