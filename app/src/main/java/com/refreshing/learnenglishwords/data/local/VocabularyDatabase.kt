package com.refreshing.learnenglishwords.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.refreshing.learnenglishwords.data.local.dao.CatalogDao
import com.refreshing.learnenglishwords.data.local.dao.ProgressDao
import com.refreshing.learnenglishwords.data.local.dao.SubtopicDao
import com.refreshing.learnenglishwords.data.local.dao.TopicDao
import com.refreshing.learnenglishwords.data.local.dao.WordDao
import com.refreshing.learnenglishwords.data.local.entity.CatalogLanguageEntity
import com.refreshing.learnenglishwords.data.local.entity.CatalogStateEntity
import com.refreshing.learnenglishwords.data.local.entity.SubtopicEntity
import com.refreshing.learnenglishwords.data.local.entity.SubtopicTitleEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicTitleEntity
import com.refreshing.learnenglishwords.data.local.entity.WordEntity
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import com.refreshing.learnenglishwords.data.local.entity.WordTranslationEntity

@Database(
    entities = [
        CatalogStateEntity::class,
        CatalogLanguageEntity::class,
        TopicEntity::class,
        TopicTitleEntity::class,
        SubtopicEntity::class,
        SubtopicTitleEntity::class,
        WordEntity::class,
        WordTranslationEntity::class,
        WordProgressEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class VocabularyDatabase : RoomDatabase() {
    abstract fun catalogDao(): CatalogDao
    abstract fun topicDao(): TopicDao
    abstract fun subtopicDao(): SubtopicDao
    abstract fun wordDao(): WordDao
    abstract fun progressDao(): ProgressDao
}
