package com.mangile.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageAsset(
    val url: String? = null
)

@Serializable
data class Source(
    val _id: String? = null,
    val name: String? = null,
    val website: String? = null,
    val coverImage: ImageAsset? = null,
    val logo: ImageAsset? = null
)

// /api/mangaList envelope
@Serializable
data class MangaListResponse(
    val code: Int? = null,
    val data: List<MangaListItem> = emptyList(),
    val limit: Int? = null,
    val page: Int? = null
)

@Serializable
data class MangaListItem(
    val mal_id: Int? = null,
    val anilist_id: Int? = null,
    val mal_year: Int? = null,
    val mal_type: String? = null,
    val anilist_title: String? = null,
    val title_romaji: String? = null,
    val title_english: String? = null,
    val title_native: String? = null,
    val anilist_type: String? = null,
    val anilist_format: String? = null,
    val anilist_status: String? = null,
    val anilist_score: Double? = null,
    val anilist_description: String? = null,
    val anilist_banner_image: String? = null,
    val anilist_cover_image: String? = null,
    val sanity_description: String? = null,
    val has_local_content: Boolean = false
)

// /api/latestTitles - coverImage is a plain string URL
@Serializable
data class LatestTitleItem(
    val _id: String? = null,
    val _type: String? = null,
    val _createdAt: String? = null,
    val _updatedAt: String? = null,
    val myAnimeListId: Int? = null,
    val title: String? = null,
    val tags: List<String> = emptyList(),
    val coverImage: String? = null,
    val bannerImage: String? = null
)

// /api/latestChapters
@Serializable
data class LatestChapterItem(
    val _id: String,
    val _type: String? = null,
    val _createdAt: String? = null,
    val title: String? = null,
    val volumeNumber: Double? = null,
    val chapterNumber: Double? = null,
    val lightNovel: ChapterTitleParent? = null,
    val manga: ChapterTitleParent? = null,
    val source: Source? = null
)

@Serializable
data class ChapterTitleParent(
    val _id: String? = null,
    val title: String? = null,
    val myAnimeListId: Int? = null,
    val coverImage: ImageAsset? = null
)

// /api/titlesByTag
@Serializable
data class TitleByTagItem(
    val _id: String? = null,
    val _type: String? = null,
    val _createdAt: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val myAnimeListId: Int? = null,
    val tags: List<String> = emptyList(),
    val coverImage: ImageAsset? = null
)

// Sanity Block (Novel content)
@Serializable
data class SanityChild(
    val _key: String? = null,
    val _type: String? = null,
    val text: String? = null,
    val marks: List<String> = emptyList()
)

@Serializable
data class SanityBlock(
    val _key: String? = null,
    val _type: String? = null,
    val style: String? = null,
    val children: List<SanityChild> = emptyList()
)

// /api/chapter
@Serializable
data class ChapterDetail(
    val _id: String,
    val _type: String? = null,
    val title: String? = null,
    val chapterNumber: Double? = null,
    val volumeNumber: Double? = null,
    val pages: List<ImageAsset> = emptyList(),
    val content: List<SanityBlock> = emptyList(),
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

// /api/titles
@Serializable
data class TitleDetails(
    val _id: String? = null,
    val _type: String? = null,
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
    val status: String? = null,
    val title_japanese: String? = null,
    val title_english: String? = null
)

@Serializable
data class AniListTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null
)

@Serializable
data class AniListCoverImage(
    val extraLarge: String? = null,
    val large: String? = null,
    val medium: String? = null
)

@Serializable
data class AniListInfo(
    val id: Int? = null,
    val countryOfOrigin: String? = null,
    val format: String? = null,
    val title: AniListTitle? = null,
    val averageScore: Double? = null,
    val bannerImage: String? = null,
    val coverImage: AniListCoverImage? = null,
    val description: String? = null
)

// /api/localTitles
@Serializable
data class LocalTitlesResponse(
    val data: List<LocalTitleItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 24
)

@Serializable
data class LocalTitleItem(
    val _id: String? = null,
    val _type: String? = null,
    val title: String? = null,
    val slug: String? = null,
    val myAnimeListId: Int? = null,
    val description: String? = null,
    val uploadStatus: String? = null,
    val tags: List<String> = emptyList(),
    val coverImage: String? = null,
    val bannerImage: String? = null
)
