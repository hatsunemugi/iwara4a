package com.ero.iwara.di

import com.ero.iwara.api.IwaraApi
import com.ero.iwara.api.IwaraApiImpl
import com.ero.iwara.api.service.IwaraParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// User Agent
//private const val USER_AGENT =
//    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideIwaraParser() = IwaraParser()

    @Provides
    @Singleton
    fun provideIwaraApi(iwaraParser: IwaraParser): IwaraApi = IwaraApiImpl(iwaraParser)
}