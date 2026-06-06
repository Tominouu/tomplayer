package com.tomplayer.app.domain.usecase

import com.tomplayer.app.data.model.Playlist
import com.tomplayer.app.data.model.PlaylistSource
import com.tomplayer.app.data.repository.MediaRepository

class ManagePlaylistsUseCase(private val repository: MediaRepository) {

    fun getPlaylists(): List<Playlist> = repository.playlists.value

    suspend fun addPlaylist(source: PlaylistSource, onProgress: (String) -> Unit = {}) {
        repository.addPlaylist(source, onProgress)
    }

    fun setActivePlaylist(playlistId: String) = repository.setActivePlaylist(playlistId)
    fun removePlaylist(playlistId: String) = repository.removePlaylist(playlistId)
}
