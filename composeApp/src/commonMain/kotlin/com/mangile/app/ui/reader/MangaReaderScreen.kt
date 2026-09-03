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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mangile.app.data.api.MangileApi
import com.mangile.app.data.models.ChapterDetail
import com.mangile.app.data.models.ChapterListItem
import kotlinx.coroutines.launch

enum class ReadingMode(val label: String) {
    WEBTOON("Webtoon"), PAGED("Sayfalı")
}

@Composable
fun MangaReaderScreen(
    chapterId: String,
    onBackClick: () -> Unit,
    onNavigateChapter: (String) -> Unit
) {
    var chapter by remember { mutableStateOf<ChapterDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var mode by remember { mutableStateOf(ReadingMode.WEBTOON) }
    var showControls by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(chapterId) {
        isLoading = true
        chapter = MangileApi.getChapter(chapterId)
        // Auto-detect webtoon format
        val tags = chapter?.manga?.tags ?: emptyList()
        val fmt = chapter?.manga?.format ?: ""
        if (tags.any { it.lowercase() in listOf("long strip", "webtoon") } || fmt.lowercase().contains("manhwa")) {
            mode = ReadingMode.WEBTOON
        }
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val ch = chapter
    if (ch == null || ch.pages.isEmpty()) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sayfa bulunamadı.", color = Color.White)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onBackClick) { Text("Geri Dön") }
            }
        }
        return
    }

    val pages = ch.pages.mapNotNull { it.url }
    val siblings = ch.chapters.sortedBy { (it.volumeNumber ?: 0.0) * 10000 + (it.chapterNumber ?: 0.0) }
    val currentIdx = siblings.indexOfFirst { it._id == chapterId }
    val prevId = if (currentIdx > 0) siblings[currentIdx - 1]._id else null
    val nextId = if (currentIdx >= 0 && currentIdx < siblings.size - 1) siblings[currentIdx + 1]._id else null

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        when (mode) {
            ReadingMode.WEBTOON -> {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().clickable { showControls = !showControls }
                ) {
                    itemsIndexed(pages) { _, url ->
                        ZoomableImage(url)
                    }
                    // End of chapter navigation
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Bölüm Sonu", color = Color.White,
                                fontSize = 18.sp, fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                prevId?.let { id ->
                                    OutlinedButton(onClick = { onNavigateChapter(id) }) {
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
                                        Spacer(Modifier.width(4.dp))
                                        Text("Önceki", color = Color.White)
                                    }
                                }
                                nextId?.let { id ->
                                    Button(
                                        onClick = { onNavigateChapter(id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Sonraki", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ReadingMode.PAGED -> {
                val pagerState = rememberPagerState(pageCount = { pages.size })
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize().clickable { showControls = !showControls }
                ) { pageIdx ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ZoomableImage(pages[pageIdx])
                    }
                }
                // Page indicator
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                    color = Color.Black.copy(0.7f), shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "${pagerState.currentPage + 1} / ${pages.size}",
                        color = Color.White, fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Top overlay
        AnimatedVisibility(
            visible = showControls, enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(color = Color.Black.copy(0.8f), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp).statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            ch.manga?.title ?: "", color = Color.White.copy(0.7f),
                            fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "C${ch.volumeNumber?.toInt() ?: 0} B${ch.chapterNumber?.toInt() ?: 0}: ${ch.title ?: ""}",
                            color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Mode toggle
                    Surface(
                        onClick = { mode = if (mode == ReadingMode.WEBTOON) ReadingMode.PAGED else ReadingMode.WEBTOON },
                        color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            mode.label, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom navigation overlay
        AnimatedVisibility(
            visible = showControls, enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = Color.Black.copy(0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp).navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { prevId?.let { onNavigateChapter(it) } },
                        enabled = prevId != null
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = if (prevId != null) Color.White else Color.Gray)
                        Text("Önceki", color = if (prevId != null) Color.White else Color.Gray)
                    }
                    Text(
                        "B${ch.chapterNumber?.toInt() ?: "-"} / ${siblings.size}",
                        color = Color.White, fontSize = 13.sp
                    )
                    TextButton(
                        onClick = { nextId?.let { onNavigateChapter(it) } },
                        enabled = nextId != null
                    ) {
                        Text("Sonraki", color = if (nextId != null) MaterialTheme.colorScheme.primary else Color.Gray, fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = if (nextId != null) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            }
        }

        // Progress bar
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.Transparent
        )
    }
}

@Composable
fun ZoomableImage(imageUrl: String, modifier: Modifier = Modifier) {
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
                scaleX = scale, scaleY = scale,
                translationX = offsetX, translationY = offsetY
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
