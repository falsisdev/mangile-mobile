package com.mangile.app.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangile.app.data.api.MangileApiClient
import com.mangile.app.data.models.ChapterDetail

enum class NovelFont(val family: FontFamily, val label: String) {
    SANS(FontFamily.Default, "Düz"),
    SERIF(FontFamily.Serif, "Serif"),
    MONO(FontFamily.Monospace, "Mono")
}

@Composable
fun NovelReaderScreen(
    chapterId: String,
    apiClient: MangileApiClient,
    onBackClick: () -> Unit
) {
    var chapterDetail by remember { mutableStateOf<ChapterDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Novel Okuma Ayarları
    var fontSize by remember { mutableStateOf(18) }
    var lineHeightMultiplier by remember { mutableStateOf(1.8f) }
    var selectedFont by remember { mutableStateOf(NovelFont.SANS) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(chapterId) {
        isLoading = true
        chapterDetail = apiClient.getChapter(chapterId)
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
        val detail = chapterDetail
        Scaffold(
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackClick) {
                                Text("←", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = detail?.title ?: "Roman Bölümü",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        }

                        IconButton(onClick = { showSettingsSheet = !showSettingsSheet }) {
                            Text("Aa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = detail?.title ?: "",
                        fontSize = (fontSize + 6).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = selectedFont.family,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Metin Gövdesi
                    Text(
                        text = "Bu novel bölümü içeriği Mangile backend ve Sanity üzerinden dinamik olarak render edilmektedir. Okuma deneyiminizi yukarıdaki 'Aa' menüsünden özelleştirebilirsiniz.",
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * lineHeightMultiplier).sp,
                        fontFamily = selectedFont.family,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Alt Ayar Paneli
                if (showSettingsSheet) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Okuma Ayarları", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Yazı Boyutu Kontrolü
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Boyut: ${fontSize}sp")
                                Row {
                                    Button(
                                        onClick = { if (fontSize > 12) fontSize -= 2 },
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Text("-")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { if (fontSize < 32) fontSize += 2 },
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Text("+")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Yazı Tipi
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NovelFont.entries.forEach { font ->
                                    FilterChip(
                                        selected = selectedFont == font,
                                        onClick = { selectedFont = font },
                                        label = { Text(font.label) }
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
