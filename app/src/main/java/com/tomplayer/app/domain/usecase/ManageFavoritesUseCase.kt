package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.data.repository.MediaRepository

class ManageFavoritesUseCase(private val repository: MediaRepository) {

    fun toggleChannelFavorite(channelId: String) = repository.toggleChannelFavorite(channelId)
    fun toggleMovieFavorite(movieId: String) = repository.toggleMovieFavorite(movieId)
    fun toggleSeriesFavorite(seriesId: String) = repository.toggleSeriesFavorite(seriesId)

    fun getFavoriteChannels(): List<Channel> = repository.getFavoriteChannels()
    fun getFavoriteMovies(): List<Movie> = repository.getFavoriteMovies()
    fun getFavoriteSeries(): List<Series> = repository.getFavoriteSeries()
}
