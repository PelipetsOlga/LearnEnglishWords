package com.refreshing.learnenglishwords.di

import com.refreshing.learnenglishwords.data.progress.ProgressRepositoryImpl
import com.refreshing.learnenglishwords.domain.progress.ProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProgressModule {

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository
}
