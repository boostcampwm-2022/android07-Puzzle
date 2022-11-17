package com.juniori.puzzle.di

import com.juniori.puzzle.data.video.VideoRepository
import com.juniori.puzzle.data.video.VideoRepositoryMockImpl
import com.juniori.puzzle.mock.getVideoListMockData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object VideoModule {
    private val mockVideoList = getVideoListMockData()

    @Provides
    fun provideMockRepository(): VideoRepository = VideoRepositoryMockImpl(mockVideoList)
}