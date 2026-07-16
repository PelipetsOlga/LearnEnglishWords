package com.refreshing.learnenglishwords.di

import com.refreshing.learnenglishwords.core.util.RandomShuffler
import com.refreshing.learnenglishwords.core.util.Shuffler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShufflerModule {

    @Provides
    @Singleton
    fun provideShuffler(): Shuffler = RandomShuffler
}
