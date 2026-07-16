package com.refreshing.learnenglishwords.data.catalog.source

import android.content.Context
import com.refreshing.learnenglishwords.data.catalog.dto.ManifestDto
import com.refreshing.learnenglishwords.data.catalog.dto.TopicFileDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetBundledCatalogSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) : BundledCatalogSource {

    override suspend fun readManifest(): ManifestDto = withContext(Dispatchers.IO) {
        context.assets.open(MANIFEST_PATH).bufferedReader().use { reader ->
            json.decodeFromString(reader.readText())
        }
    }

    override suspend fun readTopic(topicKey: String): TopicFileDto = withContext(Dispatchers.IO) {
        context.assets.open("$CATALOG_DIR/$topicKey.json").bufferedReader().use { reader ->
            json.decodeFromString(reader.readText())
        }
    }

    private companion object {
        const val CATALOG_DIR = "catalog"
        const val MANIFEST_PATH = "$CATALOG_DIR/manifest.json"
    }
}
