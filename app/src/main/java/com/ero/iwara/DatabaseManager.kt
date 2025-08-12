package com.ero.iwara

import android.content.Context
import com.ero.iwara.dao.UserDao
import com.ero.iwara.entity.TagBase
import com.ero.iwara.entity.UserBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DatabaseManager(context: Context) {

    // 获取 AppDatabase 实例，并通过它获取 userDao
    private val userDao = AppDatabase.getInstance(context.applicationContext).userDao()
    private val tagDao = AppDatabase.getInstance(context.applicationContext).tagDao()

    // --- user 操作的封装 ---
    suspend fun saveUser(username: String, password: String, token: String, accessToken: String): Long {
        return withContext(Dispatchers.IO) { // 确保在 IO 线程执行数据库操作
            val exist = userDao.getUserByUsername(username)
            val user = UserBase(exist?.id ?: 0, username = username, password = password, token = token, accessToken = accessToken)
            if(exist == null) {
                return@withContext userDao.insertUser(user)
            }
            else{
                userDao.updateUser(user)
                return@withContext exist.id
            }
        }
    }

    suspend fun saveTag(name: String, type: String, sensitive: Boolean): Long
    {
        return withContext(Dispatchers.IO) { // 确保在 IO 线程执行数据库操作
            val exist = tagDao.getTagByName(name)
            val tag = TagBase(exist?.id ?: 0, name = name, type = type, sensitive = sensitive , count = 0)
            if(exist == null) {
                return@withContext tagDao.insertTag(tag)
            }
            else{
                tagDao.updateTag(tag)
                return@withContext exist.id
            }
        }
    }

    suspend fun addUser(user: UserBase): Long {
        return withContext(Dispatchers.IO) { // 确保在 IO 线程执行数据库操作
            userDao.insertUser(user)
        }
    }

    suspend fun addUsers(users: List<UserBase>) {
        withContext(Dispatchers.IO) {
            userDao.insertUsers(users)
        }
    }

    suspend fun updateExistingUser(user: UserBase) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

    suspend fun removeUser(user: UserBase) {
        withContext(Dispatchers.IO) {
            userDao.deleteUser(user)
        }
    }

    suspend fun removeUserById(userId: Long) {
        withContext(Dispatchers.IO) {
            userDao.deleteUserById(userId)
        }
    }

    suspend fun clearUsers() {
        withContext(Dispatchers.IO) {
            userDao.clearAllUsers()
        }
    }

    suspend fun fetchUserByUserName(userName: String): UserBase? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUsername(userName)
        }
    }

    suspend fun fetchUserById(userId: Long): UserBase? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }

    fun observeAllUsers(): Flow<List<UserBase>> {
        // Flow 本身是异步的，通常不需要在这里切换 Dispatcher，
        // Room 会在后台线程查询并通过 Flow 发射数据。
        // 调用者在收集 Flow 时可以指定自己的 CoroutineContext。
        return userDao.getAllUsersFlow()
    }

    suspend fun fetchAllUsersList(): List<UserBase> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUsersList()
        }
    }

    suspend fun countUsers(): Int {
        return withContext(Dispatchers.IO) {
            userDao.getUsersCount()
        }
    }

    // 你也可以选择直接暴露 DAO，如果调用者需要更复杂或底层的查询
    fun getUserDao(): UserDao {
     return userDao
    }
}
