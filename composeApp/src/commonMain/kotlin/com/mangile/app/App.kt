package com.mangile.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mangile.app.data.api.MangileApiClient
import com.mangile.app.theme.MangileTheme
import com.mangile.app.ui.explore.ExploreScreen
import com.mangile.app.ui.home.HomeScreen
import com.mangile.app.ui.reader.MangaReaderScreen
import com.mangile.app.ui.reader.NovelReaderScreen
import com.mangile.app.ui.title.TitleDetailScreen

sealed class Screen {
    data object Home : Screen()
    data object Explore : Screen()
    data class TitleDetail(val id: String) : Screen()
    data class MangaReader(val chapterId: String) : Screen()
    data class NovelReader(val chapterId: String) : Screen()
}

@Composable
fun App() {
    MangileTheme {
        val apiClient = remember { MangileApiClient() }
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        Scaffold(
            bottomBar = {
                // Sadece ana sayfa ve keşfet ekranlarında alt gezinme çubuğunu göster
                if (currentScreen is Screen.Home || currentScreen is Screen.Explore) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is Screen.Home,
                            onClick = { currentScreen = Screen.Home },
                            label = { Text("Ana Sayfa") },
                            icon = { Text("🏠") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.Explore,
                            onClick = { currentScreen = Screen.Explore },
                            label = { Text("Keşfet") },
                            icon = { Text("🔍") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = if (currentScreen is Screen.Home || currentScreen is Screen.Explore) {
                            innerPadding.calculateBottomPadding()
                        } else {
                            androidx.compose.ui.unit.Dp.Unspecified
                        }
                    )
            ) {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            apiClient = apiClient,
                            onTitleClick = { id -> currentScreen = Screen.TitleDetail(id) },
                            onChapterClick = { chapterId, isNovel ->
                                currentScreen = if (isNovel) {
                                    Screen.NovelReader(chapterId)
                                } else {
                                    Screen.MangaReader(chapterId)
                                }
                            }
                        )
                    }
                    is Screen.Explore -> {
                        ExploreScreen(
                            apiClient = apiClient,
                            onTitleClick = { id -> currentScreen = Screen.TitleDetail(id) }
                        )
                    }
                    is Screen.TitleDetail -> {
                        TitleDetailScreen(
                            titleId = screen.id,
                            apiClient = apiClient,
                            onBackClick = { currentScreen = Screen.Home },
                            onChapterClick = { chapterId ->
                                currentScreen = Screen.MangaReader(chapterId)
                            }
                        )
                    }
                    is Screen.MangaReader -> {
                        MangaReaderScreen(
                            chapterId = screen.chapterId,
                            apiClient = apiClient,
                            onBackClick = { currentScreen = Screen.Home }
                        )
                    }
                    is Screen.NovelReader -> {
                        NovelReaderScreen(
                            chapterId = screen.chapterId,
                            apiClient = apiClient,
                            onBackClick = { currentScreen = Screen.Home }
                        )
                    }
                }
            }
        }
    }
}
