package com.refreshing.learnenglishwords.feature.settings

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.core.model.QuizOrder
import com.refreshing.learnenglishwords.core.model.TranslationDirection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // --- Quiz order ---
        Text("Quiz order", style = MaterialTheme.typography.titleMedium)
        QuizOrder.entries.forEach { order ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RadioButton(
                    selected = state.quizOrder == order,
                    onClick = { viewModel.onIntent(SettingsIntent.QuizOrderChanged(order)) },
                )
                Text(
                    text = order.name.lowercase().replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // --- Language multi-select ---
        Text("Languages", style = MaterialTheme.typography.titleMedium)
        if (state.availableLanguages.isEmpty()) {
            Text(
                text = "No additional languages available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.availableLanguages.forEach { lang ->
                    val isSelected = lang in state.selectedLanguages
                    val isOnlySelected = isSelected && state.selectedLanguages.size == 1
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (!isOnlySelected) {
                                viewModel.onIntent(SettingsIntent.LanguageToggled(lang))
                            }
                        },
                        label = { Text(lang.uppercase()) },
                    )
                }
            }
            if (state.selectedLanguages.size == 1) {
                Text(
                    text = "At least one language must be selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // --- Translation direction ---
        Text("Translation direction", style = MaterialTheme.typography.titleMedium)
        TranslationDirection.entries.forEach { direction ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RadioButton(
                    selected = state.translationDirection == direction,
                    onClick = { viewModel.onIntent(SettingsIntent.TranslationDirectionChanged(direction)) },
                )
                Text(
                    text = directionLabel(direction),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // --- Catalog metadata ---
        Text("Catalog", style = MaterialTheme.typography.titleMedium)
        if (state.catalogVersion.isNotEmpty()) {
            Text(
                text = "Version: ${state.catalogVersion}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (state.catalogGeneratedAt > 0L) {
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(Date(state.catalogGeneratedAt))
            Text(
                text = "Generated: $dateStr",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun directionLabel(direction: TranslationDirection): String = when (direction) {
    TranslationDirection.MAIN_TO_ADDITIONAL -> "Main → Additional"
    TranslationDirection.ADDITIONAL_TO_MAIN -> "Additional → Main"
    TranslationDirection.BOTH -> "Both directions"
}
