package com.refreshing.learnenglishwords.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.refreshing.learnenglishwords.data.local.entity.CatalogLanguageEntity
import com.refreshing.learnenglishwords.data.local.entity.CatalogStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {

    @Query("SELECT * FROM catalog_state WHERE id = 1")
    fun observeCatalogState(): Flow<CatalogStateEntity?>

    @Query("SELECT * FROM catalog_state WHERE id = 1")
    suspend fun getCatalogState(): CatalogStateEntity?

    @Upsert
    suspend fun upsertCatalogState(state: CatalogStateEntity)

    @Upsert
    suspend fun upsertCatalogLanguages(languages: List<CatalogLanguageEntity>)

    @Query("DELETE FROM catalog_language")
    suspend fun deleteCatalogLanguages()

    @Query("SELECT * FROM catalog_language WHERE isMain = 0 ORDER BY position ASC")
    fun observeAdditionalLanguages(): Flow<List<CatalogLanguageEntity>>

    @Query("SELECT languageCode FROM catalog_language WHERE isMain = 1 LIMIT 1")
    fun observeMainLanguageCode(): Flow<String?>
}
