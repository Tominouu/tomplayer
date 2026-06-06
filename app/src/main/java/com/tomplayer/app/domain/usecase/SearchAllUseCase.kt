package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.data.repository.MediaRepository

data class SearchResults(
    val channels: List<Channel> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val series: List<Series> = emptyList()
)

class SearchAllUseCase(private val repository: MediaRepository) {

    operator fun invoke(query: String): SearchResults {
        if (query.isBlank()) return SearchResults()
        return SearchResults(
            channels = repository.searchChannels(query),
            movies = repository.searchMovies(query),
            series = repository.searchSeries(query)
        )
    }
}
