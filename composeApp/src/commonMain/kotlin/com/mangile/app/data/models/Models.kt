package com.mangile.app.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageAsset(
    val url: String? = null
)

@Serializable
data class Source(
    val _id: String? = null,
    val _type: String? = null,
    val name: String? = null,
    val website: String? = null,
    val coverImage: ImageAsset? = null,
    val logo: ImageAsset? = null
)

// Ana sayfada /api/mangaList yanıtı
@Serializable
data class MangaListItem(
    val mal_id: Int? = null,
    val mal_year: Int? = null,
    val mal_type: String? = null,
    val anilist_title: String? = null,
    val anilist_type: String? = null,
    val anilist_score: Double? = null,
    val anilist_description: String? = null,
    val anilist_banner_image: String? = null,
    val sanity_description: String? = null,
    val has_local_content: Boolean = false
)

// Son Eklenen Başlıklar /api/latestTitles
@Serializable
data class LatestTitleItem(
    val _id: String? = null,
    val _createdAt: String? = null,
    val title: String? = null,
    val myAnimeListId: Int? = null,
    val coverImage: ImageAsset? = null
)

// Son Eklenen Bölümler /api/latestChapters
@Serializable
data class LatestChapterItem(
    val _id: String,
    val _createdAt: String? = null,
    val title: String? = null,
    val volumeNumber: Double? = null,
    val chapterNumber: Double? = null,
    val lightNovel: ChapterTitleParent? = null,
    val manga: ChapterTitleParent? = null
)

@Serializable
data class ChapterTitleParent(
    val title: String? = null,
    val myAnimeListId: Int? = null,
    val coverImage: ImageAsset? = null
)

// /api/chapter Yanıtı
@Serializable
data class ChapterDetail(
    val _id: String,
    val title: String? = null,
    val chapterNumber: Double? = null,
    val volumeNumber: Double? = null,
    val pages: List<ImageAsset> = emptyList(),
    val manga: ChapterTitleRef? = null,
    val lightNovel: ChapterTitleRef? = null,
    val chapters: List<ChapterListItem> = emptyList(),
    val source: Source? = null
)

@Serializable
data class ChapterTitleRef(
    val _id: String? = null,
    val _type: String? = null,
    val myAnimeListId: Int? = null,
    val title: String? = null,
    val tags: List<String> = emptyList(),
    val format: String? = null
)

@Serializable
data class ChapterListItem(
    val _id: String,
    val title: String? = null,
    val chapterNumber: Double? = null,
    val volumeNumber: Double? = null,
    val source: Source? = null
)

// /api/titles (Detay)
@Serializable
data class TitleDetails(
    val _id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val myAnimeListId: Int? = null,
    val tags: List<String> = emptyList(),
    val format: String? = null,
    val uploadStatus: String? = null,
    val bannerImage: ImageAsset? = null,
    val coverImage: ImageAsset? = null,
    val chapters: List<ChapterSummary> = emptyList(),
    val externalMal: JikanInfo? = null,
    val externalAnilist: AniListInfo? = null
)

@Serializable
data class ChapterSummary(
    val _id: String,
    val title: String? = null,
    val chapterNumber: Double? = null,
    val volumeNumber: Double? = null,
    val source: Source? = null
)

@Serializable
data class JikanInfo(
    val url: String? = null,
    val type: String? = null,
    val score: Double? = null,
    val status: String? = null
)

@Serializable
data class AniListInfo(
    val id: Int? = null,
    val averageScore: Double? = null,
    val bannerImage: String? = null,
    val description: String? = null
)
