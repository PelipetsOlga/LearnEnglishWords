package com.refreshing.learnenglishwords.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.refreshing.learnenglishwords.feature.learn.LearnScreen
import com.refreshing.learnenglishwords.feature.progress.ProgressScreen
import com.refreshing.learnenglishwords.feature.quiz.QuizScreen
import com.refreshing.learnenglishwords.feature.settings.SettingsScreen
import com.refreshing.learnenglishwords.feature.startup.StartupScreen
import com.refreshing.learnenglishwords.feature.subtopics.SubtopicsScreen
import com.refreshing.learnenglishwords.feature.topics.TopicsScreen

// ---------------------------------------------------------------------------
// Routes
// ---------------------------------------------------------------------------

object Routes {
    const val STARTUP = "startup"
    const val TOPICS = "topics"
    const val TOPIC_DETAIL = "topic/{topicKey}"
    const val LEARN = "learn/{subtopicUid}"
    const val QUIZ = "quiz/{subtopicUid}"
    const val PROGRESS = "progress"
    const val SETTINGS = "settings"

    fun topicDetail(topicKey: String) = "topic/$topicKey"
    fun learn(subtopicUid: String) = "learn/${Uri.encode(subtopicUid)}"
    fun quiz(subtopicUid: String) = "quiz/${Uri.encode(subtopicUid)}"
}

// ---------------------------------------------------------------------------
// Bottom navigation items
// ---------------------------------------------------------------------------

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.TOPICS, "Topics", Icons.Default.List),
    BottomNavItem(Routes.PROGRESS, "Progress", Icons.Default.Star),
    BottomNavItem(Routes.SETTINGS, "Settings", Icons.Default.Settings),
)

private val bottomNavRoutes = bottomNavItems.map { it.route }.toSet()

// ---------------------------------------------------------------------------
// Nav graph
// ---------------------------------------------------------------------------

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        // Pop to Topics (the first bottom-bar destination) so the
                                        // back stack never grows beyond one level across tabs.
                                        popUpTo(Routes.TOPICS) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
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
                TopicsScreen(
                    onTopicClick = { topicKey ->
                        navController.navigate(Routes.topicDetail(topicKey))
                    },
                )
            }

            composable(
                route = Routes.TOPIC_DETAIL,
                arguments = listOf(navArgument("topicKey") { type = NavType.StringType }),
            ) { _ ->
                SubtopicsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLearnClick = { subtopicUid ->
                        navController.navigate(Routes.learn(subtopicUid))
                    },
                    onQuizClick = { subtopicUid ->
                        navController.navigate(Routes.quiz(subtopicUid))
                    },
                )
            }

            composable(
                route = Routes.LEARN,
                arguments = listOf(navArgument("subtopicUid") { type = NavType.StringType }),
            ) { backStackEntry ->
                val subtopicUid = Uri.decode(backStackEntry.arguments?.getString("subtopicUid") ?: "")
                LearnScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onStartQuiz = {
                        navController.navigate(Routes.quiz(subtopicUid)) {
                            popUpTo(Routes.TOPIC_DETAIL) // keep subtopics in back stack
                        }
                    },
                )
            }

            composable(
                route = Routes.QUIZ,
                arguments = listOf(navArgument("subtopicUid") { type = NavType.StringType }),
            ) { backStackEntry ->
                val subtopicUid = Uri.decode(backStackEntry.arguments?.getString("subtopicUid") ?: "")
                QuizScreen(
                    subtopicUid = subtopicUid,
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Routes.PROGRESS) {
                ProgressScreen()
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}
