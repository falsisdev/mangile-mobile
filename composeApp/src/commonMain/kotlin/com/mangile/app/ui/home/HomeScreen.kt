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
import com.mangile.app.data.api.MangileApiClient
import com.mangile.app.data.models.LatestChapterItem
import com.mangile.app.data.models.LatestTitleItem
import com.mangile.app.data.models.MangaListItem

@Composable
fun HomeScreen(
    apiClient: MangileApiClient,
    onTitleClick: (String) -> Unit,
    onChapterClick: (String, Boolean) -> Unit // isNovel: Boolean
) {
    var popularManga by remember { mutableStateOf<List<MangaListItem>>(emptyList()) }
    var latestChapters by remember { mutableStateOf<List<LatestChapterItem>>(emptyList()) }
    var latestTitles by remember { mutableStateOf<List<LatestTitleItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        popularManga = apiClient.getPopularManga()
        latestChapters = apiClient.getLatestChapters()
        latestTitles = apiClient.getLatestTitles()
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Banner Slider
            if (popularManga.isNotEmpty()) {
                item {
                    HeroSliderSection(
                        items = popularManga.take(6),
                        onItemClick = { item ->
                            item.mal_id?.let { onTitleClick(it.toString()) }
                        }
                    )
                }
            }

            // Son Eklenen Bölümler
            if (latestChapters.isNotEmpty()) {
                item {
                    SectionHeader(title = "Son Eklenen Bölümler")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(latestChapters) { chapter ->
                            LatestChapterCard(
                                chapter = chapter,
                                onClick = {
                                    val isNovel = chapter.lightNovel != null
                                    onChapterClick(chapter._id, isNovel)
                                }
                            )
                        }
                    }
                }
            }

            // Son Eklenen Seriler
            if (latestTitles.isNotEmpty()) {
                item {
                    SectionHeader(title = "Son Eklenen İçerikler")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(latestTitles) { titleItem ->
                            LatestTitleCard(
                                titleItem = titleItem,
                                onClick = {
                                    val id = titleItem.myAnimeListId?.toString() ?: titleItem._id
                                    id?.let { onTitleClick(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroSliderSection(
    items: List<MangaListItem>,
    onItemClick: (MangaListItem) -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentItem = items.getOrNull(currentIndex) ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClick(currentItem) }
    ) {
        AsyncImage(
            model = currentItem.anilist_banner_image,
            contentDescription = currentItem.anilist_title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Karartma Katmanı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = currentItem.mal_type ?: "Manga",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currentItem.anilist_title ?: "Bilinmeyen Başlık",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (currentItem.anilist_score != null) {
                Text(
                    text = "★ ${(currentItem.anilist_score / 10.0).let { "%.1f".format(it) }}",
                    color = Color(0xFFFBBF24),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 12.dp)
    )
}

@Composable
fun LatestChapterCard(
    chapter: LatestChapterItem,
    onClick: () -> Unit
) {
    val coverUrl = chapter.lightNovel?.coverImage?.url ?: chapter.manga?.coverImage?.url
    val seriesTitle = chapter.lightNovel?.title ?: chapter.manga?.title ?: "Seri"

    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = coverUrl,
                contentDescription = seriesTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = seriesTitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "Bölüm ${chapter.chapterNumber?.toInt() ?: "-"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LatestTitleCard(
    titleItem: LatestTitleItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = titleItem.coverImage?.url,
                contentDescription = titleItem.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = titleItem.title ?: "Başlıksız",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
