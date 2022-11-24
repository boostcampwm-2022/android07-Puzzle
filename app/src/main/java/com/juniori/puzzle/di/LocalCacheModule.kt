package com.juniori.puzzle.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalCacheModule {

    const val CACHE_DIR_PATH = "CACHE_DIR_PATH"

    @Singleton
    @Provides
    @Named(CACHE_DIR_PATH)
    fun provideCacheDirPath(@ApplicationContext context: Context): String =
        context.cacheDir.path
}
