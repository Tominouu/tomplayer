package com.tomplayer.app.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@UnstableApi
class PlayerController(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setSeekForwardIncrementMs(10000)
        .setSeekBackIncrementMs(10000)
        .build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _volume = MutableStateFlow(1f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = playbackState == Player.STATE_BUFFERING
                _isPlaying.value = playbackState == Player.STATE_READY && exoPlayer.playWhenReady
            }

            override fun onPlayerError(error: PlaybackException) {
                _error.value = error.localizedMessage ?: "Erreur de lecture"
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    fun play(url: String, userAgent: String? = null) {
        _error.value = null
        _isBuffering.value = true

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(30000)

        if (userAgent != null) {
            dataSourceFactory.setUserAgent(userAgent)
        }

        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(MediaItem.fromUri(url))

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun playMediaItem(mediaItem: MediaItem) {
        _error.value = null
        _isBuffering.value = true
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekForward() {
        exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
    }

    fun seekBackward() {
        exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
        exoPlayer.volume = _volume.value
    }

    fun toggleMute() {
        exoPlayer.volume = if (exoPlayer.volume > 0f) 0f else _volume.value
    }

    fun release() {
        exoPlayer.release()
    }

    fun getPlayer(): ExoPlayer = exoPlayer
}
