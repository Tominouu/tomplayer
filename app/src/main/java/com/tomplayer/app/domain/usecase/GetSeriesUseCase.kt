package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.data.repository.MediaRepository

class GetSeriesUseCase(private val repository: MediaRepository) {

    fun getSeries(categoryId: String? = null): List<Series> {
        return repository.getSeriesForCategory(categoryId)
    }

    suspend fun getSeriesInfo(seriesId: String) = repository.loadSeriesInfo(seriesId)

    fun getCategories(): List<Category> = repository.seriesCategories.value

    fun getFavorites(): List<Series> = repository.getFavoriteSeries()
}
