package com.mangile.app.data.api

import com.mangile.app.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object MangileApi {
    const val BASE_URL = "https://mangile-backend.onrender.com"

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }

    val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }

    suspend fun getPopularManga(limit: Int = 12): List<MangaListItem> {
        return try {
            val resp: MangaListResponse = client.get("$BASE_URL/api/mangaList") {
                parameter("filterType", "POPULAR")
                parameter("limit", limit)
            }.body()
            resp.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun searchManga(
        query: String = "",
        limit: Int = 48,
        page: Int = 1,
        sort: String = "POPULARITY_DESC",
        format: String = "ALL",
        genre: String = "ALL",
        status: String = "ALL"
    ): MangaListResponse {
        return try {
            client.get("$BASE_URL/api/mangaList") {
                parameter("limit", limit)
                parameter("page", page)
                if (query.isNotBlank()) parameter("search", query)
                parameter("sort", sort)
                if (format != "ALL") parameter("format", format)
                if (genre != "ALL") parameter("genre", genre)
                if (status != "ALL") parameter("status", status)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            MangaListResponse()
        }
    }

    suspend fun getLatestTitles(): List<LatestTitleItem> {
        return try {
            client.get("$BASE_URL/api/latestTitles").body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getLatestChapters(): List<LatestChapterItem> {
        return try {
            client.get("$BASE_URL/api/latestChapters").body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTitlesByTag(tag: String): List<TitleByTagItem> {
        return try {
            client.get("$BASE_URL/api/titlesByTag") {
                parameter("tag", tag)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTitleDetails(malId: String): TitleDetails? {
        return try {
            client.get("$BASE_URL/api/titles") {
                parameter("mal_id", malId)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getChapter(id: String): ChapterDetail? {
        return try {
            client.get("$BASE_URL/api/chapter") {
                parameter("id", id)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLocalTitles(
        page: Int = 1,
        limit: Int = 24,
        search: String = "",
        type: String = "ALL",
        tag: String = "ALL",
        status: String = "ALL",
        sort: String = "date_desc"
    ): LocalTitlesResponse {
        return try {
            client.get("$BASE_URL/api/localTitles") {
                parameter("page", page)
                parameter("limit", limit)
                if (search.isNotBlank()) parameter("search", search)
                if (type != "ALL") parameter("type", type)
                if (tag != "ALL") parameter("tag", tag)
                if (status != "ALL") parameter("status", status)
                parameter("sort", sort)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            LocalTitlesResponse()
        }
    }
}
