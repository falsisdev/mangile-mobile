package com.mangile.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangile.app.theme.MangileTheme
import com.mangile.app.ui.explore.ExploreScreen
import com.mangile.app.ui.home.HomeScreen
import com.mangile.app.ui.home.SettingsScreen
import com.mangile.app.ui.reader.MangaReaderScreen
import com.mangile.app.ui.reader.NovelReaderScreen
import com.mangile.app.ui.title.TitleDetailScreen

sealed class Screen {
    data object Home : Screen()
    data object Explore : Screen()
    data object Settings : Screen()
    data class TitleDetail(val id: String) : Screen()
    data class MangaReader(val chapterId: String) : Screen()
    data class NovelReader(val chapterId: String) : Screen()
}

@Composable
fun App() {
    MangileTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
        val backStack = remember { mutableStateListOf<Screen>() }

        fun navigateTo(screen: Screen) {
            backStack.add(currentScreen)
            currentScreen = screen
        }

        fun goBack() {
            if (backStack.isNotEmpty()) {
                currentScreen = backStack.removeLast()
            }
        }

        val isMainScreen = currentScreen is Screen.Home || currentScreen is Screen.Explore || currentScreen is Screen.Settings

        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = isMainScreen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is Screen.Home,
                            onClick = {
                                if (currentScreen !is Screen.Home) {
                                    backStack.clear()
                                    currentScreen = Screen.Home
                                }
                            },
                            label = { Text("Ana Sayfa") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Explore,
                            onClick = {
                                if (currentScreen !is Screen.Explore) {
                                    backStack.clear()
                                    currentScreen = Screen.Explore
                                }
                            },
                            label = { Text("Keşfet") },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Keşfet") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Settings,
                            onClick = {
                                if (currentScreen !is Screen.Settings) {
                                    backStack.clear()
                                    currentScreen = Screen.Settings
                                }
                            },
                            label = { Text("Ayarlar") },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Ayarlar") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isMainScreen) innerPadding else PaddingValues(0.dp))
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        val isDetailOrReader = targetState is Screen.TitleDetail || targetState is Screen.MangaReader || targetState is Screen.NovelReader
                        val wasDetailOrReader = initialState is Screen.TitleDetail || initialState is Screen.MangaReader || initialState is Screen.NovelReader
                        
                        if (isDetailOrReader && !wasDetailOrReader) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) togetherWith
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                        } else if (!isDetailOrReader && wasDetailOrReader) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) togetherWith
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                        } else {
                            fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                        }
                    }
                ) { screen ->
                    when (screen) {
                        is Screen.Home -> {
                            HomeScreen(
                                onTitleClick = { id -> navigateTo(Screen.TitleDetail(id)) },
                                onChapterClick = { chapterId, isNovel ->
                                    navigateTo(
                                        if (isNovel) Screen.NovelReader(chapterId)
                                        else Screen.MangaReader(chapterId)
                                    )
                                }
                            )
                        }
                        is Screen.Explore -> {
                            ExploreScreen(
                                onTitleClick = { id -> navigateTo(Screen.TitleDetail(id)) }
                            )
                        }
                        is Screen.Settings -> {
                            SettingsScreen()
                        }
                        is Screen.TitleDetail -> {
                            TitleDetailScreen(
                                titleId = screen.id,
                                onBackClick = { goBack() },
                                onChapterClick = { chapterId, isNovel ->
                                    navigateTo(
                                        if (isNovel) Screen.NovelReader(chapterId)
                                        else Screen.MangaReader(chapterId)
                                    )
                                }
                            )
                        }
                        is Screen.MangaReader -> {
                            MangaReaderScreen(
                                chapterId = screen.chapterId,
                                onBackClick = { goBack() },
                                onNavigateChapter = { newChapterId ->
                                    currentScreen = Screen.MangaReader(newChapterId)
                                }
                            )
                        }
                        is Screen.NovelReader -> {
                            NovelReaderScreen(
                                chapterId = screen.chapterId,
                                onBackClick = { goBack() },
                                onNavigateChapter = { newChapterId ->
                                    currentScreen = Screen.NovelReader(newChapterId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
