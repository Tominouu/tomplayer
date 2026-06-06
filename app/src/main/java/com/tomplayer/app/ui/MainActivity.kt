package com.tomplayer.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.tomplayer.app.TomPlayerApplication
import com.tomplayer.app.data.repository.MediaRepository
import com.tomplayer.app.domain.usecase.GetEpgUseCase
import com.tomplayer.app.domain.usecase.GetMoviesUseCase
import com.tomplayer.app.domain.usecase.GetSeriesUseCase
import com.tomplayer.app.domain.usecase.ManagePlaylistsUseCase
import com.tomplayer.app.domain.usecase.SearchAllUseCase
import com.tomplayer.app.ui.epg.EpgViewModel
import com.tomplayer.app.ui.home.HomeViewModel
import com.tomplayer.app.ui.navigation.TvNavHost
import com.tomplayer.app.ui.player.PlayerActivity
import com.tomplayer.app.ui.search.SearchViewModel
import com.tomplayer.app.ui.settings.SettingsViewModel
import com.tomplayer.app.ui.theme.TomPlayerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TomPlayerApplication
        val repository = app.mediaRepository
        val localDataSource = app.appContainer.localDataSource

        setContent {
            TomPlayerTheme {
                MainScreen(
                    repository = repository,
                    localDataSource = localDataSource,
                    onOpenPlayer = { type, id ->
                        val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                            putExtra("content_type", type)
                            putExtra("content_id", id)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    repository: MediaRepository,
    localDataSource: com.tomplayer.app.data.source.LocalDataSource,
    onOpenPlayer: (String, String) -> Unit
) {
    val navController = rememberNavController()
    val managePlaylistsUseCase = remember { ManagePlaylistsUseCase(repository) }
    val getEpgUseCase = remember { GetEpgUseCase(repository) }
    val getMoviesUseCase = remember { GetMoviesUseCase(repository) }
    val getSeriesUseCase = remember { GetSeriesUseCase(repository) }
    val searchAllUseCase = remember { SearchAllUseCase(repository) }

    val homeViewModel = remember { HomeViewModel(repository, getMoviesUseCase, getSeriesUseCase) }
    val epgViewModel = remember { EpgViewModel(repository, getEpgUseCase) }
    val searchViewModel = remember { SearchViewModel(searchAllUseCase) }
    val settingsViewModel = remember {
        SettingsViewModel(repository, managePlaylistsUseCase, localDataSource)
    }

    TvNavHost(
        navController = navController,
        homeViewModel = homeViewModel,
        epgViewModel = epgViewModel,
        searchViewModel = searchViewModel,
        settingsViewModel = settingsViewModel,
        onOpenPlayer = onOpenPlayer
    )
}
