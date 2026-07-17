package com.refreshing.learnenglishwords.feature.topics

import androidx.compose.foundation.background
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
import com.refreshing.learnenglishwords.ui.preview.PreviewMb
import com.refreshing.learnenglishwords.ui.theme.AppBackground
import com.refreshing.learnenglishwords.ui.theme.AppCardSurface
import com.refreshing.learnenglishwords.ui.theme.AppGray
import com.refreshing.learnenglishwords.ui.theme.AppNavy
import com.refreshing.learnenglishwords.ui.theme.LearnEnglishWordsTheme

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
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                    text = "Learn English Words",
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

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@PreviewMb
@Composable
private fun PreviewTopicCardNoProgress() {
    LearnEnglishWordsTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopicCard(topic = Topic("animals", "Animals", 42, 0), index = 0, onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
            TopicCard(topic = Topic("food", "Food & Drinks", 30, 65), index = 1, onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
            TopicCard(topic = Topic("travel", "Travel", 18, 100), index = 2, onClick = {})
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(accent)
                .padding(start = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .background(accent.copy(alpha = 0.08f))
            ) {

                Column(
                    modifier = Modifier
                        .width(32.dp)
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppBackground),
                        )
                    }

                }
                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(AppCardSurface)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppNavy,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${topic.wordCount} words",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppGray,
                    )
                    if (topic.progressPercent > 0) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { topic.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp)),
                            color = accent,
                            trackColor = accent.copy(alpha = 0.18f),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${topic.progressPercent}%",
                            color = accent,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
