package com.juniori.puzzle.di

import com.juniori.puzzle.data.location.LocationDataSource
import com.juniori.puzzle.data.location.LocationDataSourceImpl
import com.juniori.puzzle.data.location.LocationRepositoryImpl
import com.juniori.puzzle.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun providesLocationRepository(impl: LocationRepositoryImpl): LocationRepository = impl

    @Singleton
    @Provides
    fun providesLocationDataSource(impl: LocationDataSourceImpl): LocationDataSource = impl

}