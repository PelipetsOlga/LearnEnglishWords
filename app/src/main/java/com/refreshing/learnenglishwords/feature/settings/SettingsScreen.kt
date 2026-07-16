package com.refreshing.learnenglishwords.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
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
            // ── 1. Vocabulary settings ──────────────────────────────────────
            SectionHeader("Vocabulary settings")

            SubsectionHeader("Languages")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.availableLanguages.forEach { lang ->
                    val isSelected = lang in state.selectedLanguages
                    val isOnlySelected = isSelected && state.selectedLanguages.size == 1
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (!isOnlySelected) {
                                viewModel.onIntent(SettingsIntent.VocabLanguageToggled(lang))
                            }
                        },
                        label = { Text(langLabel(lang)) },
                    )
                }
            }

            HorizontalDivider()

            // ── 2. Quiz settings ────────────────────────────────────────────
            SectionHeader("Quiz settings")

            SubsectionHeader("Language")
            state.availableLanguages.forEach { lang ->
                RadioRow(
                    label = langLabel(lang),
                    selected = state.quizLanguage == lang,
                    onClick = { viewModel.onIntent(SettingsIntent.QuizLanguageChanged(lang)) },
                )
            }

            SubsectionHeader("Words order")
            QuizOrder.entries.forEach { order ->
                RadioRow(
                    label = orderLabel(order),
                    selected = state.quizOrder == order,
                    onClick = { viewModel.onIntent(SettingsIntent.QuizOrderChanged(order)) },
                )
            }

            SubsectionHeader("Translation direction")
            TranslationDirection.entries.forEach { direction ->
                RadioRow(
                    label = directionLabel(direction),
                    selected = state.translationDirection == direction,
                    onClick = { viewModel.onIntent(SettingsIntent.TranslationDirectionChanged(direction)) },
                )
            }

            HorizontalDivider()

            // ── 3. Catalog info ─────────────────────────────────────────────
            SectionHeader("Catalog")
            if (state.catalogVersion.isNotEmpty()) {
                Text(
                    text = "Version: ${state.catalogVersion}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (state.catalogGeneratedAt > 0L) {
                Text(
                    text = "Generated: ${formatDate(state.catalogGeneratedAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SubsectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = label, role = Role.RadioButton, onClick = onClick)
            .semantics { role = Role.RadioButton },
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun langLabel(code: String) = when (code) {
    "uk" -> "UA"
    else -> code.uppercase()
}

private fun orderLabel(order: QuizOrder) = when (order) {
    QuizOrder.RANDOM -> "Random"
    QuizOrder.EXISTING -> "Existing"
}

private fun directionLabel(direction: TranslationDirection) = when (direction) {
    TranslationDirection.MAIN_TO_ADDITIONAL -> "Main → Additional"
    TranslationDirection.ADDITIONAL_TO_MAIN -> "Additional → Main"
    TranslationDirection.BOTH -> "Both directions"
}

private fun formatDate(epochMs: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(epochMs))
