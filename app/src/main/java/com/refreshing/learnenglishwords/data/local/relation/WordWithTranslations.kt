package com.refreshing.learnenglishwords.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.refreshing.learnenglishwords.data.local.entity.WordEntity
import com.refreshing.learnenglishwords.data.local.entity.WordTranslationEntity

data class WordWithTranslations(
    @Embedded val word: WordEntity,
    @Relation(
        parentColumn = "wordUid",
        entityColumn = "wordUid",
    )
    val translations: List<WordTranslationEntity>,
)
