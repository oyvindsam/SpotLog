package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.LoggerService
import com.samudev.spotlog.SpotLogTileService
import com.samudev.spotlog.data.SongRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface AppComponent {

    // Subcomponents
    fun songRepository(): SongRepository

    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}