package com.tomplayer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomplayer.app.ui.theme.NetflixBlack
import com.tomplayer.app.ui.theme.NetflixDarkGrey
import com.tomplayer.app.ui.theme.NetflixRed
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite80

@Composable
fun TvTopBar(
    title: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(NetflixBlack.copy(alpha = 0.95f))
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "TomPlayer",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = NetflixRed
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TappableLabel(
                label = "Rechercher",
                onClick = onSearchClick
            )
            TappableLabel(
                label = "Paramètres",
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun TappableLabel(
    label: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused },
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isFocused) NetflixDarkGrey else NetflixBlack
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (isFocused) NetflixWhite else NetflixWhite80,
            fontSize = 16.sp,
            fontWeight = if (isFocused) FontWeight.Medium else FontWeight.Normal
        )
    }
}
