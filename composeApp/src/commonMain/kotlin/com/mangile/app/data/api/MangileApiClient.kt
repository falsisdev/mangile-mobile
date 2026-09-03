package com.mangile.app.data.api

import com.mangile.app.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class MangileApiClient(
    val baseUrl: String = "https://mangile.com.tr" // veya yerel testte http://10.0.2.2:2611
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    // Popüler Seriler (Hero Slider)
    suspend fun getPopularManga(limit: Int = 12): List<MangaListItem> {
        return try {
            client.get("$baseUrl/api/mangaList") {
                parameter("filterType", "POPULAR")
                parameter("limit", limit)
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Son Eklenen Başlıklar
    suspend fun getLatestTitles(): List<LatestTitleItem> {
        return try {
            client.get("$baseUrl/api/latestTitles").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Son Eklenen Bölümler
    suspend fun getLatestChapters(): List<LatestChapterItem> {
        return try {
            client.get("$baseUrl/api/latestChapters").body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Etikete Göre Başlıklar
    suspend fun getTitlesByTag(tag: String): List<LatestTitleItem> {
        return try {
            client.get("$baseUrl/api/titlesByTag") {
                parameter("tag", tag)
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Seri Detayı
    suspend fun getTitleDetails(id: String): TitleDetails? {
        return try {
            client.get("$baseUrl/api/titles") {
                parameter("id", id)
            }.body()
        } catch (e: Exception) {
            null
        }
    }

    // Bölüm Detayı (Manga Sayfaları & Novel İçeriği)
    suspend fun getChapter(id: String): ChapterDetail? {
        return try {
            client.get("$baseUrl/api/chapter") {
                parameter("id", id)
            }.body()
        } catch (e: Exception) {
            null
        }
    }
}
