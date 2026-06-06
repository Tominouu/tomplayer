package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.EpgProgram
import com.tomplayer.app.data.repository.MediaRepository

class GetEpgUseCase(private val repository: MediaRepository) {

    suspend fun refreshEpg() = repository.fetchEpg()

    fun getProgramsForChannel(channelId: String): List<EpgProgram> =
        repository.getEpgForChannel(channelId)

    fun getCurrentProgram(channelId: String): EpgProgram? =
        repository.getCurrentProgram(channelId)
}
