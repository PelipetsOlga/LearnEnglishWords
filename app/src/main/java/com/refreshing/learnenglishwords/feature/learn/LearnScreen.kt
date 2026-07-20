package com.refreshing.learnenglishwords.feature.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.ui.design.LangBadge
import com.refreshing.learnenglishwords.ui.design.SparkleIcon
import com.refreshing.learnenglishwords.ui.preview.PreviewMb
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.LearnEnglishWordsTheme
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppOutline
import com.refreshing.learnenglishwords.ui.theme.AppPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onNavigateBack: () -> Unit,
    onStartQuiz: () -> Unit,
    viewModel: LearnViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LearnEffect.NavigateToQuiz -> onStartQuiz()
            }
        }
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.subtopicTitle.ifEmpty { "Vocabulary" },
                        style = MaterialTheme.typography.titleLarge,
                        color = AppNavy,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = AppCardSurface),
                        modifier = Modifier.clip(CircleShape),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground),
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.isEmpty -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No words available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppGray,
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                        .testTag("learn_word_list"),
                ) {
                    items(state.words, key = { it.wordUid }) { word ->
                        WordRow(word = word)
                        HorizontalDivider(color = AppOutline)
                    }
                }

                Button(
                    onClick = { viewModel.onIntent(LearnIntent.StartQuizClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(52.dp)
                        .testTag("start_quiz_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                ) {
                    Text("Start Quiz", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@PreviewMb
@Composable
private fun PreviewWordRowNone() {
    LearnEnglishWordsTheme {
        WordRow(LearnWordItem("1", "apple", listOf("uk" to "яблуко"), WordStatus.NONE))
    }
}

@PreviewMb
@Composable
private fun PreviewWordRowLearned() {
    LearnEnglishWordsTheme {
        WordRow(LearnWordItem("2", "beautiful", listOf("uk" to "гарний"), WordStatus.LEARNED))
    }
}

@PreviewMb
@Composable
private fun PreviewWordRowMistakes() {
    LearnEnglishWordsTheme {
        WordRow(LearnWordItem("3", "knowledge", listOf("uk" to "знання"), WordStatus.HAS_MISTAKES))
    }
}

@PreviewMb
@Composable
private fun PreviewWordRowMultiLang() {
    LearnEnglishWordsTheme {
        WordRow(
            LearnWordItem(
                "4",
                "cat",
                listOf("uk" to "кіт", "pl" to "kot"),
                WordStatus.LEARNED,
            ),
        )
    }
}

@Composable
private fun WordRow(word: LearnWordItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = word.mainTranslation,
                style = MaterialTheme.typography.bodyLarge,
                color = AppNavy,
            )
            word.additionalTranslations.forEach { (lang, text) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LangBadge(code = lang)
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppGray,
                    )
                }
            }
        }
        Spacer(Modifier.padding(start = 8.dp))
        when (word.status) {
            WordStatus.LEARNED -> SparkleIcon()
            WordStatus.HAS_MISTAKES -> Icon(
                Icons.Default.Warning,
                contentDescription = "Has mistakes",
                tint = MaterialTheme.colorScheme.error,
            )
            WordStatus.NONE -> {}
        }
    }
}
