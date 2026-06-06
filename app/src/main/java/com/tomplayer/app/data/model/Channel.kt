package com.tomplayer.app.data.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String? = null,
    val streamUrl: String,
    val category: String? = null,
    val epgChannelId: String? = null,
    val isFavorite: Boolean = false,
    val group: String? = null,
    val tvgName: String? = null,
    val tvgId: String? = null,
    val userAgent: String? = null,
    val referer: String? = null,
    val isAdult: Boolean = false
)
