package com.mangile.app.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mangile.app.data.api.MangileApiClient
import com.mangile.app.data.models.ChapterDetail

enum class ReadingMode {
    WEBTOON, // Dikey Sonsuz Kaydırma
    PAGED    // Yatay Sayfa Sayfa
}

@Composable
fun MangaReaderScreen(
    chapterId: String,
    apiClient: MangileApiClient,
    onBackClick: () -> Unit
) {
    var chapterDetail by remember { mutableStateOf<ChapterDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var readingMode by remember { mutableStateOf(ReadingMode.WEBTOON) }
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(chapterId) {
        isLoading = true
        chapterDetail = apiClient.getChapter(chapterId)
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        val detail = chapterDetail
        if (detail == null || detail.pages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Bu bölüme ait sayfa bulunamadı.", color = Color.White)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Okuyucu İçeriği
                if (readingMode == ReadingMode.WEBTOON) {
                    WebtoonReader(
                        pages = detail.pages.mapNotNull { it.url },
                        onTap = { showControls = !showControls }
                    )
                } else {
                    PagedReader(
                        pages = detail.pages.mapNotNull { it.url },
                        onTap = { showControls = !showControls }
                    )
                }

                // Üst Bar Kontrolü
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    ReaderTopBar(
                        title = detail.title ?: "Bölüm ${detail.chapterNumber?.toInt() ?: ""}",
                        readingMode = readingMode,
                        onBackClick = onBackClick,
                        onToggleMode = {
                            readingMode = if (readingMode == ReadingMode.WEBTOON) {
                                ReadingMode.PAGED
                            } else {
                                ReadingMode.WEBTOON
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WebtoonReader(
    pages: List<String>,
    onTap: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .clickable { onTap() }
    ) {
        itemsIndexed(pages) { index, imageUrl ->
            ZoomableImage(
                imageUrl = imageUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PagedReader(
    pages: List<String>,
    onTap: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .clickable { onTap() }
    ) { pageIndex ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ZoomableImage(
                imageUrl = pages[pageIndex],
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 3.5f)
                    if (scale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Manga Sayfası",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun ReaderTopBar(
    title: String,
    readingMode: ReadingMode,
    onBackClick: () -> Unit,
    onToggleMode: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.75f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Text("←", color = Color.White, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = onToggleMode,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (readingMode == ReadingMode.WEBTOON) "Webtoon" else "Sayfalı",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
