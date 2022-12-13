package com.juniori.puzzle.di

import com.juniori.puzzle.data.video.VideoRepositoryImpl
import com.juniori.puzzle.data.video.VideoRepositoryMockImpl
import com.juniori.puzzle.domain.repository.VideoRepository
import com.juniori.puzzle.mock.getVideoListMockData
import com.juniori.puzzle.util.VideoMetaDataUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object VideoModule {
    private val mockVideoList = getVideoListMockData()

    @MockData
    @Provides
    fun provideMockRepository(): VideoRepository = VideoRepositoryMockImpl(mockVideoList)

    @Singleton
    @Provides
    fun provideVideoMetaDataUtil(): VideoMetaDataUtil = VideoMetaDataUtil

    @Singleton
    @Provides
    fun provideRepository(impl: VideoRepositoryImpl): VideoRepository = impl
}
