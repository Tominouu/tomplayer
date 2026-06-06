package com.tomplayer.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomplayer.app.data.model.Playlist
import com.tomplayer.app.data.repository.MediaRepository
import com.tomplayer.app.data.source.LocalDataSource
import com.tomplayer.app.domain.usecase.ManagePlaylistsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: MediaRepository,
    private val managePlaylistsUseCase: ManagePlaylistsUseCase,
    private val localDataSource: LocalDataSource
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _epgUrl = MutableStateFlow("")
    val epgUrl: StateFlow<String> = _epgUrl.asStateFlow()

    private val _isAddingPlaylist = MutableStateFlow(false)
    val isAddingPlaylist: StateFlow<Boolean> = _isAddingPlaylist.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        _playlists.value = managePlaylistsUseCase.getPlaylists()
        _epgUrl.value = localDataSource.loadEpgSource() ?: ""
    }

    fun addPlaylistFromUrl(url: String, name: String?) {
        viewModelScope.launch {
            _isAddingPlaylist.value = true
            _statusMessage.value = "Ajout de la playlist…"
            try {
                managePlaylistsUseCase.addPlaylist(
                    com.tomplayer.app.data.model.PlaylistSource.M3uUrl(url, name),
                    onProgress = { msg -> _statusMessage.value = msg }
                )
                _playlists.value = managePlaylistsUseCase.getPlaylists()
                _statusMessage.value = "Playlist ajoutée avec succès"
            } catch (e: Exception) {
                _statusMessage.value = "Erreur : ${e.message}"
            } finally {
                _isAddingPlaylist.value = false
            }
        }
    }

    fun addXtreamPlaylist(serverUrl: String, username: String, password: String) {
        viewModelScope.launch {
            _isAddingPlaylist.value = true
            _statusMessage.value = "Connexion au serveur…"
            try {
                managePlaylistsUseCase.addPlaylist(
                    com.tomplayer.app.data.model.PlaylistSource.Xtream(serverUrl, username, password),
                    onProgress = { msg -> _statusMessage.value = msg }
                )
                _playlists.value = managePlaylistsUseCase.getPlaylists()
                _statusMessage.value = "Playlist Xtream ajoutée avec succès"
            } catch (e: Exception) {
                _statusMessage.value = "Erreur : ${e.message}"
            } finally {
                _isAddingPlaylist.value = false
            }
        }
    }

    fun setActivePlaylist(playlistId: String) {
        managePlaylistsUseCase.setActivePlaylist(playlistId)
        _playlists.value = managePlaylistsUseCase.getPlaylists()
    }

    fun removePlaylist(playlistId: String) {
        managePlaylistsUseCase.removePlaylist(playlistId)
        _playlists.value = managePlaylistsUseCase.getPlaylists()
    }

    fun saveEpgUrl(url: String) {
        localDataSource.saveEpgSource(url)
        _epgUrl.value = url
        _statusMessage.value = "Source EPG enregistrée"
    }

    fun clearStatus() {
        _statusMessage.value = null
    }
}
