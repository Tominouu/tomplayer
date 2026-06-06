package com.tomplayer.app.data.repository

import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.ContentType
import com.tomplayer.app.data.model.EpgProgram
import com.tomplayer.app.data.model.Episode
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.Playlist
import com.tomplayer.app.data.model.PlaylistSource
import com.tomplayer.app.data.model.ProgressInfo
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.data.parser.EpgParser
import com.tomplayer.app.data.parser.M3uParser
import com.tomplayer.app.data.parser.XtreamParser
import com.tomplayer.app.data.source.LocalDataSource
import com.tomplayer.app.data.source.RemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
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

    private val _epgPrograms = MutableStateFlow<Map<String, List<EpgProgram>>>(emptyMap())
    val epgPrograms: StateFlow<Map<String, List<EpgProgram>>> = _epgPrograms.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasContent = MutableStateFlow(false)
    val hasContent: StateFlow<Boolean> = _hasContent.asStateFlow()

    private var currentBaseUrl = ""
    private var currentUsername = ""
    private var currentPassword = ""

    init {
        loadPlaylistsFromStorage()
    }

    private fun loadPlaylistsFromStorage() {
        _playlists.value = localDataSource.loadPlaylists()
        _hasContent.value = _playlists.value.isNotEmpty()
    }

    suspend fun addPlaylist(source: PlaylistSource, onProgress: (String) -> Unit = {}) {
        when (source) {
            is PlaylistSource.M3uUrl -> {
                onProgress("Téléchargement de la playlist...")
                val result = remoteDataSource.fetchContent(source.url)
                val content = result.getOrThrow()
                onProgress("Analyse de la playlist...")
                val parsed = M3uParser.parse(content, source.url)
                _channels.value = parsed
                updateLiveCategories(parsed)
                val playlist = Playlist(
                    id = source.url.hashCode().toString(),
                    name = source.name ?: source.url.substringAfterLast("/").substringBeforeLast("."),
                    source = source, channelCount = parsed.size
                )
                saveAndActivatePlaylist(playlist)
            }
            is PlaylistSource.M3uFile -> {
                onProgress("Lecture du fichier...")
                val file = java.io.File(source.filePath)
                val parsed = M3uParser.parse(file.readText())
                _channels.value = parsed
                updateLiveCategories(parsed)
                val playlist = Playlist(
                    id = source.filePath.hashCode().toString(),
                    name = source.name ?: file.nameWithoutExtension,
                    source = source, channelCount = parsed.size
                )
                saveAndActivatePlaylist(playlist)
            }
            is PlaylistSource.Xtream -> {
                currentBaseUrl = source.serverUrl.trimEnd('/')
                currentUsername = source.username
                currentPassword = source.password
                onProgress("Connexion au serveur...")

                val liveCat = remoteDataSource.fetchXtreamApi(currentBaseUrl, currentUsername, currentPassword, "get_live_categories")
                val movieCat = remoteDataSource.fetchXtreamApi(currentBaseUrl, currentUsername, currentPassword, "get_vod_categories")
                val seriesCat = remoteDataSource.fetchXtreamApi(currentBaseUrl, currentUsername, currentPassword, "get_series_categories")

                onProgress("Récupération des chaînes live...")
                val liveJson = remoteDataSource.fetchXtreamApi(currentBaseUrl, currentUsername, currentPassword, "get_live_streams")
                liveJson.onSuccess { _channels.value = XtreamParser.parseLiveStreams(it, currentBaseUrl, currentUsername, currentPassword) }

                onProgress("Récupération des films...")
                val moviesJson = remoteDataSource.fetchXtreamApi(currentBaseUrl, currentUsername, currentPassword, "get_vod_streams")
                moviesJson.onSuccess {
                    _movies.value = XtreamParser.parseMovies(it, currentBaseUrl, currentUsername, currentPassword)
                    applyMovieFavorites()
                }

                onProgress("Récupération des séries...")
                val seriesJson = remoteDataSource.fetchXtreamApi(currentBaseUrl, currentUsername, currentPassword, "get_series")
                seriesJson.onSuccess {
                    val parsed = XtreamParser.parseSeriesList(it)
                    XtreamParser.storeSeriesContext(parsed, currentBaseUrl, currentUsername, currentPassword)
                    _series.value = parsed
                    applySeriesFavorites()
                }

                liveCat.onSuccess { _liveCategories.value = XtreamParser.parseLiveCategories(it) }
                movieCat.onSuccess { _movieCategories.value = XtreamParser.parseMovieCategories(it) }
                seriesCat.onSuccess { _seriesCategories.value = XtreamParser.parseSeriesCategories(it) }

                val total = _channels.value.size + _movies.value.size + _series.value.size
                val playlist = Playlist(
                    id = "${currentBaseUrl}_$currentUsername".hashCode().toString(),
                    name = source.name ?: currentUsername,
                    source = source, channelCount = total
                )
                saveAndActivatePlaylist(playlist)
            }
        }
    }

    private fun saveAndActivatePlaylist(playlist: Playlist) {
        val updated = _playlists.value.map { it.copy(isActive = false) } + playlist.copy(isActive = true)
        _playlists.value = updated
        localDataSource.savePlaylists(updated)
        _hasContent.value = true
    }

    private fun updateLiveCategories(channels: List<Channel>) {
        val map = mutableMapOf<String, Int>()
        channels.forEach { val c = it.category ?: "Non classé"; map[c] = (map[c] ?: 0) + 1 }
        _liveCategories.value = map.map { Category(id = it.key, name = it.key, channelCount = it.value) }
    }

    fun setActivePlaylist(playlistId: String) {
        val updated = _playlists.value.map { it.copy(isActive = it.id == playlistId) }
        _playlists.value = updated
        localDataSource.savePlaylists(updated)
    }

    fun removePlaylist(playlistId: String) {
        val updated = _playlists.value.filter { it.id != playlistId }
        _playlists.value = updated
        localDataSource.savePlaylists(updated)
        if (updated.isEmpty()) {
            _channels.value = emptyList()
            _movies.value = emptyList()
            _series.value = emptyList()
            _hasContent.value = false
        }
    }

    suspend fun fetchEpg() {
        val epgUrl = localDataSource.loadEpgSource() ?: return
        _isLoading.value = true
        try {
            remoteDataSource.fetchContent(epgUrl).onSuccess { xml ->
                _epgPrograms.value = EpgParser.parse(xml).groupBy { it.channelId }
            }
        } finally { _isLoading.value = false }
    }

    fun getChannelsForCategory(categoryId: String?): List<Channel> =
        if (categoryId == null) _channels.value
        else _channels.value.filter { it.category == categoryId }

    fun getMoviesForCategory(categoryId: String?): List<Movie> =
        if (categoryId == null) _movies.value
        else _movies.value.filter { it.category == categoryId }

    fun getSeriesForCategory(categoryId: String?): List<Series> =
        if (categoryId == null) _series.value
        else _series.value.filter { it.category == categoryId }

    fun searchChannels(query: String): List<Channel> {
        val q = query.lowercase()
        return _channels.value.filter { it.name.lowercase().contains(q) }
    }

    fun searchMovies(query: String): List<Movie> {
        val q = query.lowercase()
        return _movies.value.filter { it.name.lowercase().contains(q) }
    }

    fun searchSeries(query: String): List<Series> {
        val q = query.lowercase()
        return _series.value.filter { it.name.lowercase().contains(q) }
    }

    fun getEpgForChannel(channelId: String): List<EpgProgram> =
        _epgPrograms.value[channelId] ?: emptyList()

    fun getCurrentProgram(channelId: String): EpgProgram? {
        val now = System.currentTimeMillis()
        return _epgPrograms.value[channelId]?.find { it.startTime <= now && it.endTime > now }
    }

    fun toggleChannelFavorite(channelId: String) {
        val favs = localDataSource.loadFavorites().toMutableSet()
        if (favs.contains(channelId)) favs.remove(channelId) else favs.add(channelId)
        localDataSource.saveFavorites(favs)
        _channels.value = _channels.value.map { if (it.id == channelId) it.copy(isFavorite = favs.contains(channelId)) else it }
    }

    fun toggleMovieFavorite(movieId: String) {
        val favs = localDataSource.loadMovieFavoriteIds().toMutableSet()
        if (favs.contains(movieId)) favs.remove(movieId) else favs.add(movieId)
        localDataSource.saveMovieFavoriteIds(favs)
        applyMovieFavorites()
    }

    fun toggleSeriesFavorite(seriesId: String) {
        val favs = localDataSource.loadSeriesFavoriteIds().toMutableSet()
        if (favs.contains(seriesId)) favs.remove(seriesId) else favs.add(seriesId)
        localDataSource.saveSeriesFavoriteIds(favs)
        applySeriesFavorites()
    }

    private fun applyMovieFavorites() {
        val favs = localDataSource.loadMovieFavoriteIds()
        _movies.value = _movies.value.map { it.copy(isFavorite = favs.contains(it.id)) }
    }

    private fun applySeriesFavorites() {
        val favs = localDataSource.loadSeriesFavoriteIds()
        _series.value = _series.value.map { it.copy(isFavorite = favs.contains(it.id)) }
    }

    fun getFavoriteChannels(): List<Channel> {
        val favs = localDataSource.loadFavorites()
        return _channels.value.filter { favs.contains(it.id) }
    }

    fun getFavoriteMovies(): List<Movie> {
        val favs = localDataSource.loadMovieFavoriteIds()
        return _movies.value.filter { favs.contains(it.id) }
    }

    fun getFavoriteSeries(): List<Series> {
        val favs = localDataSource.loadSeriesFavoriteIds()
        return _series.value.filter { favs.contains(it.id) }
    }

    fun getRecentChannels(): List<Channel> {
        val lastId = localDataSource.loadLastChannel()
        return if (lastId != null) listOfNotNull(_channels.value.find { it.id == lastId }) else emptyList()
    }

    fun getResumeProgress(): List<ProgressInfo> = localDataSource.getResumeContent()

    fun saveProgress(progress: ProgressInfo) = localDataSource.saveProgress(progress)

    fun getMovieProgress(movieId: String): ProgressInfo? =
        localDataSource.getProgress(movieId, ContentType.MOVIE)

    fun getEpisodeProgress(episodeId: String): ProgressInfo? =
        localDataSource.getProgress(episodeId, ContentType.EPISODE)

    suspend fun loadSeriesInfo(seriesId: String): Series? {
        return if (currentBaseUrl.isNotEmpty()) {
            val json = remoteDataSource.fetchXtreamSeriesInfo(currentBaseUrl, currentUsername, currentPassword, seriesId)
            json.getOrNull()?.let { XtreamParser.parseSeriesInfo(it, seriesId, currentBaseUrl, currentUsername, currentPassword) }
        } else null
    }
}
