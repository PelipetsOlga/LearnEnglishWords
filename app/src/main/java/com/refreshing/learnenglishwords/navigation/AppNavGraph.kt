package com.refreshing.learnenglishwords.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.refreshing.learnenglishwords.feature.startup.StartupScreen

object Routes {
    const val STARTUP = "startup"
    const val TOPICS = "topics"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.STARTUP,
    ) {
        composable(Routes.STARTUP) {
            StartupScreen(
                onNavigateToTopics = {
                    navController.navigate(Routes.TOPICS) {
                        popUpTo(Routes.STARTUP) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.TOPICS) {
            // Placeholder replaced in Checkpoint 5
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Topics",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
    }
}
