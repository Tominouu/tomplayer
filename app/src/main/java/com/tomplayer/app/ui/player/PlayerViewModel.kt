package com.tomplayer.app.ui.player

import androidx.lifecycle.ViewModel
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.ContentType
import com.tomplayer.app.data.model.EpgProgram
import com.tomplayer.app.data.model.Movie
import com.tomplayer.app.data.model.ProgressInfo
import com.tomplayer.app.data.model.Series
import com.tomplayer.app.data.repository.MediaRepository
import com.tomplayer.app.data.source.LocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class PlaybackContent {
    data class LiveChannel(val channel: Channel, val program: EpgProgram? = null) : PlaybackContent()
    data class MovieContent(val movie: Movie, val progress: ProgressInfo? = null) : PlaybackContent()
    data class EpisodeContent(
        val seriesName: String,
        val episodeId: String,
        val episodeName: String,
        val streamUrl: String,
        val progress: ProgressInfo? = null
    ) : PlaybackContent()
}

class PlayerViewModel(
    private val repository: MediaRepository,
    private val localDataSource: LocalDataSource
) : ViewModel() {

    private val _playbackContent = MutableStateFlow<PlaybackContent?>(null)
    val playbackContent: StateFlow<PlaybackContent?> = _playbackContent.asStateFlow()

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _showControls = MutableStateFlow(false)
    val showControls: StateFlow<Boolean> = _showControls.asStateFlow()

    private var channelIndex = 0

    init {
        _channels.value = repository.channels.value
    }

    fun loadContent(type: String, id: String) {
        when (type) {
            "live" -> {
                val ch = _channels.value.find { it.id == id } ?: return
                channelIndex = _channels.value.indexOf(ch)
                localDataSource.saveLastChannel(id)
                val program = repository.getCurrentProgram(ch.epgChannelId ?: ch.id)
                _playbackContent.value = PlaybackContent.LiveChannel(ch, program)
            }
            "movie" -> {
                val movie = repository.movies.value.find { it.id == id } ?: return
                val progress = repository.getMovieProgress(id)
                _playbackContent.value = PlaybackContent.MovieContent(movie, progress)
            }
        }
    }

    fun loadEpisode(seriesName: String, episodeId: String, episodeName: String, streamUrl: String) {
        val progress = localDataSource.getProgress(episodeId, ContentType.EPISODE)
        _playbackContent.value = PlaybackContent.EpisodeContent(seriesName, episodeId, episodeName, streamUrl, progress)
    }

    fun switchToNextChannel() {
        val channels = _channels.value
        if (channels.isEmpty()) return
        channelIndex = (channelIndex + 1) % channels.size
        val ch = channels[channelIndex]
        localDataSource.saveLastChannel(ch.id)
        val program = repository.getCurrentProgram(ch.epgChannelId ?: ch.id)
        _playbackContent.value = PlaybackContent.LiveChannel(ch, program)
    }

    fun switchToPreviousChannel() {
        val channels = _channels.value
        if (channels.isEmpty()) return
        channelIndex = if (channelIndex - 1 < 0) channels.size - 1 else channelIndex - 1
        val ch = channels[channelIndex]
        localDataSource.saveLastChannel(ch.id)
        val program = repository.getCurrentProgram(ch.epgChannelId ?: ch.id)
        _playbackContent.value = PlaybackContent.LiveChannel(ch, program)
    }

    fun toggleControls() { _showControls.value = !_showControls.value }
    fun hideControls() { _showControls.value = false }
}
