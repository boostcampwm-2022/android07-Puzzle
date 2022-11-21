package com.juniori.puzzle.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockData

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealData