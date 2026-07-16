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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.domain.progress.ProgressAggregator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Progress") })
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
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

