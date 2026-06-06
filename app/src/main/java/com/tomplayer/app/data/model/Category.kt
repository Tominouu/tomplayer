package com.tomplayer.app.data.model

data class Category(
    val id: String,
    val name: String,
    val channelCount: Int = 0,
    val iconUrl: String? = null
)
