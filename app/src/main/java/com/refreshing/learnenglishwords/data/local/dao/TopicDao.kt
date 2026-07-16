package com.refreshing.learnenglishwords.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.refreshing.learnenglishwords.data.local.entity.TopicEntity
import com.refreshing.learnenglishwords.data.local.entity.TopicTitleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query("SELECT * FROM topic WHERE isActive = 1 ORDER BY position ASC")
    fun observeActiveTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topic WHERE topicKey = :topicKey")
    fun observeTopic(topicKey: String): Flow<TopicEntity?>

    @Query("SELECT * FROM topic WHERE isActive = 1 ORDER BY position ASC")
    suspend fun getActiveTopics(): List<TopicEntity>

    @Query("SELECT * FROM topic_title WHERE topicKey = :topicKey")
    suspend fun getTitlesForTopic(topicKey: String): List<TopicTitleEntity>

    @Upsert
    suspend fun upsertTopic(topic: TopicEntity)

    @Upsert
    suspend fun upsertTopicTitles(titles: List<TopicTitleEntity>)

    @Query("UPDATE topic SET isActive = 0 WHERE topicKey NOT IN (:activeKeys)")
    suspend fun markTopicsInactiveExcept(activeKeys: List<String>)
}
