package com.refreshing.learnenglishwords.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refreshing.learnenglishwords.ui.theme.AccentGold
import com.refreshing.learnenglishwords.ui.theme.AccentPurple
import com.refreshing.learnenglishwords.ui.theme.AccentTeal
import com.refreshing.learnenglishwords.ui.theme.AppGold
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppTeal
import com.refreshing.learnenglishwords.ui.theme.AppTealContainer
import com.refreshing.learnenglishwords.ui.theme.WordCardBottom
import com.refreshing.learnenglishwords.ui.theme.WordCardTop

fun accentColorAt(index: Int): Color = when (index % 3) {
    0 -> AccentPurple
    1 -> AccentTeal
    else -> AccentGold
}

val WordCardGradient = Brush.verticalGradient(listOf(WordCardTop, WordCardBottom))

@Composable
fun LangBadge(code: String, modifier: Modifier = Modifier) {
    val display = if (code == "uk") "UA" else code.uppercase()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(AppTealContainer)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = display,
            color = AppNavy,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
fun SparkleIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.AutoAwesome,
        contentDescription = "Learned",
        tint = AppGold,
        modifier = modifier.size(20.dp),
    )
}

@Composable
fun AccentAvatar(index: Int, label: String, modifier: Modifier = Modifier) {
    val color = accentColorAt(index)
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.18f))
            .border(1.5.dp, color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.take(1).uppercase(),
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
