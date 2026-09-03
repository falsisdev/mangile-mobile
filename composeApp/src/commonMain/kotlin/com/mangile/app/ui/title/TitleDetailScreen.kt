package com.mangile.app.ui.title

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.mangile.app.data.models.ChapterSummary
import com.mangile.app.data.models.TitleDetails

@Composable
fun TitleDetailScreen(
    titleId: String,
    onBackClick: () -> Unit,
    onChapterClick: (String, Boolean) -> Unit
) {
    var details by remember { mutableStateOf<TitleDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var descExpanded by remember { mutableStateOf(false) }
    var sortAsc by remember { mutableStateOf(true) }

    LaunchedEffect(titleId) {
        isLoading = true
        details = MangileApi.getTitleDetails(titleId)
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val d = details
    if (d == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Seri bilgisi bulunamadı.", color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onBackClick) { Text("Geri Dön") }
            }
        }
        return
    }

    val isNovel = d._type == "lightNovel"
    val chapters = d.chapters.let { list ->
        if (sortAsc) list.sortedBy { it.volumeNumber ?: 0.0 }
        else list.sortedByDescending { it.volumeNumber ?: 0.0 }
    }
    val score = d.externalAnilist?.averageScore
    val malScore = d.externalMal?.score
    val bannerUrl = d.bannerImage?.url ?: d.externalAnilist?.bannerImage ?: d.coverImage?.url

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Header with banner
        item {
            Box(Modifier.fillMaxWidth().height(280.dp)) {
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = d.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                ))
                // Back button
                Surface(
                    onClick = onBackClick,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = CircleShape,
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart).statusBarsPadding()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.onSurface)
                }
                // Cover + Title
                Row(
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        Modifier.width(100.dp).height(145.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = d.coverImage?.url
                                ?: d.externalAnilist?.coverImage?.extraLarge,
                            contentDescription = d.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = d.title ?: "Bilinmeyen Seri",
                            color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 3, overflow = TextOverflow.Ellipsis,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            score?.let {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = "Puan", tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "${"%.1f".format(it / 10.0)}",
                                            fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            malScore?.let {
                                Surface(
                                    color = Color(0xFF2E51A2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "MAL $it",
                                        fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tags
        if (d.tags.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    d.tags.take(5).forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                tag, fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Read CTAs
        if (chapters.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val first = chapters.first()
                    val last = chapters.last()
                    Button(
                        onClick = { onChapterClick(first._id, isNovel) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Baştan Oku", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onChapterClick(last._id, isNovel) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Son Bölüm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Description with animation
        d.description?.let { desc ->
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                ) {
                    Text(
                        text = desc,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp,
                        maxLines = if (descExpanded) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (desc.length > 200) {
                        Text(
                            text = if (descExpanded) "Daralt" else "Devamını Oku",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { descExpanded = !descExpanded }
                        )
                    }
                }
            }
        }

        // Chapter list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Bölümler (${chapters.size})",
                    fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { sortAsc = !sortAsc }) {
                    Text(
                        if (sortAsc) "Eski → Yeni" else "Yeni → Eski",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Chapter rows
        items(chapters) { ch ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onChapterClick(ch._id, isNovel) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val label = buildString {
                            ch.volumeNumber?.let { append("C${it.toInt()} ") }
                            ch.chapterNumber?.let { append("B${it.toInt()}") }
                        }
                        Text(
                            label.ifBlank { "Bölüm" },
                            fontWeight = FontWeight.Bold, fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ch.title?.let {
                            Text(
                                it, fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        ch.source?.name?.let {
                            Text(
                                it, fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Text(
                        "Oku",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp
                    )
                }
            }
        }
    }
}
