package com.tomplayer.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomplayer.app.ui.theme.NetflixBlack
import com.tomplayer.app.ui.theme.NetflixDarkGrey
import com.tomplayer.app.ui.theme.NetflixGrey
import com.tomplayer.app.ui.theme.NetflixRed
import com.tomplayer.app.ui.theme.NetflixWhite
import com.tomplayer.app.ui.theme.NetflixWhite80
import com.tomplayer.app.ui.theme.OnSurfaceVariantDark

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onPlaylistAdded: () -> Unit
) {
    val playlists by viewModel.playlists.collectAsState()
    val epgUrl by viewModel.epgUrl.collectAsState()
    val isAddingPlaylist by viewModel.isAddingPlaylist.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var urlInput by remember { mutableStateOf("") }
    var playlistNameInput by remember { mutableStateOf("") }
    var xtreamServerUrl by remember { mutableStateOf("") }
    var xtreamUsername by remember { mutableStateOf("") }
    var xtreamPassword by remember { mutableStateOf("") }
    var epgInput by remember { mutableStateOf(epgUrl) }

    var isTextFieldFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    BackHandler {
        if (isTextFieldFocused) {
            focusManager.clearFocus()
            isTextFieldFocused = false
        } else {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NetflixBlack)
            .padding(48.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Paramètres",
                color = NetflixWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ajouter une playlist M3U",
                color = NetflixWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            TvTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.fillMaxWidth(),
                onFocusChange = { isTextFieldFocused = it },
                placeholder = "URL de la playlist M3U…"
            )

            Spacer(modifier = Modifier.height(8.dp))

            TvTextField(
                value = playlistNameInput,
                onValueChange = { playlistNameInput = it },
                modifier = Modifier.fillMaxWidth(),
                onFocusChange = { isTextFieldFocused = it },
                placeholder = "Nom (optionnel)…"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (urlInput.isNotBlank()) {
                        viewModel.addPlaylistFromUrl(
                            urlInput,
                            playlistNameInput.ifBlank { null }
                        )
                    }
                },
                enabled = urlInput.isNotBlank() && !isAddingPlaylist,
                colors = ButtonDefaults.buttonColors(containerColor = NetflixRed)
            ) {
                Text("Ajouter la playlist M3U")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ajouter Xtream Codes",
                color = NetflixWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            TvTextField(
                value = xtreamServerUrl,
                onValueChange = { xtreamServerUrl = it },
                modifier = Modifier.fillMaxWidth(),
                onFocusChange = { isTextFieldFocused = it },
                placeholder = "URL du serveur…"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TvTextField(
                    value = xtreamUsername,
                    onValueChange = { xtreamUsername = it },
                    modifier = Modifier.weight(1f),
                    onFocusChange = { isTextFieldFocused = it },
                    placeholder = "Utilisateur…"
                )
                TvTextField(
                    value = xtreamPassword,
                    onValueChange = { xtreamPassword = it },
                    modifier = Modifier.weight(1f),
                    onFocusChange = { isTextFieldFocused = it },
                    placeholder = "Mot de passe…"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.addXtreamPlaylist(xtreamServerUrl, xtreamUsername, xtreamPassword)
                },
                enabled = xtreamServerUrl.isNotBlank() && xtreamUsername.isNotBlank() &&
                    xtreamPassword.isNotBlank() && !isAddingPlaylist,
                colors = ButtonDefaults.buttonColors(containerColor = NetflixRed)
            ) {
                Text("Ajouter Xtream Codes")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Source EPG (XMLTV)",
                color = NetflixWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            TvTextField(
                value = epgInput,
                onValueChange = { epgInput = it },
                modifier = Modifier.fillMaxWidth(),
                onFocusChange = { isTextFieldFocused = it },
                placeholder = "URL du guide TV (XMLTV)…"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.saveEpgUrl(epgInput) },
                enabled = epgInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NetflixRed)
            ) {
                Text("Enregistrer la source EPG")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (playlists.isNotEmpty()) {
                Text(
                    text = "Mes playlists",
                    color = NetflixWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                playlists.forEach { playlist ->
                    Card(
                        onClick = { viewModel.setActivePlaylist(playlist.id) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (playlist.isActive) NetflixRed.copy(alpha = 0.3f)
                            else NetflixDarkGrey
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = playlist.name,
                                    color = NetflixWhite,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${playlist.channelCount} chaînes",
                                    color = OnSurfaceVariantDark,
                                    fontSize = 14.sp
                                )
                            }
                            Button(
                                onClick = { viewModel.removePlaylist(playlist.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NetflixDarkGrey
                                )
                            ) {
                                Text("Supprimer")
                            }
                        }
                    }
                }
            }

            statusMessage?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = NetflixWhite80,
                    fontSize = 14.sp
                )
            }

            if (isAddingPlaylist) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chargement en cours…",
                    color = NetflixWhite80,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TvTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    onFocusChange: ((Boolean) -> Unit)? = null
) {
    var isActivated by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onFocusChanged { onFocusChange?.invoke(it.isFocused) }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { isActivated = true }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().onFocusChanged {
                if (!it.isFocused) isActivated = false
            },
            readOnly = !isActivated,
            singleLine = true,
            placeholder = { Text(placeholder) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = NetflixDarkGrey,
                unfocusedContainerColor = NetflixGrey,
                focusedTextColor = NetflixWhite,
                unfocusedTextColor = NetflixWhite80
            )
        )
    }
}
