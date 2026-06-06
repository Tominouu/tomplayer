package com.tomplayer.app.data.model

sealed class PlaylistSource {
    data class M3uUrl(val url: String, val name: String? = null) : PlaylistSource()
    data class M3uFile(val filePath: String, val name: String? = null) : PlaylistSource()
    data class Xtream(
        val serverUrl: String,
        val username: String,
        val password: String,
        val name: String? = null
    ) : PlaylistSource()
}

data class Playlist(
    val id: String,
    val name: String,
    val source: PlaylistSource,
    val channelCount: Int = 0,
    val isActive: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
