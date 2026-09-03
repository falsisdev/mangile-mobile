package com.mangile.app.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangile.app.data.api.MangileApi
import com.mangile.app.data.models.ChapterDetail

enum class NovelFont(val family: FontFamily, val label: String) {
    SANS(FontFamily.Default, "Düz"),
    SERIF(FontFamily.Serif, "Serif"),
    MONO(FontFamily.Monospace, "Mono")
}

@Composable
fun NovelReaderScreen(
    chapterId: String,
    onBackClick: () -> Unit,
    onNavigateChapter: (String) -> Unit
) {
    var chapter by remember { mutableStateOf<ChapterDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var fontSize by remember { mutableStateOf(17) }
    var lineH by remember { mutableStateOf(1.8f) }
    var font by remember { mutableStateOf(NovelFont.SANS) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(chapterId) {
        isLoading = true
        chapter = MangileApi.getChapter(chapterId)
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val ch = chapter ?: return
    val siblings = ch.chapters.sortedBy { (it.volumeNumber ?: 0.0) * 10000 + (it.chapterNumber ?: 0.0) }
    val curIdx = siblings.indexOfFirst { it._id == chapterId }
    val prevId = if (curIdx > 0) siblings[curIdx - 1]._id else null
    val nextId = if (curIdx in 0 until siblings.size - 1) siblings[curIdx + 1]._id else null

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            ch.lightNovel?.title ?: "",
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            ch.title ?: "Bölüm",
                            fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Font size buttons
                    IconButton(onClick = { if (fontSize > 12) fontSize -= 2 }) {
                        Text("A-", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { if (fontSize < 32) fontSize += 2 }) {
                        Text("A+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showSettings = !showSettings }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ayarlar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)
        ) {
            val scrollState = rememberScrollState()

            Column(
                Modifier.fillMaxSize().verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Chapter title
                Text(
                    ch.title ?: "",
                    fontSize = (fontSize + 8).sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = font.family,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Sanity blocks
                val blocks = ch.content
                if (blocks.isEmpty()) {
                    Text(
                        "Bu bölüm için metin içeriği bulunamadı.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp
                    )
                } else {
                    blocks.forEach { block ->
                        if (block._type == "block") {
                            val text = block.children.joinToString("") { it.text ?: "" }
                            if (text.isBlank()) return@forEach
                            val style = block.style ?: "normal"
                            val isH = style.startsWith("h")
                            val isBQ = style == "blockquote"

                            if (isBQ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(0.3f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                ) {
                                    Row(Modifier.padding(16.dp)) {
                                        Box(
                                            Modifier.width(3.dp).height(40.dp)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text, fontSize = fontSize.sp, lineHeight = (fontSize * lineH).sp,
                                            fontFamily = font.family, fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = text,
                                    fontSize = if (isH) (fontSize + 4).sp else fontSize.sp,
                                    fontWeight = if (isH) FontWeight.Bold else FontWeight.Normal,
                                    lineHeight = (fontSize * lineH).sp,
                                    fontFamily = font.family,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(bottom = if (isH) 20.dp else 14.dp)
                                )
                            }
                        }
                    }
                }

                // End of chapter nav
                Spacer(Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(20.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { prevId?.let { onNavigateChapter(it) } },
                        enabled = prevId != null
                    ) { Text("← Önceki Bölüm") }
                    Button(
                        onClick = { nextId?.let { onNavigateChapter(it) } },
                        enabled = nextId != null,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Sonraki Bölüm →", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.height(60.dp))
            }

            // Settings panel
            if (showSettings) {
                Card(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Okuma Ayarları", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            IconButton(onClick = { showSettings = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Yazı Boyutu: ${fontSize}sp", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = fontSize.toFloat(), onValueChange = { fontSize = it.toInt() },
                            valueRange = 12f..32f, steps = 9,
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Satır Yüksekliği: ${"%.1f".format(lineH)}x", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = lineH, onValueChange = { lineH = it },
                            valueRange = 1.2f..2.5f,
                            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Yazı Tipi", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NovelFont.entries.forEach { f ->
                                FilterChip(
                                    selected = font == f,
                                    onClick = { font = f },
                                    label = { Text(f.label) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
