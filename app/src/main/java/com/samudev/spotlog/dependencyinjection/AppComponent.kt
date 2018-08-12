package com.samudev.spotlog.dependencyinjection

import android.content.SharedPreferences
import com.samudev.spotlog.data.SongRepository
import com.samudev.spotlog.preference.PrefsFragment
import com.samudev.spotlog.service.LoggerService
import com.samudev.spotlog.service.SpotLogTileService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    // Subcomponents
    fun songRepository(): SongRepository
    fun sharedPreferences(): SharedPreferences

    fun injectPrefsFragment(fragment: PrefsFragment)
    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}