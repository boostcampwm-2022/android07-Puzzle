package com.juniori.puzzle.data.firebase

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.juniori.puzzle.util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIME_OUT_MILLIS = 5000L

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
        .readTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
        .writeTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
        .addNetworkInterceptor {
            val request = it.request()
                .newBuilder()
                .build()
            it.proceed(request)
        }
        .build()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideFirebaseService(retrofit: Retrofit): FirebaseService =
        retrofit.create(FirebaseService::class.java)

    @Singleton
    @Provides
    fun provideFirebaseRepository(service: FirebaseService): FirebaseRepository =
        FirebaseRepository(service)
}
