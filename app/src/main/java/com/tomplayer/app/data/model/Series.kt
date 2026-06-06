package com.tomplayer.app.data.model

data class Series(
    val id: String,
    val name: String,
    val coverUrl: String? = null,
    val backdropUrl: String? = null,
    val category: String? = null,
    val categoryId: String? = null,
    val year: String? = null,
    val rating: String? = null,
    val description: String? = null,
    val genre: String? = null,
    val isFavorite: Boolean = false,
    val seasons: List<Season> = emptyList()
)

data class Season(
    val id: String,
    val number: Int,
    val name: String,
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val id: String,
    val name: String,
    val streamUrl: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val coverUrl: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val progress: Long = 0L,
    val totalDuration: Long = 0L
) {
    val progressPercent: Float
        get() = if (totalDuration > 0) (progress.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f) else 0f
}
