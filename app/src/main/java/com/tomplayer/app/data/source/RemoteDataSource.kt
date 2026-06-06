package com.tomplayer.app.data.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RemoteDataSource {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun fetchContent(url: String, userAgent: String? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBuilder = Request.Builder()
                    .url(url)
                    .addHeader("Accept", "*/*")
                if (userAgent != null) requestBuilder.addHeader("User-Agent", userAgent)
                val response = client.newCall(requestBuilder.build()).execute()
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "")
                } else {
                    Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchXtreamApi(
        baseUrl: String,
        username: String,
        password: String,
        action: String
    ): Result<String> {
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=$action"
        return fetchContent(url)
    }

    suspend fun fetchXtreamSeriesInfo(
        baseUrl: String,
        username: String,
        password: String,
        seriesId: String
    ): Result<String> {
        val url = "$baseUrl/player_api.php?username=$username&password=$password&action=get_series_info&series_id=$seriesId"
        return fetchContent(url)
    }
}
