package com.refreshing.learnenglishwords.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppOutline
import com.refreshing.learnenglishwords.ui.theme.AppPrimary
import com.refreshing.learnenglishwords.ui.theme.AppTeal
import com.refreshing.learnenglishwords.ui.theme.AppTealContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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
            // Large title with gear icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppNavy,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = AppPrimary,
                )
            }

            // ── 1. Vocabulary settings ──────────────────────────────────────
            SettingsCard {
                SectionLabel("Vocabulary")
                Spacer(Modifier.height(8.dp))
                SubLabel("Display languages")
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppTeal,
                                selectedLabelColor = AppCardSurface,
                                containerColor = AppTealContainer,
                                labelColor = AppNavy,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = AppOutline,
                                selectedBorderColor = AppTeal,
                            ),
                        )
                    }
                }
            }

            HorizontalDivider(color = AppOutline)

            // ── 2. Quiz settings ────────────────────────────────────────────
            SettingsCard {
                SectionLabel("Quiz")
                Spacer(Modifier.height(8.dp))

                SubLabel("Language")
                state.availableLanguages.forEach { lang ->
                    RadioRow(
                        label = langLabel(lang),
                        selected = state.quizLanguage == lang,
                        onClick = { viewModel.onIntent(SettingsIntent.QuizLanguageChanged(lang)) },
                    )
                }

                Spacer(Modifier.height(8.dp))
                SubLabel("Words order")
                QuizOrder.entries.forEach { order ->
                    RadioRow(
                        label = orderLabel(order),
                        selected = state.quizOrder == order,
                        onClick = { viewModel.onIntent(SettingsIntent.QuizOrderChanged(order)) },
                    )
                }

                Spacer(Modifier.height(8.dp))
                SubLabel("Translation direction")
                TranslationDirection.entries.forEach { direction ->
                    RadioRow(
                        label = directionLabel(direction),
                        selected = state.translationDirection == direction,
                        onClick = { viewModel.onIntent(SettingsIntent.TranslationDirectionChanged(direction)) },
                    )
                }
            }

            HorizontalDivider(color = AppOutline)

            // ── 3. Catalog info ─────────────────────────────────────────────
            SettingsCard {
                SectionLabel("Catalog")
                Spacer(Modifier.height(8.dp))
                if (state.catalogVersion.isNotEmpty()) {
                    Text(
                        text = "Version: ${state.catalogVersion}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppGray,
                    )
                }
                if (state.catalogGeneratedAt > 0L) {
                    Text(
                        text = "Generated: ${formatDate(state.catalogGeneratedAt)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppGray,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            content()
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = AppNavy)
}

@Composable
private fun SubLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = AppGray,
    )
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = label, role = Role.RadioButton, onClick = onClick)
            .semantics { role = Role.RadioButton }
            .padding(vertical = 2.dp),
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = AppPrimary),
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = AppNavy,
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
