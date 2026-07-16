package com.refreshing.learnenglishwords.feature.topics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.refreshing.learnenglishwords.core.model.Topic
import com.refreshing.learnenglishwords.ui.design.accentColorAt
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.AppOutline
import com.refreshing.learnenglishwords.ui.theme.AppPrimary

@Composable
fun TopicsScreen(
    onTopicClick: (topicKey: String) -> Unit,
    viewModel: TopicsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TopicsEffect.NavigateToSubtopics -> onTopicClick(effect.topicKey)
            }
        }
    }

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "__title__") {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Topics",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppNavy,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            itemsIndexed(state.topics, key = { _, t -> t.topicKey }) { index, topic ->
                TopicCard(
                    topic = topic,
                    index = index,
                    onClick = { viewModel.onIntent(TopicsIntent.TopicClicked(topic.topicKey)) },
                )
            }
            item(key = "__bottom__") { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    index: Int,
    onClick: () -> Unit,
) {
    val accent = accentColorAt(index)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left colored accent panel with notebook holes
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .fillMaxHeight()
                    .background(accent.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppBackground),
                        )
                    }
                }
            }
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppNavy,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${topic.wordCount} words",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppGray,
                )
                if (topic.progressPercent > 0) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { topic.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = accent,
                        trackColor = accent.copy(alpha = 0.18f),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${topic.progressPercent}%",
                        fontSize = 11.sp,
                        color = accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
