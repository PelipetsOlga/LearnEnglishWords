package com.refreshing.learnenglishwords.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.refreshing.learnenglishwords.data.local.entity.TopicEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicTitleEntity
import com.refreshing.learnenglishwords.data.local.relation.TopicWordCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query("SELECT * FROM topic WHERE isActive = 1 ORDER BY position ASC")
    fun observeActiveTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topic WHERE topicKey = :topicKey")
    fun observeTopic(topicKey: String): Flow<TopicEntity?>

    @Query("SELECT * FROM topic WHERE isActive = 1 ORDER BY position ASC")
    suspend fun getActiveTopics(): List<TopicEntity>

    @Query("SELECT tt.* FROM topic_title tt INNER JOIN topic t ON tt.topicKey = t.topicKey WHERE t.isActive = 1")
    fun observeAllActiveTopicTitles(): Flow<List<TopicTitleEntity>>

    @Query(
        "SELECT s.topicKey, COUNT(w.wordUid) as wordCount " +
            "FROM subtopic s LEFT JOIN word w ON w.subtopicUid = s.subtopicUid AND w.isActive = 1 " +
            "WHERE s.isActive = 1 GROUP BY s.topicKey",
    )
    fun observeWordCountsPerTopic(): Flow<List<TopicWordCount>>

    @Query("SELECT * FROM topic_title WHERE topicKey = :topicKey")
    suspend fun getTitlesForTopic(topicKey: String): List<TopicTitleEntity>

    @Upsert
    suspend fun upsertTopic(topic: TopicEntity)

    @Upsert
    suspend fun upsertTopicTitles(titles: List<TopicTitleEntity>)

    @Query("UPDATE topic SET isActive = 0 WHERE topicKey NOT IN (:activeKeys)")
    suspend fun markTopicsInactiveExcept(activeKeys: List<String>)
}
