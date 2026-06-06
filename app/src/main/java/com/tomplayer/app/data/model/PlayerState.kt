package com.tomplayer.app.data.model

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentChannel: Channel? = null,
    val currentProgram: EpgProgram? = null,
    val buffering: Boolean = true,
    val error: String? = null,
    val volume: Float = 1f,
    val isMuted: Boolean = false
)
