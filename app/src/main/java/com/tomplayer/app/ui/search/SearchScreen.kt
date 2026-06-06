package com.tomplayer.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomplayer.app.ui.components.TvChannelCard
import com.tomplayer.app.ui.theme.NetflixBlack
import com.tomplayer.app.ui.theme.NetflixDarkGrey
import com.tomplayer.app.ui.theme.NetflixGrey
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite80

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onOpenPlayer: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NetflixBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
        ) {
            Text(
                text = "Rechercher une chaîne",
                color = NetflixWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nom de la chaîne…") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NetflixDarkGrey,
                    unfocusedContainerColor = NetflixGrey,
                    focusedTextColor = NetflixWhite,
                    unfocusedTextColor = NetflixWhite80,
                    cursorColor = NetflixWhite
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isSearching) {
                Text(
                    text = "Recherche en cours…",
                    color = NetflixWhite80,
                    fontSize = 16.sp
                )
            } else if (query.isNotBlank() && results.isEmpty()) {
                Text(
                    text = "Aucun résultat pour \"$query\"",
                    color = NetflixWhite80,
                    fontSize = 16.sp
                )
            } else if (results.isNotEmpty()) {
                Text(
                    text = "${results.size} résultat(s)",
                    color = NetflixWhite80,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(results) { channel ->
                        TvChannelCard(
                            name = channel.name,
                            logoUrl = channel.logoUrl,
                            onClick = { onOpenPlayer("live", channel.id) }
                        )
                    }
                }
            }
        }
    }
}
