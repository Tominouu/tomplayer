package com.tomplayer.app.data.model

data class Movie(
    val id: String,
    val name: String,
    val streamUrl: String,
    val coverUrl: String? = null,
    val backdropUrl: String? = null,
    val category: String? = null,
    val categoryId: String? = null,
    val year: String? = null,
    val rating: String? = null,
    val duration: String? = null,
    val description: String? = null,
    val director: String? = null,
    val cast: String? = null,
    val genre: String? = null,
    val isFavorite: Boolean = false,
    val progress: Long = 0L,
    val totalDuration: Long = 0L
) {
    val progressPercent: Float
        get() = if (totalDuration > 0) (progress.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f) else 0f
}
