package com.tomplayer.app

import android.app.Application
import com.tomplayer.app.di.AppContainer
import com.tomplayer.app.data.repository.MediaRepository

class TomPlayerApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    val mediaRepository: MediaRepository
        get() = appContainer.mediaRepository

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
