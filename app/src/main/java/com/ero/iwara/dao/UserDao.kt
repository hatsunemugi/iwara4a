package com.ero.iwara.dao

import androidx.room.*
import com.ero.iwara.entity.UserBase
import kotlinx.coroutines.flow.Flow // 用于异步获取数据流

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // 冲突时替换
    suspend fun insertUser(user: UserBase): Long // 返回插入的 rowId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(user: List<UserBase>)

    @Update
    suspend fun updateUser(user: UserBase)

    @Delete
    suspend fun deleteUser(user: UserBase)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Long)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserBase? // 返回单个 User，可能为 null

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserBase? // 返回单个 User，可能为 null

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsersFlow(): Flow<List<UserBase>> // 返回一个 Flow，当数据变化时会自动更新

    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAllUsersList(): List<UserBase> // 一次性获取列表

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int
}