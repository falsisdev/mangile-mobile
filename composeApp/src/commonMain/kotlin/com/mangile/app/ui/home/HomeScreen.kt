package com.mangile.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mangile.app.data.api.MangileApi
import com.mangile.app.data.models.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onTitleClick: (String) -> Unit,
    onChapterClick: (String, Boolean) -> Unit
) {
    var popularManga by remember { mutableStateOf<List<MangaListItem>>(emptyList()) }
    var latestChapters by remember { mutableStateOf<List<LatestChapterItem>>(emptyList()) }
    var latestTitles by remember { mutableStateOf<List<LatestTitleItem>>(emptyList()) }
    var taggedTitles by remember { mutableStateOf<Map<String, List<TitleByTagItem>>>(emptyMap()) }
    var heroIndex by remember { mutableStateOf(0) }
    val tags = listOf("Ödüllü", "Macera", "Dram", "Fantezi")

    // Performance fix: Launch each request independently so UI populates progressively
    LaunchedEffect(Unit) {
        launch {
            try { popularManga = MangileApi.getPopularManga() } catch (e: Exception) {}
        }
        launch {
            try { latestChapters = MangileApi.getLatestChapters() } catch (e: Exception) {}
        }
        launch {
            try { latestTitles = MangileApi.getLatestTitles() } catch (e: Exception) {}
        }
        launch {
            tags.forEach { tag ->
                try {
                    val result = MangileApi.getTitlesByTag(tag)
                    taggedTitles = taggedTitles + (tag to result)
                } catch (e: Exception) {}
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Banner
        if (popularManga.isNotEmpty()) {
            item {
                val hero = popularManga[heroIndex % popularManga.size]
                Box(
                    modifier = Modifier
                        .fillMaxWidth().height(260.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { hero.mal_id?.let { onTitleClick(it.toString()) } }
                ) {
                    AsyncImage(
                        model = hero.anilist_banner_image ?: hero.anilist_cover_image,
                        contentDescription = hero.anilist_title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                    )
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    ) {
                        val typeStr = when {
                            hero.mal_type?.contains("Manhwa", true) == true -> "Manhwa"
                            hero.mal_type?.contains("NOVEL", true) == true -> "Hafif Roman"
                            else -> "Manga"
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = typeStr,
                                fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = hero.anilist_title ?: "",
                            color = Color.White, fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            hero.anilist_score?.let {
                                Text(
                                    text = "★ ${"%.1f".format(it / 10.0)}",
                                    color = Color(0xFFFFB300),
                                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            hero.mal_year?.let {
                                Text(text = it.toString(), color = Color.White.copy(0.7f), fontSize = 12.sp)
                            }
                        }
                    }
                }
                // Hero dots
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    popularManga.take(6).forEachIndexed { i, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (heroIndex == i) 8.dp else 6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (heroIndex == i) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(0.3f)
                                )
                                .clickable { heroIndex = i }
                        )
                    }
                }
            }
        }

        // Son Eklenen Bölümler
        if (latestChapters.isNotEmpty()) {
            item {
                SectionTitle("Son Eklenen Bölümler")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(latestChapters) { ch ->
                        val cover = ch.lightNovel?.coverImage?.url ?: ch.manga?.coverImage?.url
                        val series = ch.lightNovel?.title ?: ch.manga?.title ?: "Seri"
                        val isNovel = ch.lightNovel != null
                        Column(
                            modifier = Modifier.width(120.dp)
                                .clickable { onChapterClick(ch._id, isNovel) }
                        ) {
                            Box(
                                Modifier.fillMaxWidth().height(170.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = cover, contentDescription = series,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                // Type badge
                                Surface(
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                    color = if (isNovel) MaterialTheme.colorScheme.secondary
                                           else MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isNovel) "Roman" else "Manga",
                                        fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                        color = if (isNovel) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = series, fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 2, overflow = TextOverflow.Ellipsis,
                                lineHeight = 15.sp
                            )
                            Text(
                                text = "C${ch.volumeNumber?.toInt() ?: 0} B${ch.chapterNumber?.toInt() ?: 0}: ${ch.title ?: ""}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Son Eklenen İçerikler
        if (latestTitles.isNotEmpty()) {
            item {
                SectionTitle("Son Oluşturulan İçerikler")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(latestTitles) { t ->
                        Column(
                            modifier = Modifier.width(120.dp)
                                .clickable { t.myAnimeListId?.let { onTitleClick(it.toString()) } }
                        ) {
                            Box(
                                Modifier.fillMaxWidth().height(170.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = t.coverImage,
                                    contentDescription = t.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = t.title ?: "", fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 2, overflow = TextOverflow.Ellipsis,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Tag sections
        tags.forEach { tag ->
            val items = taggedTitles[tag] ?: emptyList()
            if (items.isNotEmpty()) {
                item {
                    SectionTitle("$tag Türünde Seriler")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items) { t ->
                            Column(
                                modifier = Modifier.width(120.dp)
                                    .clickable { t.myAnimeListId?.let { onTitleClick(it.toString()) } }
                            ) {
                                Box(
                                    Modifier.fillMaxWidth().height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = t.coverImage?.url,
                                        contentDescription = t.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = t.title ?: "", fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Initial empty state padding so the screen is not entirely empty before things load
        if (popularManga.isEmpty() && latestChapters.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp, end = 16.dp)
    )
}
