package com.refreshing.learnenglishwords.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = Color.White,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppNavy,
    secondary = AppTeal,
    onSecondary = Color.White,
    secondaryContainer = AppTealContainer,
    onSecondaryContainer = AppNavy,
    tertiary = AppGold,
    onTertiary = AppNavy,
    background = AppBackground,
    onBackground = AppNavy,
    surface = AppCardSurface,
    onSurface = AppNavy,
    surfaceVariant = AppPrimaryContainer,
    onSurfaceVariant = AppGray,
    outline = AppOutline,
    error = AppRed,
    onError = Color.White,
)

@Composable
fun LearnEnglishWordsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content,
    )
}
