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
    val baseUrl: String = "https://mangile-backend.onrender.com"
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
            level = LogLevel.ALL
        }
    }

    // Popüler Seriler (Hero Slider)
    suspend fun getPopularManga(limit: Int = 12): List<MangaListItem> {
        return try {
            val response: MangaListResponse = client.get("$baseUrl/api/mangaList") {
                parameter("filterType", "POPULAR")
                parameter("limit", limit)
            }.body()
            response.data
        } catch (e: Exception) {
            println("[HATA] getPopularManga: ${e.message}")
            emptyList()
        }
    }

    // Son Eklenen Başlıklar
    suspend fun getLatestTitles(): List<LatestTitleItem> {
        return try {
            client.get("$baseUrl/api/latestTitles").body()
        } catch (e: Exception) {
            println("[HATA] getLatestTitles: ${e.message}")
            emptyList()
        }
    }

    // Son Eklenen Bölümler
    suspend fun getLatestChapters(): List<LatestChapterItem> {
        return try {
            client.get("$baseUrl/api/latestChapters").body()
        } catch (e: Exception) {
            println("[HATA] getLatestChapters: ${e.message}")
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
            println("[HATA] getTitlesByTag: ${e.message}")
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
            println("[HATA] getTitleDetails: ${e.message}")
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
            println("[HATA] getChapter: ${e.message}")
            null
        }
    }
}
