package com.tomplayer.app.data.source

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomplayer.app.data.model.ContentType
import com.tomplayer.app.data.model.Playlist
import com.tomplayer.app.data.model.PlaylistSource
import com.tomplayer.app.data.model.ProgressInfo

class LocalDataSource(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tomplayer_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun savePlaylists(playlists: List<Playlist>) {
        val json = gson.toJson(playlists)
        prefs.edit().putString(KEY_PLAYLISTS, json).apply()
    }

    fun loadPlaylists(): List<Playlist> {
        val json = prefs.getString(KEY_PLAYLISTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Playlist>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun saveFavorites(channelIds: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, channelIds).apply()
    }

    fun loadFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun saveLastChannel(channelId: String?) {
        prefs.edit().putString(KEY_LAST_CHANNEL, channelId).apply()
    }

    fun loadLastChannel(): String? {
        return prefs.getString(KEY_LAST_CHANNEL, null)
    }

    fun saveEpgSource(url: String) {
        prefs.edit().putString(KEY_EPG_SOURCE, url).apply()
    }

    fun loadEpgSource(): String? {
        return prefs.getString(KEY_EPG_SOURCE, null)
    }

    fun getString(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }

    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun saveProgress(progress: ProgressInfo) {
        val all = loadAllProgress().toMutableList()
        all.removeAll { it.contentId == progress.contentId && it.contentType == progress.contentType }
        all.add(progress)
        val json = gson.toJson(all)
        prefs.edit().putString(KEY_PROGRESS, json).apply()
    }

    fun loadAllProgress(): List<ProgressInfo> {
        val json = prefs.getString(KEY_PROGRESS, null) ?: return emptyList()
        val type = object : TypeToken<List<ProgressInfo>>() {}.type
        return try { gson.fromJson(json, type) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun getProgress(contentId: String, contentType: ContentType): ProgressInfo? {
        return loadAllProgress().find { it.contentId == contentId && it.contentType == contentType }
    }

    fun getResumeContent(): List<ProgressInfo> {
        return loadAllProgress()
            .filter { it.percent in 0.05f..0.95f }
            .sortedByDescending { it.lastPlayed }
            .take(20)
    }

    fun saveMovieFavoriteIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_MOVIE_FAVORITES, ids).apply()
    }

    fun loadMovieFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_MOVIE_FAVORITES, emptySet()) ?: emptySet()
    }

    fun saveSeriesFavoriteIds(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_SERIES_FAVORITES, ids).apply()
    }

    fun loadSeriesFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_SERIES_FAVORITES, emptySet()) ?: emptySet()
    }

    fun saveLastMoviePosition(movieId: String, position: Long, duration: Long) {
        saveProgress(ProgressInfo(movieId, ContentType.MOVIE, position, duration))
    }

    fun saveLastEpisodePosition(episodeId: String, position: Long, duration: Long) {
        saveProgress(ProgressInfo(episodeId, ContentType.EPISODE, position, duration))
    }

    companion object {
        private const val KEY_PLAYLISTS = "playlists"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_MOVIE_FAVORITES = "movie_favorites"
        private const val KEY_SERIES_FAVORITES = "series_favorites"
        private const val KEY_LAST_CHANNEL = "last_channel"
        private const val KEY_EPG_SOURCE = "epg_source"
        private const val KEY_PROGRESS = "watch_progress"
    }
}
