package com.tomplayer.app.ui.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomplayer.app.data.model.Channel
import com.tomplayer.app.data.model.EpgProgram
import com.tomplayer.app.ui.theme.EpgBackground
import com.tomplayer.app.ui.theme.EpgNowAccent
import com.tomplayer.app.ui.theme.NetflixBlack
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite60
import com.tomplayer.app.ui.theme.OnSurfaceVariantDark

@Composable
fun EpgScreen(
    viewModel: EpgViewModel,
    onOpenPlayer: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val channels by viewModel.channels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NetflixBlack)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 48.dp, end = 48.dp, bottom = 48.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Guide TV",
                    color = NetflixWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(channels.take(50)) { _, channel ->
                val programs = viewModel.getProgramsForChannel(channel.epgChannelId ?: channel.id)
                EpgChannelRow(
                    channel = channel,
                    programs = programs,
                    onChannelClick = { onOpenPlayer(channel.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EpgChannelRow(
    channel: Channel,
    programs: List<EpgProgram>,
    onChannelClick: () -> Unit
) {
    Card(
        onClick = onChannelClick,
        modifier = Modifier.fillMaxWidth().height(80.dp),
        colors = CardDefaults.cardColors(containerColor = EpgBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Text(
                text = channel.name,
                color = NetflixWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (programs.isNotEmpty()) {
                val nowProgram = programs.find { it.isNow }
                val nextPrograms = programs.filter { it.startTime > System.currentTimeMillis() }.take(2)

                if (nowProgram != null) {
                    Text(
                        text = "\u25B6 ${nowProgram.title}",
                        color = EpgNowAccent,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (nextPrograms.isNotEmpty()) {
                    Text(
                        text = nextPrograms.joinToString(" → ") { it.title },
                        color = NetflixWhite60,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text(
                    text = "Aucune information programme",
                    color = OnSurfaceVariantDark,
                    fontSize = 12.sp
                )
            }
        }
    }
}
