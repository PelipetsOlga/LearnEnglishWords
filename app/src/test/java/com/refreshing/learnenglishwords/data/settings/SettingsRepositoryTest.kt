package com.refreshing.learnenglishwords.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import com.refreshing.learnenglishwords.data.local.dao.CatalogDao
import com.refreshing.learnenglishwords.data.local.entity.CatalogLanguageEntity
import com.refreshing.learnenglishwords.data.local.entity.CatalogStateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var fakeCatalogDao: FakeCatalogDao
    private lateinit var repository: SettingsRepositoryImpl

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create {
            tmpFolder.newFile("test_settings.preferences_pb")
        }
        fakeCatalogDao = FakeCatalogDao()
        repository = SettingsRepositoryImpl(dataStore, fakeCatalogDao)
    }

    // --- defaults ---

    @Test
    fun `defaults - quiz order is RANDOM`() = runTest {
        val settings = repository.observeSettings().first()
        assertEquals(QuizOrder.RANDOM, settings.quizOrder)
    }

    @Test
    fun `defaults - translation direction is MAIN_TO_ADDITIONAL`() = runTest {
        val settings = repository.observeSettings().first()
        assertEquals(TranslationDirection.MAIN_TO_ADDITIONAL, settings.translationDirection)
    }

    @Test
    fun `defaults - selected languages default to pl only on first launch`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru", "pl"))
        val settings = repository.observeSettings().first()
        assertEquals(setOf("pl"), settings.selectedLanguages)
    }

    @Test
    fun `defaults - quiz language defaults to pl`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru", "pl"))
        val settings = repository.observeSettings().first()
        assertEquals("pl", settings.quizLanguage)
    }

    @Test
    fun `setQuizLanguage - persists and reflects in observeSettings`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru", "pl"))
        repository.setQuizLanguage("uk")
        val settings = repository.observeSettings().first()
        assertEquals("uk", settings.quizLanguage)
    }

    // --- quiz order ---

    @Test
    fun `setQuizOrder - persists and reflects in observeSettings`() = runTest {
        repository.setQuizOrder(QuizOrder.EXISTING)
        val settings = repository.observeSettings().first()
        assertEquals(QuizOrder.EXISTING, settings.quizOrder)
    }

    // --- translation direction ---

    @Test
    fun `setTranslationDirection - persists ADDITIONAL_TO_MAIN`() = runTest {
        repository.setTranslationDirection(TranslationDirection.ADDITIONAL_TO_MAIN)
        val settings = repository.observeSettings().first()
        assertEquals(TranslationDirection.ADDITIONAL_TO_MAIN, settings.translationDirection)
    }

    @Test
    fun `setTranslationDirection - persists BOTH`() = runTest {
        repository.setTranslationDirection(TranslationDirection.BOTH)
        val settings = repository.observeSettings().first()
        assertEquals(TranslationDirection.BOTH, settings.translationDirection)
    }

    // --- selected languages ---

    @Test
    fun `setSelectedLanguages - updates selected set`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru", "pl"))
        repository.setSelectedLanguages(setOf("uk"))
        val settings = repository.observeSettings().first()
        assertEquals(setOf("uk"), settings.selectedLanguages)
    }

    @Test
    fun `setSelectedLanguages - empty set is ignored`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru"))
        repository.setSelectedLanguages(setOf("uk"))
        repository.setSelectedLanguages(emptySet()) // should be ignored
        val settings = repository.observeSettings().first()
        assertEquals(setOf("uk"), settings.selectedLanguages)
    }

    // --- catalog change handling ---

    @Test
    fun `unsupported languages are removed when catalog changes`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru", "pl"))
        repository.setSelectedLanguages(setOf("uk", "ru", "pl"))
        // Catalog drops "pl"
        fakeCatalogDao.setLanguages(listOf("uk", "ru"))
        val settings = repository.observeSettings().first()
        assertFalse("pl should not be selected", "pl" in settings.selectedLanguages)
        assertTrue("uk should remain", "uk" in settings.selectedLanguages)
    }

    @Test
    fun `if all saved languages removed from catalog, fall back to all available`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru"))
        repository.setSelectedLanguages(setOf("uk"))
        // Catalog completely changes to "pl" only
        fakeCatalogDao.setLanguages(listOf("pl"))
        val settings = repository.observeSettings().first()
        assertEquals(setOf("pl"), settings.selectedLanguages)
    }

    // --- observeAvailableLanguages ---

    @Test
    fun `observeAvailableLanguages returns languages in catalog order`() = runTest {
        fakeCatalogDao.setLanguages(listOf("uk", "ru", "pl"))
        val langs = repository.observeAvailableLanguages().first()
        assertEquals(listOf("uk", "ru", "pl"), langs)
    }
}

// ---------------------------------------------------------------------------
// Fakes
// ---------------------------------------------------------------------------

private class FakeCatalogDao : CatalogDao {

    private val languagesFlow = MutableStateFlow<List<CatalogLanguageEntity>>(emptyList())

    fun setLanguages(codes: List<String>) {
        languagesFlow.value = codes.mapIndexed { index, code ->
            CatalogLanguageEntity(languageCode = code, isMain = false, position = index)
        }
    }

    override fun observeAdditionalLanguages(): Flow<List<CatalogLanguageEntity>> = languagesFlow

    // --- unused stubs ---
    override fun observeCatalogState(): Flow<CatalogStateEntity?> = MutableStateFlow(null)
    override suspend fun getCatalogState(): CatalogStateEntity? = null
    override suspend fun upsertCatalogState(state: CatalogStateEntity) = Unit
    override suspend fun upsertCatalogLanguages(languages: List<CatalogLanguageEntity>) = Unit
    override suspend fun deleteCatalogLanguages() = Unit
    override fun observeMainLanguageCode(): Flow<String?> = MutableStateFlow("en")
}
