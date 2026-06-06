package com.tomplayer.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.ContentType
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.ProgressInfo
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.data.repository.MediaRepository
import com.tomplayer.app.domain.usecase.GetMoviesUseCase
import com.tomplayer.app.domain.usecase.GetSeriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ContentTab { LIVE, MOVIES, SERIES }

class HomeViewModel(
    private val repository: MediaRepository,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getSeriesUseCase: GetSeriesUseCase
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(ContentTab.LIVE)
    val selectedTab: StateFlow<ContentTab> = _selectedTab.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _series = MutableStateFlow<List<Series>>(emptyList())
    val series: StateFlow<List<Series>> = _series.asStateFlow()

    private val _liveCategories = MutableStateFlow<List<Category>>(emptyList())
    val liveCategories: StateFlow<List<Category>> = _liveCategories.asStateFlow()

    private val _movieCategories = MutableStateFlow<List<Category>>(emptyList())
    val movieCategories: StateFlow<List<Category>> = _movieCategories.asStateFlow()

    private val _seriesCategories = MutableStateFlow<List<Category>>(emptyList())
    val seriesCategories: StateFlow<List<Category>> = _seriesCategories.asStateFlow()

    private val _favoriteChannels = MutableStateFlow<List<Channel>>(emptyList())
    val favoriteChannels: StateFlow<List<Channel>> = _favoriteChannels.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies.asStateFlow()

    private val _favoriteSeries = MutableStateFlow<List<Series>>(emptyList())
    val favoriteSeries: StateFlow<List<Series>> = _favoriteSeries.asStateFlow()

    private val _featuredMovies = MutableStateFlow<List<Movie>>(emptyList())
    val featuredMovies: StateFlow<List<Movie>> = _featuredMovies.asStateFlow()

    private val _resumeContent = MutableStateFlow<List<ProgressInfo>>(emptyList())
    val resumeContent: StateFlow<List<ProgressInfo>> = _resumeContent.asStateFlow()

    private val _hasContent = MutableStateFlow(false)
    val hasContent: StateFlow<Boolean> = _hasContent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var _selectedLiveCategory = MutableStateFlow<String?>(null)
    val selectedLiveCategory: StateFlow<String?> = _selectedLiveCategory.asStateFlow()

    private var _selectedMovieCategory = MutableStateFlow<String?>(null)
    val selectedMovieCategory: StateFlow<String?> = _selectedMovieCategory.asStateFlow()

    private var _selectedSeriesCategory = MutableStateFlow<String?>(null)
    val selectedSeriesCategory: StateFlow<String?> = _selectedSeriesCategory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.channels.collect {
                _channels.value = it
                _favoriteChannels.value = repository.getFavoriteChannels()
                _hasContent.value = it.isNotEmpty() || _movies.value.isNotEmpty()
            }
        }
        viewModelScope.launch {
            repository.movies.collect {
                _movies.value = it
                _featuredMovies.value = it.shuffled().take(10)
                _favoriteMovies.value = repository.getFavoriteMovies()
            }
        }
        viewModelScope.launch {
            repository.series.collect {
                _series.value = it
                _favoriteSeries.value = repository.getFavoriteSeries()
            }
        }
        viewModelScope.launch { repository.liveCategories.collect { _liveCategories.value = it } }
        viewModelScope.launch { repository.movieCategories.collect { _movieCategories.value = it } }
        viewModelScope.launch { repository.seriesCategories.collect { _seriesCategories.value = it } }
        refresh()
    }

    fun selectTab(tab: ContentTab) { _selectedTab.value = tab }

    fun selectLiveCategory(categoryId: String?) {
        _selectedLiveCategory.value = categoryId
        _channels.value = repository.getChannelsForCategory(categoryId)
    }

    fun selectMovieCategory(categoryId: String?) {
        _selectedMovieCategory.value = categoryId
        _movies.value = repository.getMoviesForCategory(categoryId)
    }

    fun selectSeriesCategory(categoryId: String?) {
        _selectedSeriesCategory.value = categoryId
        _series.value = repository.getSeriesForCategory(categoryId)
    }

    fun toggleChannelFavorite(channelId: String) {
        repository.toggleChannelFavorite(channelId)
        _favoriteChannels.value = repository.getFavoriteChannels()
    }

    fun toggleMovieFavorite(movieId: String) {
        repository.toggleMovieFavorite(movieId)
        _favoriteMovies.value = repository.getFavoriteMovies()
    }

    fun toggleSeriesFavorite(seriesId: String) {
        repository.toggleSeriesFavorite(seriesId)
        _favoriteSeries.value = repository.getFavoriteSeries()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _resumeContent.value = repository.getResumeProgress()
            } finally { _isLoading.value = false }
        }
    }
}
