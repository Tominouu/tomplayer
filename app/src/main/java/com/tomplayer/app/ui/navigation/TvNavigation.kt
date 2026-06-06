package com.tomplayer.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tomplayer.app.ui.home.HomeScreen
import com.tomplayer.app.ui.home.HomeViewModel
import com.tomplayer.app.ui.epg.EpgScreen
import com.tomplayer.app.ui.epg.EpgViewModel
import com.tomplayer.app.ui.search.SearchScreen
import com.tomplayer.app.ui.search.SearchViewModel
import com.tomplayer.app.ui.settings.SettingsScreen
import com.tomplayer.app.ui.settings.SettingsViewModel

object TvRoutes {
    const val HOME = "home"
    const val EPG = "epg"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val PLAYER = "player/{type}/{id}"
    const val MOVIES = "movies"
    const val SERIES = "series"
    const val SERIES_DETAIL = "series/{seriesId}"

    fun player(type: String, id: String) = "player/$type/$id"
    fun seriesDetail(seriesId: String) = "series/$seriesId"
}

@Composable
fun TvNavHost(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    epgViewModel: EpgViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
    onOpenPlayer: (String, String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = TvRoutes.HOME
    ) {
        composable(TvRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onOpenPlayer = { type, id -> onOpenPlayer(type, id) },
                onNavigateToSearch = { navController.navigate(TvRoutes.SEARCH) },
                onNavigateToSettings = { navController.navigate(TvRoutes.SETTINGS) },
                onNavigateToEpg = { navController.navigate(TvRoutes.EPG) }
            )
        }

        composable(TvRoutes.EPG) {
            EpgScreen(
                viewModel = epgViewModel,
                onOpenPlayer = { channelId -> onOpenPlayer("live", channelId) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(TvRoutes.SEARCH) {
            SearchScreen(
                viewModel = searchViewModel,
                onOpenPlayer = { type, id -> onOpenPlayer(type, id) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(TvRoutes.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPlaylistAdded = {
                    navController.popBackStack()
                    homeViewModel.refresh()
                }
            )
        }
    }
}
