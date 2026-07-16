package com.refreshing.learnenglishwords.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.refreshing.learnenglishwords.data.local.entity.SubtopicEntity
import com.refreshing.learnenglishwords.data.local.entity.SubtopicTitleEntity
import com.refreshing.learnenglishwords.data.local.relation.SubtopicWordCount
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtopicDao {

    @Query("SELECT * FROM subtopic WHERE topicKey = :topicKey AND isActive = 1 ORDER BY position ASC")
    fun observeActiveSubtopics(topicKey: String): Flow<List<SubtopicEntity>>

    @Query("SELECT * FROM subtopic WHERE subtopicUid = :subtopicUid")
    fun observeSubtopic(subtopicUid: String): Flow<SubtopicEntity?>

    @Query("SELECT COUNT(*) FROM subtopic WHERE isActive = 1")
    suspend fun getActiveSubtopicCount(): Int

    @Query("SELECT * FROM subtopic_title WHERE subtopicUid = :subtopicUid")
    fun observeSubtopicTitles(subtopicUid: String): Flow<List<SubtopicTitleEntity>>

    @Query(
        "SELECT st.* FROM subtopic_title st INNER JOIN subtopic s ON st.subtopicUid = s.subtopicUid " +
            "WHERE s.topicKey = :topicKey AND s.isActive = 1",
    )
    fun observeSubtopicTitlesForTopic(topicKey: String): Flow<List<SubtopicTitleEntity>>

    @Query(
        "SELECT s.subtopicUid, COUNT(w.wordUid) as wordCount " +
            "FROM subtopic s LEFT JOIN word w ON w.subtopicUid = s.subtopicUid AND w.isActive = 1 " +
            "WHERE s.topicKey = :topicKey AND s.isActive = 1 GROUP BY s.subtopicUid",
    )
    fun observeWordCountsForTopic(topicKey: String): Flow<List<SubtopicWordCount>>

    @Query("SELECT * FROM subtopic_title WHERE subtopicUid = :subtopicUid")
    suspend fun getTitlesForSubtopic(subtopicUid: String): List<SubtopicTitleEntity>

    @Upsert
    suspend fun upsertSubtopic(subtopic: SubtopicEntity)

    @Upsert
    suspend fun upsertSubtopicTitles(titles: List<SubtopicTitleEntity>)

    @Query(
        "UPDATE subtopic SET isActive = 0 " +
            "WHERE topicKey = :topicKey AND subtopicUid NOT IN (:activeUids)",
    )
    suspend fun markSubtopicsInactiveExcept(topicKey: String, activeUids: List<String>)
}
