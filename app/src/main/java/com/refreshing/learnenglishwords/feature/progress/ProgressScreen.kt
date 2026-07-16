package com.refreshing.learnenglishwords.feature.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.domain.progress.ProgressAggregator

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Overall summary card
        OverallCard(stats = state.overall)

        // Per-direction breakdown
        if (state.overall.directions.isNotEmpty()) {
            Text("By direction", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    state.overall.directions.forEachIndexed { idx, dir ->
                        if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DirectionRow(dir)
                    }
                }
            }
        }

        // Per-topic breakdown
        if (state.topics.isNotEmpty()) {
            Text("By topic", style = MaterialTheme.typography.titleMedium)
            state.topics.forEach { row ->
                TopicProgressCard(
                    row = row,
                    onResetClick = { viewModel.onIntent(ProgressIntent.ResetTopicRequested(row.topicKey)) },
                )
            }
        }

        // Failure total
        if (state.overall.failureCount > 0) {
            Text(
                text = "Total mistakes: ${state.overall.failureCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Reset all
        Button(
            onClick = { viewModel.onIntent(ProgressIntent.ResetAllRequested) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Reset all progress")
        }
    }

    if (state.resetAllConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProgressIntent.ResetAllDismissed) },
            title = { Text("Reset all progress?") },
            text = { Text("All learning progress will be permanently deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ProgressIntent.ResetAllConfirmed) }) {
                    Text("Reset all", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProgressIntent.ResetAllDismissed) }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (state.resetTopicConfirmKey != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProgressIntent.ResetTopicDismissed) },
            title = { Text("Reset topic progress?") },
            text = { Text("All progress for this topic will be deleted.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ProgressIntent.ResetTopicConfirmed) }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProgressIntent.ResetTopicDismissed) }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun OverallCard(stats: ProgressAggregator.Stats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Overall progress", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${stats.percent}%",
                style = MaterialTheme.typography.displayMedium,
            )
            LinearProgressIndicator(
                progress = { stats.percent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${stats.learnedCount} learned / ${stats.totalCount} total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DirectionRow(dir: ProgressAggregator.DirectionStats) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${dir.sourceLanguage.uppercase()} → ${dir.targetLanguage.uppercase()}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "${dir.percent}% (${dir.learnedCount}/${dir.totalCount})",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        LinearProgressIndicator(
            progress = { dir.percent / 100f },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun TopicProgressCard(
    row: TopicProgressRow,
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
                    text = row.title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${row.stats.percent}%",
                    style = MaterialTheme.typography.bodyLarge,
                )
                IconButton(onClick = onResetClick) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset topic")
                }
            }
            LinearProgressIndicator(
                progress = { row.stats.percent / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            )
            Text(
                text = "${row.stats.learnedCount}/${row.stats.totalCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
