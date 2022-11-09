package com.wisnu.speechrecognition.data.di

import android.content.Context
import com.wisnu.speechrecognition.data.repository.AuthRepository
import com.wisnu.speechrecognition.data.repository.ScoreRepository
import com.wisnu.speechrecognition.data.source.remote.AuthRemoteDataSource
import com.wisnu.speechrecognition.data.source.remote.ScoreRemoteDataSource
import com.wisnu.speechrecognition.session.UserPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserSharedPreference(@ApplicationContext context: Context) = UserPreference(context)

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource() = AuthRemoteDataSource()

    @Provides
    @Singleton
    fun provideScoreRemoteDataSource() = ScoreRemoteDataSource()

    @Provides
    @Singleton
    fun provideAuthRepository(dataSource: AuthRemoteDataSource) = AuthRepository(dataSource)

    @Provides
    @Singleton
    fun provideScoreRepository(dataSource: ScoreRemoteDataSource) = ScoreRepository(dataSource)

}