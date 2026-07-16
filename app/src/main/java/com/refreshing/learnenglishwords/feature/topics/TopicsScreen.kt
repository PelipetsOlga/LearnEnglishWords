package com.refreshing.learnenglishwords.feature.topics

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.core.model.Topic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    onTopicClick: (topicKey: String) -> Unit,
    viewModel: TopicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TopicsEffect.NavigateToSubtopics -> onTopicClick(effect.topicKey)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Topics") })
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.topics, key = { it.topicKey }) { topic ->
                TopicCard(
                    topic = topic,
                    onClick = { viewModel.onIntent(TopicsIntent.TopicClicked(topic.topicKey)) },
                    onResetClick = { viewModel.onIntent(TopicsIntent.ResetTopicRequested(topic.topicKey)) },
                )
            }
        }
    }

    if (state.resetConfirmTopicKey != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(TopicsIntent.ResetDismissed) },
            title = { Text("Reset progress?") },
            text = { Text("All progress for this topic will be deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(TopicsIntent.ResetConfirmed) }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(TopicsIntent.ResetDismissed) }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    onClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier.semantics { },
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset progress for ${topic.title}",
                    )
                }
            }
            Text(
                text = "${topic.wordCount} words · ${topic.progressPercent}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { topic.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
    }
}
