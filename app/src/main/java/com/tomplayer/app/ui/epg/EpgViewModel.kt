package com.tomplayer.app.ui.epg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomplayer.app.data.model.Category
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.EpgProgram
import com.tomplayer.app.data.repository.MediaRepository
import com.tomplayer.app.domain.usecase.GetEpgUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EpgViewModel(
    private val repository: MediaRepository,
    private val getEpgUseCase: GetEpgUseCase
) : ViewModel() {

    private val _programs = MutableStateFlow<Map<String, List<EpgProgram>>>(emptyMap())
    val programs: StateFlow<Map<String, List<EpgProgram>>> = _programs.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<String?>(null)
    val selectedChannelId: StateFlow<String?> = _selectedChannelId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        _channels.value = repository.channels.value
        refresh()
    }

    fun selectChannel(channelId: String) {
        _selectedChannelId.value = channelId
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getEpgUseCase.refreshEpg()
                _programs.value = repository.epgPrograms.value
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProgramsForChannel(channelId: String): List<EpgProgram> {
        return _programs.value[channelId] ?: emptyList()
    }
}
