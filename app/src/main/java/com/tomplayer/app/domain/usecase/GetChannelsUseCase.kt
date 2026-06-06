package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.repository.MediaRepository

class GetChannelsUseCase(private val repository: MediaRepository) {

    fun getChannelsByCategory(categoryId: String?): List<Channel> =
        repository.getChannelsForCategory(categoryId)

    fun getAllCategories(): List<Category> = repository.liveCategories.value
}
