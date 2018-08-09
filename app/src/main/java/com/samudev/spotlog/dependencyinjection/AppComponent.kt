package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.LoggerService
import com.samudev.spotlog.SpotLogTileService
import com.samudev.spotlog.data.SongDao
import dagger.Component

@SpotLogScope
@Component(modules = [RepositoryModule::class])
interface AppComponent {

    fun getSongDao(): SongDao

    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}