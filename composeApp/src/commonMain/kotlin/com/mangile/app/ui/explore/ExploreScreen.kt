package com.mangile.app.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mangile.app.data.api.MangileApi
import com.mangile.app.data.models.*
import kotlinx.coroutines.delay

enum class ExploreMode { MENU, ALL, LOCAL }

@Composable
fun ExploreScreen(
    onTitleClick: (String) -> Unit
) {
    var mode by remember { mutableStateOf(ExploreMode.MENU) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // All mode state
    var allResults by remember { mutableStateOf<List<MangaListItem>>(emptyList()) }
    var selectedSort by remember { mutableStateOf("POPULARITY_DESC") }
    var selectedFormat by remember { mutableStateOf("ALL") }

    // Local mode state
    var localResults by remember { mutableStateOf<List<LocalTitleItem>>(emptyList()) }
    var selectedTag by remember { mutableStateOf("ALL") }
    var selectedType by remember { mutableStateOf("ALL") }

    val tags = listOf("ALL", "Ödüllü", "Aksiyon", "Macera", "Dram", "Fantezi", "Korku", "Romantik", "Komedi", "Psikolojik")

    // Load data when mode or filters change
    LaunchedEffect(mode, searchQuery, selectedSort, selectedFormat, selectedTag, selectedType) {
        if (mode == ExploreMode.MENU) return@LaunchedEffect
        // Only debounce if the user is actively typing a search query
        if (searchQuery.isNotEmpty()) {
            delay(350) 
        }
        isLoading = true
        when (mode) {
            ExploreMode.ALL -> {
                try {
                    val resp = MangileApi.searchManga(
                        query = searchQuery, sort = selectedSort, format = selectedFormat
                    )
                    allResults = resp.data
                } catch (e: Exception) {}
            }
            ExploreMode.LOCAL -> {
                try {
                    val resp = MangileApi.getLocalTitles(
                        search = searchQuery, tag = selectedTag, type = selectedType
                    )
                    localResults = resp.data
                } catch (e: Exception) {}
            }
            else -> {}
        }
        isLoading = false
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp).statusBarsPadding()
    ) {
        when (mode) {
            ExploreMode.MENU -> {
                Text(
                    "Keşfet", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                // All series card
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                        .clickable { mode = ExploreMode.ALL },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.CenterStart) {
                        Column {
                            Text("Tüm Serileri Keşfet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("AniList veritabanından binlerce seri", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Local series card
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                        .clickable { mode = ExploreMode.LOCAL },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.CenterStart) {
                        Column {
                            Text("Mangile Serilerini Keşfet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
                            Text("Türkçe çeviri yapılan yerel serimiz", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondary.copy(0.7f))
                        }
                    }
                }
            }

            ExploreMode.ALL, ExploreMode.LOCAL -> {
                // Back + Search
                Row(
                    Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { mode = ExploreMode.MENU; searchQuery = "" }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Ara...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Filter chips
                if (mode == ExploreMode.ALL) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("POPULARITY_DESC" to "Popüler", "SCORE_DESC" to "Puan", "TRENDING_DESC" to "Trend").forEach { (v, l) ->
                            FilterChip(
                                selected = selectedSort == v,
                                onClick = { selectedSort = v },
                                label = { Text(l, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ALL" to "Tümü", "MANGA" to "Manga", "NOVEL" to "Roman").forEach { (v, l) ->
                            FilterChip(
                                selected = selectedFormat == v,
                                onClick = { selectedFormat = v },
                                label = { Text(l, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                if (mode == ExploreMode.LOCAL) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ALL" to "Tümü", "manga" to "Manga", "lightNovel" to "Roman").forEach { (v, l) ->
                            FilterChip(
                                selected = selectedType == v,
                                onClick = { selectedType = v },
                                label = { Text(l, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.take(5).forEach { tag ->
                            FilterChip(
                                selected = selectedTag == tag,
                                onClick = { selectedTag = tag },
                                label = { Text(if (tag == "ALL") "Tümü" else tag, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    if (mode == ExploreMode.ALL) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(allResults) { item ->
                                Column(
                                    Modifier.clickable { item.mal_id?.let { onTitleClick(it.toString()) } }
                                ) {
                                    Box(
                                        Modifier.fillMaxWidth().height(150.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        AsyncImage(
                                            model = item.anilist_cover_image, contentDescription = item.anilist_title,
                                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        item.anilist_title ?: "", fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onBackground, lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(localResults) { item ->
                                Column(
                                    Modifier.clickable { item.myAnimeListId?.let { onTitleClick(it.toString()) } }
                                ) {
                                    Box(
                                        Modifier.fillMaxWidth().height(150.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        AsyncImage(
                                            model = item.coverImage, contentDescription = item.title,
                                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        item.title ?: "", fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onBackground, lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
