package com.tomplayer.app.data.model

data class EpgProgram(
    val channelId: String,
    val title: String,
    val description: String? = null,
    val startTime: Long,
    val endTime: Long,
    val isNow: Boolean = false,
    val category: String? = null,
    val iconUrl: String? = null
) {
    val duration: Long get() = endTime - startTime

    val progress: Float
        get() {
            if (!isNow) return 0f
            val now = System.currentTimeMillis()
            val total = endTime - startTime
            val elapsed = now - startTime
            return (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        }
}
