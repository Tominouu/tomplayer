package com.tomplayer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tomplayer.app.data.model.EpgProgram
import com.tomplayer.app.ui.theme.EpgNowAccent
import com.tomplayer.app.ui.theme.EpgTimelineColor
import com.tomplayer.app.ui.theme.NetflixGrey

@Composable
fun EpgTimeline(
    programs: List<EpgProgram>,
    modifier: Modifier = Modifier
) {
    if (programs.isEmpty()) return

    val totalDuration = programs.sumOf { it.duration }
    if (totalDuration <= 0) return

    val now = System.currentTimeMillis()
    val allStart = programs.minOf { it.startTime }
    val allEnd = programs.maxOf { it.endTime }
    val totalRange = allEnd - allStart

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            programs.forEach { program ->
                val weight = program.duration.toFloat() / totalDuration.toFloat()
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .fillMaxHeight()
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (program.isNow) EpgNowAccent else NetflixGrey)
                )
            }
        }

        if (totalRange > 0) {
            val nowFraction = ((now - allStart).toFloat() / totalRange.toFloat())
                .coerceIn(0f, 1f)
            val offsetValue: Dp = (nowFraction * 1000).dp
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .padding(start = offsetValue)
                    .background(EpgTimelineColor)
            )
        }
    }
}
