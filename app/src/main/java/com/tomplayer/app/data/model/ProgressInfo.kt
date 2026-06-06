package com.tomplayer.app.data.model

data class ProgressInfo(
    val contentId: String,
    val contentType: ContentType,
    val position: Long,
    val totalDuration: Long,
    val lastPlayed: Long = System.currentTimeMillis()
) {
    val percent: Float
        get() = if (totalDuration > 0) (position.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f) else 0f
}

enum class ContentType {
    LIVE, MOVIE, EPISODE
}
