package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.repository.MediaRepository

class GetMoviesUseCase(private val repository: MediaRepository) {

    fun getMovies(categoryId: String? = null): List<Movie> {
        return repository.getMoviesForCategory(categoryId)
    }

    fun getCategories(): List<Category> = repository.movieCategories.value

    fun getFeatured(): List<Movie> = repository.movies.value.shuffled().take(10)

    fun getLatest(): List<Movie> = repository.movies.value.take(20)

    fun getFavorites(): List<Movie> = repository.getFavoriteMovies()
}
