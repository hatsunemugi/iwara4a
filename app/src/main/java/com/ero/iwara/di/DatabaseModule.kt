package com.ero.iwara.di

import android.content.Context

import com.ero.iwara.AppDatabase
import com.ero.iwara.DatabaseManager
import com.ero.iwara.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 通常数据库是应用范围的单例
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideDatabaseManager(@ApplicationContext context: Context): DatabaseManager {
        // 如果 DatabaseManager 内部直接获取 AppDatabase.getInstance(context).noteDao()
        // 那么它只需要 ApplicationContext
        return DatabaseManager(context)
        // 或者，如果 DatabaseManager 接收 NoteDao 作为参数：
        // fun provideDatabaseManager(noteDao: NoteDao): DatabaseManager {
        //     return DatabaseManager(noteDao) // 假设 DatabaseManager 构造函数改为 (private val noteDao: NoteDao)
        // }
    }
}