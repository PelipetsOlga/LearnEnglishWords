package com.refreshing.learnenglishwords.feature.progress

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.domain.progress.ProgressAggregator
import com.refreshing.learnenglishwords.ui.design.LangBadge
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppOutline
import com.refreshing.learnenglishwords.ui.theme.AppPrimary
import com.refreshing.learnenglishwords.ui.theme.AppPrimaryContainer
import com.refreshing.learnenglishwords.ui.theme.AppRed

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(containerColor = AppBackground) { innerPadding ->
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.headlineMedium,
                color = AppNavy,
            )

            OverallCard(stats = state.overall)

            if (state.overall.directions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "By direction",
                            style = MaterialTheme.typography.titleSmall,
                            color = AppGray,
                        )
                        Spacer(Modifier.height(12.dp))
                        state.overall.directions.forEachIndexed { idx, dir ->
                            if (idx > 0) HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = AppOutline,
                            )
                            DirectionRow(dir)
                        }
                    }
                }
            }

            if (state.overall.failureCount > 0) {
                Text(
                    text = "Total mistakes: ${state.overall.failureCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppGray,
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { viewModel.onIntent(ProgressIntent.ResetAllRequested) },
                colors = ButtonDefaults.buttonColors(containerColor = AppRed),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Reset all progress", style = MaterialTheme.typography.titleSmall)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            CircularGauge(percent = stats.percent, modifier = Modifier.size(88.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Overall progress",
                    style = MaterialTheme.typography.titleSmall,
                    color = AppGray,
                )
                Text(
                    text = "${stats.percent}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppNavy,
                )
                Text(
                    text = "${stats.learnedCount} learned / ${stats.totalCount} total",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppGray,
                )
            }
        }
    }
}

@Composable
private fun CircularGauge(percent: Int, modifier: Modifier = Modifier) {
    val primary = AppPrimary
    val track = AppPrimaryContainer
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = track,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
            )
            drawArc(
                color = primary,
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                style = stroke,
            )
        }
        Text(
            text = "$percent%",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppNavy,
        )
    }
}

@Composable
private fun DirectionRow(dir: ProgressAggregator.DirectionStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            LangBadge(code = dir.sourceLanguage)
            Text("→", style = MaterialTheme.typography.bodyMedium, color = AppGray)
            LangBadge(code = dir.targetLanguage)
        }
        Text(
            text = "${dir.percent}% (${dir.learnedCount}/${dir.totalCount})",
            style = MaterialTheme.typography.bodyMedium,
            color = AppNavy,
        )
    }
}
