package com.refreshing.learnenglishwords.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.refreshing.learnenglishwords.data.local.entity.WordProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query(
        "SELECT wp.* FROM word_progress wp " +
            "INNER JOIN word w ON wp.wordUid = w.wordUid " +
            "WHERE w.subtopicUid = :subtopicUid AND w.isActive = 1",
    )
    fun observeProgressForSubtopic(subtopicUid: String): Flow<List<WordProgressEntity>>

    @Query(
        "SELECT wp.* FROM word_progress wp " +
            "INNER JOIN word w ON wp.wordUid = w.wordUid " +
            "INNER JOIN subtopic s ON w.subtopicUid = s.subtopicUid " +
            "WHERE s.topicKey = :topicKey AND w.isActive = 1",
    )
    fun observeProgressForTopic(topicKey: String): Flow<List<WordProgressEntity>>

    @Query(
        "SELECT wp.* FROM word_progress wp " +
            "INNER JOIN word w ON wp.wordUid = w.wordUid " +
            "WHERE w.isActive = 1",
    )
    fun observeAllActiveProgress(): Flow<List<WordProgressEntity>>

    @Upsert
    suspend fun upsertProgress(progress: WordProgressEntity)

    @Query("DELETE FROM word_progress")
    suspend fun deleteAllProgress()

    @Query(
        "DELETE FROM word_progress WHERE wordUid IN (" +
            "SELECT w.wordUid FROM word w " +
            "INNER JOIN subtopic s ON w.subtopicUid = s.subtopicUid " +
            "WHERE s.topicKey = :topicKey)",
    )
    suspend fun deleteProgressForTopic(topicKey: String)

    @Query(
        "DELETE FROM word_progress WHERE wordUid IN (" +
            "SELECT wordUid FROM word WHERE subtopicUid = :subtopicUid)",
    )
    suspend fun deleteProgressForSubtopic(subtopicUid: String)
}
