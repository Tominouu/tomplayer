package com.tomplayer.app.di

import android.content.Context
import com.tomplayer.app.data.repository.MediaRepository
import com.tomplayer.app.data.source.LocalDataSource
import com.tomplayer.app.data.source.RemoteDataSource
import com.tomplayer.app.domain.usecase.GetEpgUseCase
import com.tomplayer.app.domain.usecase.GetMoviesUseCase
import com.tomplayer.app.domain.usecase.GetSeriesUseCase
import com.tomplayer.app.domain.usecase.ManageFavoritesUseCase
import com.tomplayer.app.domain.usecase.ManagePlaylistsUseCase
import com.tomplayer.app.domain.usecase.SearchAllUseCase

class AppContainer(context: Context) {

    val remoteDataSource = RemoteDataSource()
    val localDataSource = LocalDataSource(context)

    val mediaRepository = MediaRepository(remoteDataSource, localDataSource)

    val managePlaylistsUseCase = ManagePlaylistsUseCase(mediaRepository)
    val manageFavoritesUseCase = ManageFavoritesUseCase(mediaRepository)
    val getEpgUseCase = GetEpgUseCase(mediaRepository)
    val getMoviesUseCase = GetMoviesUseCase(mediaRepository)
    val getSeriesUseCase = GetSeriesUseCase(mediaRepository)
    val searchAllUseCase = SearchAllUseCase(mediaRepository)
}
