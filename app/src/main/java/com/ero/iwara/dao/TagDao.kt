package com.ero.iwara.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ero.iwara.entity.TagBase
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 冲突时替换
    suspend fun insertTag(tag: TagBase): Long // 返回插入的 rowId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(tag: List<TagBase>)

    @Update
    suspend fun updateTag(tag: TagBase)

    @Delete
    suspend fun deleteTag(tag: TagBase)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: Long)

    @Query("DELETE FROM tags")
    suspend fun clearAllTags()

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): TagBase? // 返回单个 User，可能为 null

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): TagBase? // 返回单个 Tag，可能为 null

    @Query("SELECT * FROM tags ORDER BY id ASC")
    fun getAllTagsFlow(): Flow<List<TagBase>> // 返回一个 Flow，当数据变化时会自动更新

    @Query("SELECT * FROM tags ORDER BY id ASC")
    suspend fun getAllTagsList(): List<TagBase> // 一次性获取列表

    @Query("SELECT COUNT(*) FROM Tags")
    suspend fun getsCount(): Int
}