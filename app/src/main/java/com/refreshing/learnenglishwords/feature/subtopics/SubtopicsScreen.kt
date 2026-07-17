package com.refreshing.learnenglishwords.feature.subtopics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.core.model.Subtopic
import com.refreshing.learnenglishwords.ui.design.AccentAvatar
import com.refreshing.learnenglishwords.ui.design.accentColorAt
import com.refreshing.learnenglishwords.ui.preview.PreviewMb
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.LearnEnglishWordsTheme
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtopicsScreen(
    onNavigateBack: () -> Unit,
    onLearnClick: (subtopicUid: String) -> Unit,
    onQuizClick: (subtopicUid: String) -> Unit,
    onQuizTopicClick: (topicKey: String) -> Unit,
    onLearnTopicClick: (topicKey: String) -> Unit,
    viewModel: SubtopicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SubtopicsEffect.NavigateToLearn -> onLearnClick(effect.subtopicUid)
                is SubtopicsEffect.NavigateToQuiz -> onQuizClick(effect.subtopicUid)
                is SubtopicsEffect.NavigateToQuizTopic -> onQuizTopicClick(effect.topicKey)
                is SubtopicsEffect.NavigateToLearnTopic -> onLearnTopicClick(effect.topicKey)
            }
        }
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.topicTitle.ifEmpty { state.topicKey },
                        style = MaterialTheme.typography.titleLarge,
                        color = AppNavy,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = AppCardSurface,
                        ),
                        modifier = Modifier.clip(CircleShape),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppNavy,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onIntent(SubtopicsIntent.ResetTopicRequested) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = AppCardSurface,
                        ),
                        modifier = Modifier.clip(CircleShape),
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Reset progress for this topic",
                            tint = AppGray,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground),
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = AppBackground) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(
                        onClick = { viewModel.onIntent(SubtopicsIntent.QuizTopicClicked) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null)
                        Text(
                            "Quiz all",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(state.subtopics, key = { _, s -> s.subtopicUid }) { index, subtopic ->
                    SubtopicCard(
                        subtopic = subtopic,
                        index = index,
                        onLearnClick = { viewModel.onIntent(SubtopicsIntent.LearnClicked(subtopic.subtopicUid)) },
                        onQuizClick = { viewModel.onIntent(SubtopicsIntent.QuizClicked(subtopic.subtopicUid)) },
                        onResetClick = { viewModel.onIntent(SubtopicsIntent.ResetSubtopicRequested(subtopic.subtopicUid)) },
                    )
                }
                item(key = "__all_words__") {
                    AllWordsCard(
                        onClick = { viewModel.onIntent(SubtopicsIntent.LearnTopicClicked) },
                    )
                }
                item(key = "__bottom__") { Spacer(Modifier.height(4.dp)) }
            }
        }
    }

    if (state.resetConfirmSubtopicUid != null) {
        val subtopicTitle = state.subtopics
            .find { it.subtopicUid == state.resetConfirmSubtopicUid }?.title
            ?: state.resetConfirmSubtopicUid
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(SubtopicsIntent.ResetSubtopicDismissed) },
            title = { Text("Reset subtopic progress?") },
            text = { Text("All progress for \"$subtopicTitle\" will be deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(SubtopicsIntent.ResetSubtopicConfirmed) }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(SubtopicsIntent.ResetSubtopicDismissed) }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (state.resetConfirmTopic) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(SubtopicsIntent.ResetTopicDismissed) },
            title = { Text("Reset topic progress?") },
            text = { Text("All progress for \"${state.topicTitle.ifEmpty { state.topicKey }}\" will be deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(SubtopicsIntent.ResetTopicConfirmed) }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(SubtopicsIntent.ResetTopicDismissed) }) {
                    Text("Cancel")
                }
            },
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@PreviewMb
@Composable
private fun PreviewSubtopicCardNoProgress() {
    LearnEnglishWordsTheme {
        SubtopicCard(
            subtopic = Subtopic("animals/wild", "animals", "Wild Animals", 20, 0),
            index = 0,
            onLearnClick = {},
            onQuizClick = {},
            onResetClick = {},
        )
    }
}

@PreviewMb
@Composable
private fun PreviewSubtopicCardWithProgress() {
    LearnEnglishWordsTheme {
        SubtopicCard(
            subtopic = Subtopic("animals/domestic", "animals", "Domestic Animals", 15, 73),
            index = 1,
            onLearnClick = {},
            onQuizClick = {},
            onResetClick = {},
        )
    }
}

@PreviewMb
@Composable
private fun PreviewAllWordsCardPreview() {
    LearnEnglishWordsTheme {
        AllWordsCard(onClick = {})
    }
}

@Composable
private fun SubtopicCard(
    subtopic: Subtopic,
    index: Int,
    onLearnClick: () -> Unit,
    onQuizClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    val accent = accentColorAt(index)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AccentAvatar(index = index, label = subtopic.title)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = subtopic.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = AppNavy,
                )
                Text(
                    text = "${subtopic.wordCount} words · ${subtopic.progressPercent}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppGray,
                )
                if (subtopic.progressPercent > 0) {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { subtopic.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = accent,
                        trackColor = accent.copy(alpha = 0.18f),
                    )
                }
            }
            // Tinted icon buttons
            IconButton(
                onClick = onLearnClick,
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = accent.copy(alpha = 0.10f),
                ),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Learn ${subtopic.title}",
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(
                onClick = onQuizClick,
                modifier = Modifier.size(36.dp).padding(start = 4.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = AppPrimary.copy(alpha = 0.10f),
                ),
            ) {
                Icon(
                    Icons.Default.Quiz,
                    contentDescription = "Quiz ${subtopic.title}",
                    tint = AppPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(
                onClick = onResetClick,
                modifier = Modifier.size(36.dp).padding(start = 4.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = AppGray.copy(alpha = 0.10f),
                ),
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset ${subtopic.title}",
                    tint = AppGray,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun AllWordsCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "All words",
                style = MaterialTheme.typography.titleSmall,
                color = AppNavy,
            )
            Icon(
                Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "All words",
                tint = AppPrimary,
            )
        }
    }
}
