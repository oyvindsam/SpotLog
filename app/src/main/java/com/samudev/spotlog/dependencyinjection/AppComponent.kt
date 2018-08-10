package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.LoggerService
import com.samudev.spotlog.SpotLogTileService
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.preference.PrefsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    // Subcomponents
    fun songRepository(): SongRepository

    fun injectPrefsFragment(fragment: PrefsFragment)
    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}