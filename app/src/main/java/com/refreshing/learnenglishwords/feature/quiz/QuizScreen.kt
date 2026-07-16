package com.refreshing.learnenglishwords.feature.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                QuizEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!state.isLoading && !state.isEmpty && !state.isComplete) {
                        LinearProgressIndicator(
                            progress = {
                                if (state.totalTasks > 0) state.currentIndex.toFloat() / state.totalTasks else 0f
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onIntent(QuizIntent.CloseClicked) }) {
                        Icon(Icons.Default.Close, contentDescription = "Close quiz")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()

                state.isEmpty -> EmptyQuizContent(onClose = { viewModel.onIntent(QuizIntent.CloseClicked) })

                state.isComplete -> CompletionSummary(
                    state = state,
                    onClose = { viewModel.onIntent(QuizIntent.CloseClicked) },
                )

                state.currentTask != null -> QuestionContent(
                    state = state,
                    onOptionSelected = { viewModel.onIntent(QuizIntent.OptionSelected(it)) },
                    onContinue = { viewModel.onIntent(QuizIntent.ContinueClicked) },
                )
            }
        }
    }

    if (state.showCloseConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(QuizIntent.CloseDismissed) },
            title = { Text("Quit quiz?") },
            text = { Text("Your progress so far has been saved, but the remaining questions will be lost.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(QuizIntent.CloseConfirmed) }) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(QuizIntent.CloseDismissed) }) {
                    Text("Continue quiz")
                }
            },
        )
    }
}

@Composable
private fun QuestionContent(
    state: QuizUiState,
    onOptionSelected: (String) -> Unit,
    onContinue: () -> Unit,
) {
    val task = state.currentTask ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "${state.sourceLanguage(task)} → ${state.targetLanguage(task)}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = task.prompt,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        task.options.forEach { option ->
            val containerColor = when {
                state.selectedOption == null -> MaterialTheme.colorScheme.surface
                option == task.correctAnswer -> Color(0xFF4CAF50)
                option == state.selectedOption && state.isCorrect == false -> Color(0xFFF44336)
                else -> MaterialTheme.colorScheme.surface
            }
            OutlinedButton(
                onClick = { if (state.selectedOption == null) onOptionSelected(option) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor),
                enabled = state.selectedOption == null,
            ) {
                Text(option)
            }
        }

        if (state.selectedOption != null) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isCorrect == true) "Continue" else "Got it")
            }
        }
    }
}

@Composable
private fun CompletionSummary(state: QuizUiState, onClose: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Quiz complete!", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        SummaryRow("Questions answered", state.questionsAnswered.toString())
        SummaryRow("Mistakes", state.mistakeCount.toString())
        SummaryRow("Newly learned", state.newlyLearnedCount.toString())
        SummaryRow("Remaining", state.remainingCount.toString())
        Spacer(Modifier.height(16.dp))
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text("Done")
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EmptyQuizContent(onClose: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "All done! Nothing left to quiz.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onClose) { Text("Back") }
    }
}

private fun QuizUiState.sourceLanguage(task: com.refreshing.learnenglishwords.core.model.QuizTask) =
    task.sourceLanguage.uppercase()

private fun QuizUiState.targetLanguage(task: com.refreshing.learnenglishwords.core.model.QuizTask) =
    task.targetLanguage.uppercase()
