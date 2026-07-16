package com.refreshing.learnenglishwords.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.refreshing.learnenglishwords.core.time.TimeProvider
import com.refreshing.learnenglishwords.data.catalog.CatalogValidator
import com.refreshing.learnenglishwords.data.catalog.mapper.CatalogMapper
import com.refreshing.learnenglishwords.data.catalog.source.AssetBundledCatalogSource
import com.refreshing.learnenglishwords.domain.catalog.BootstrapCatalog
import com.refreshing.learnenglishwords.domain.catalog.BootstrapResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogImportTest {

    private lateinit var database: VocabularyDatabase
    private lateinit var bootstrap: BootstrapCatalog

    @OptIn(ExperimentalSerializationApi::class)
    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, VocabularyDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = false
            coerceInputValues = false
            explicitNulls = false
        }
        val fakeTime = object : TimeProvider { override fun currentTimeMillis() = 1_000_000L }
        bootstrap = BootstrapCatalog(
            bundledCatalogSource = AssetBundledCatalogSource(context, json),
            catalogValidator = CatalogValidator(),
            database = database,
            mapper = CatalogMapper(),
            timeProvider = fakeTime,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun bootstrap_imports_3_active_topics() = runBlocking {
        bootstrap.run()
        assertEquals(3, database.topicDao().getActiveTopics().size)
    }

    @Test
    fun bootstrap_imports_28_active_subtopics() = runBlocking {
        bootstrap.run()
        assertEquals(28, database.subtopicDao().getActiveSubtopicCount())
    }

    @Test
    fun bootstrap_imports_550_active_words() = runBlocking {
        bootstrap.run()
        assertEquals(550, database.wordDao().getActiveWordCount())
    }

    @Test
    fun bootstrap_imports_2200_translations() = runBlocking {
        bootstrap.run()
        assertEquals(2200, database.wordDao().getTranslationCount())
    }

    @Test
    fun bootstrap_catalogState_hasCorrectVersion() = runBlocking {
        bootstrap.run()
        val state = database.catalogDao().getCatalogState()
        assertNotNull(state)
        assertEquals("2026.07.16.1", state!!.catalogVersion)
    }

    @Test
    fun bootstrap_isIdempotent_secondRunDoesNotDuplicate() = runBlocking {
        val first = bootstrap.run()
        val second = bootstrap.run()

        assertEquals(BootstrapResult.Imported, first)
        assertEquals(BootstrapResult.AlreadyImported, second)
        assertEquals(3, database.topicDao().getActiveTopics().size)
        assertEquals(550, database.wordDao().getActiveWordCount())
        assertEquals(2200, database.wordDao().getTranslationCount())
    }
}
