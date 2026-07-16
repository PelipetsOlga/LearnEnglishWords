package com.refreshing.learnenglishwords.feature.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.ui.design.LangBadge
import com.refreshing.learnenglishwords.ui.design.WordCardGradient
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppOutline
import com.refreshing.learnenglishwords.ui.theme.AppPrimary
import com.refreshing.learnenglishwords.ui.theme.AppPrimaryContainer
import com.refreshing.learnenglishwords.ui.theme.AppTeal

private val AnswerCorrect = Color(0xFF4CAF50)
private val AnswerWrong = Color(0xFFF44336)

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
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = {
                    if (!state.isLoading && !state.isEmpty && !state.isComplete) {
                        LinearProgressIndicator(
                            progress = {
                                if (state.totalTasks > 0) state.currentIndex.toFloat() / state.totalTasks else 0f
                            },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                            color = AppPrimary,
                            trackColor = AppPrimaryContainer,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onIntent(QuizIntent.CloseClicked) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = AppCardSurface),
                        modifier = Modifier.clip(CircleShape),
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close quiz", tint = AppNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground),
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Word card — purple gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WordCardGradient)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LangBadge(code = task.sourceLanguage)
                    Text(
                        text = "→",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    LangBadge(code = task.targetLanguage)
                }
                Text(
                    text = task.prompt,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Answer options
        task.options.forEach { option ->
            val (bgColor, borderColor, textColor) = when {
                state.selectedOption == null -> Triple(AppCardSurface, AppOutline, AppNavy)
                option == task.correctAnswer -> Triple(AnswerCorrect.copy(alpha = 0.12f), AnswerCorrect, AnswerCorrect)
                option == state.selectedOption && state.isCorrect == false ->
                    Triple(AnswerWrong.copy(alpha = 0.12f), AnswerWrong, AnswerWrong)
                else -> Triple(AppCardSurface, AppOutline, AppGray)
            }
            Card(
                onClick = { if (state.selectedOption == null) onOptionSelected(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Text(
                    text = option,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                )
            }
        }

        if (state.selectedOption != null) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
            ) {
                Text(
                    if (state.isCorrect == true) "Continue" else "Got it",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Composable
private fun CompletionSummary(state: QuizUiState, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Quiz complete!", style = MaterialTheme.typography.headlineSmall, color = AppNavy)
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryRow("Questions answered", state.questionsAnswered.toString())
                SummaryRow("Mistakes", state.mistakeCount.toString())
                SummaryRow("Newly learned", state.newlyLearnedCount.toString())
                SummaryRow("Remaining", state.remainingCount.toString())
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
        ) {
            Text("Done", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = AppGray)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = AppNavy)
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
            color = AppGray,
        )
        Button(
            onClick = onClose,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
        ) { Text("Back") }
    }
}
