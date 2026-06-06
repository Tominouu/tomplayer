package com.tomplayer.app.ui.player

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.tomplayer.app.TomPlayerApplication
import com.tomplayer.app.data.model.ContentType
import com.tomplayer.app.data.model.ProgressInfo
import com.tomplayer.app.player.PlayerController
import com.tomplayer.app.ui.theme.NetflixBlack
import com.tomplayer.app.ui.theme.NetflixDarkGrey
import com.tomplayer.app.ui.theme.NetflixRed
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite60
import com.tomplayer.app.ui.theme.NetflixWhite80
import kotlinx.coroutines.delay

@UnstableApi
class PlayerActivity : ComponentActivity() {

    private lateinit var playerController: PlayerController
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TomPlayerApplication
        playerController = PlayerController(this)
        viewModel = PlayerViewModel(
            repository = app.mediaRepository,
            localDataSource = app.appContainer.localDataSource
        )

        val type = intent.getStringExtra("content_type") ?: return
        val id = intent.getStringExtra("content_id") ?: return
        viewModel.loadContent(type, id)

        setContent {
            PlayerScreen(
                playerController = playerController,
                viewModel = viewModel
            )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> { viewModel.switchToPreviousChannel(); true }
            KeyEvent.KEYCODE_DPAD_DOWN -> { viewModel.switchToNextChannel(); true }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { viewModel.toggleControls(); true }
            KeyEvent.KEYCODE_BACK -> {
                if (viewModel.showControls.value) { viewModel.hideControls(); true }
                else super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { playerController.togglePlayPause(); true }
            KeyEvent.KEYCODE_DPAD_LEFT -> { playerController.seekBackward(); true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { playerController.seekForward(); true }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerController.release()
    }
}

@Composable
fun PlayerScreen(
    playerController: PlayerController,
    viewModel: PlayerViewModel
) {
    val playbackContent by viewModel.playbackContent.collectAsState()
    val showControls by viewModel.showControls.collectAsState()
    val isBuffering by playerController.isBuffering.collectAsState()
    val isPlaying by playerController.isPlaying.collectAsState()

    var autoHide by remember { mutableStateOf(false) }

    LaunchedEffect(showControls) {
        if (showControls) { autoHide = true; delay(5000); if (autoHide) viewModel.hideControls() }
        else autoHide = false
    }

    LaunchedEffect(playbackContent) {
        playbackContent?.let { content ->
            when (content) {
                is PlaybackContent.LiveChannel ->
                    playerController.play(content.channel.streamUrl, content.channel.userAgent)
                is PlaybackContent.MovieContent ->
                    playerController.play(content.movie.streamUrl)
                is PlaybackContent.EpisodeContent ->
                    playerController.play(content.streamUrl)
            }
        }
    }

    val title = when (val c = playbackContent) {
        is PlaybackContent.LiveChannel -> c.channel.name
        is PlaybackContent.MovieContent -> c.movie.name
        is PlaybackContent.EpisodeContent -> "${c.seriesName} – ${c.episodeName}"
        null -> ""
    }
    val subtitle = when (val c = playbackContent) {
        is PlaybackContent.LiveChannel -> c.program?.title
        is PlaybackContent.MovieContent -> c.movie.year
        is PlaybackContent.EpisodeContent -> null
        null -> null
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = playerController.getPlayer()
                    useController = false
                    setBackgroundColor(android.graphics.Color.BLACK)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isBuffering) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chargement…", color = NetflixWhite, fontSize = 18.sp)
            }
        }

        AnimatedVisibility(
            visible = showControls, enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(32.dp)
                ) {
                    Text(title, color = NetflixWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    subtitle?.let {
                        Text(it, color = NetflixWhite80, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerCtrlBtn("<<") { viewModel.switchToPreviousChannel() }
                    Spacer(Modifier.width(24.dp))
                    PlayerCtrlBtn(if (isPlaying) "||" else "\u25B6") { playerController.togglePlayPause() }
                    Spacer(Modifier.width(24.dp))
                    PlayerCtrlBtn(">>") { viewModel.switchToNextChannel() }
                }
            }
        }
    }
}

@Composable
private fun PlayerCtrlBtn(label: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (focused) NetflixRed.copy(alpha = 0.8f) else NetflixDarkGrey)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = NetflixWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
