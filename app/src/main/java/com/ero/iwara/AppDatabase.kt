package com.ero.iwara

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ero.iwara.dao.UserDao
import com.ero.iwara.entity.UserBase

@Database(entities = [UserBase::class], version = 1, exportSchema = false) // 列出实体，定义版本
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao // Room 会自动实现这个方法

    companion object {
        @Volatile // 确保实例的可见性
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "ero_iwara.db"

        fun getInstance(context: Context): AppDatabase {
            // 双重检查锁定，确保线程安全且只创建一个实例
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // .fallbackToDestructiveMigration() // 如果不想提供迁移策略，版本升级时会销毁并重建数据库
                    // .addMigrations(MIGRATION_1_2, ...) // 添加迁移策略
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}