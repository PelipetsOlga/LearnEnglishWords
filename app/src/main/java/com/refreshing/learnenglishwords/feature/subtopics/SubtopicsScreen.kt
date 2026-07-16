package com.refreshing.learnenglishwords.feature.subtopics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.core.model.Subtopic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtopicsScreen(
    onNavigateBack: () -> Unit,
    onLearnClick: (subtopicUid: String) -> Unit,
    onQuizClick: (subtopicUid: String) -> Unit,
    viewModel: SubtopicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SubtopicsEffect.NavigateToLearn -> onLearnClick(effect.subtopicUid)
                is SubtopicsEffect.NavigateToQuiz -> onQuizClick(effect.subtopicUid)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.topicKey) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.subtopics, key = { it.subtopicUid }) { subtopic ->
                    SubtopicCard(
                        subtopic = subtopic,
                        onLearnClick = { viewModel.onIntent(SubtopicsIntent.LearnClicked(subtopic.subtopicUid)) },
                        onQuizClick = { viewModel.onIntent(SubtopicsIntent.QuizClicked(subtopic.subtopicUid)) },
                        onResetClick = { viewModel.onIntent(SubtopicsIntent.ResetSubtopicRequested(subtopic.subtopicUid)) },
                    )
                }
            }
        }
    }

    if (state.resetConfirmSubtopicUid != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(SubtopicsIntent.ResetDismissed) },
            title = { Text("Reset progress?") },
            text = { Text("All progress for this subtopic will be deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(SubtopicsIntent.ResetConfirmed) }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(SubtopicsIntent.ResetDismissed) }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun SubtopicCard(
    subtopic: Subtopic,
    onLearnClick: () -> Unit,
    onQuizClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = subtopic.title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onLearnClick) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Learn")
                }
                IconButton(onClick = onQuizClick) {
                    Icon(Icons.Default.Quiz, contentDescription = "Quiz")
                }
                IconButton(onClick = onResetClick) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset subtopic progress")
                }
            }
            Text(
                text = "${subtopic.wordCount} words · ${subtopic.progressPercent}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { subtopic.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
}
