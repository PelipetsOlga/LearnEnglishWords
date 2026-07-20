package com.refreshing.learnenglishwords.di

import android.content.Context
import androidx.room.Room
import com.refreshing.learnenglishwords.data.local.VocabularyDatabase
import com.refreshing.learnenglishwords.data.local.dao.CatalogDao
import com.refreshing.learnenglishwords.data.local.dao.ProgressDao
import com.refreshing.learnenglishwords.data.local.dao.SubtopicDao
import com.refreshing.learnenglishwords.data.local.dao.TopicDao
import com.refreshing.learnenglishwords.data.local.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): VocabularyDatabase =
        Room.inMemoryDatabaseBuilder(context, VocabularyDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideCatalogDao(db: VocabularyDatabase): CatalogDao = db.catalogDao()

    @Provides
    fun provideTopicDao(db: VocabularyDatabase): TopicDao = db.topicDao()

    @Provides
    fun provideSubtopicDao(db: VocabularyDatabase): SubtopicDao = db.subtopicDao()

    @Provides
    fun provideWordDao(db: VocabularyDatabase): WordDao = db.wordDao()

    @Provides
    fun provideProgressDao(db: VocabularyDatabase): ProgressDao = db.progressDao()
}
