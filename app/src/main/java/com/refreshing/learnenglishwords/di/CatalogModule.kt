package com.refreshing.learnenglishwords.di

import com.refreshing.learnenglishwords.data.catalog.source.AssetBundledCatalogSource
import com.refreshing.learnenglishwords.data.catalog.source.BundledCatalogSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    @Binds
    @Singleton
    abstract fun bindBundledCatalogSource(
        impl: AssetBundledCatalogSource,
    ): BundledCatalogSource
}
