package com.refreshing.learnenglishwords.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.refreshing.learnenglishwords.data.local.entity.WordEntity
import com.refreshing.learnenglishwords.data.local.entity.WordTranslationEntity
import com.refreshing.learnenglishwords.data.local.relation.WordWithTranslations
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Transaction
    @Query("SELECT * FROM word WHERE subtopicUid = :subtopicUid AND isActive = 1 ORDER BY position ASC")
    fun observeActiveWordsWithTranslations(subtopicUid: String): Flow<List<WordWithTranslations>>

    @Transaction
    @Query("SELECT * FROM word WHERE subtopicUid = :subtopicUid AND isActive = 1 ORDER BY position ASC")
    suspend fun getActiveWordsWithTranslations(subtopicUid: String): List<WordWithTranslations>

    @Query("SELECT COUNT(*) FROM word WHERE isActive = 1")
    suspend fun getActiveWordCount(): Int

    @Query("SELECT COUNT(*) FROM word_translation")
    suspend fun getTranslationCount(): Int

    @Upsert
    suspend fun upsertWord(word: WordEntity)

    @Upsert
    suspend fun upsertWordTranslations(translations: List<WordTranslationEntity>)

    @Query(
        "DELETE FROM word_translation WHERE wordUid = :wordUid AND language NOT IN (:keepLanguages)",
    )
    suspend fun deleteObsoleteTranslations(wordUid: String, keepLanguages: List<String>)

    @Query(
        "UPDATE word SET isActive = 0 " +
            "WHERE subtopicUid = :subtopicUid AND wordUid NOT IN (:activeUids)",
    )
    suspend fun markWordsInactiveExcept(subtopicUid: String, activeUids: List<String>)
}
