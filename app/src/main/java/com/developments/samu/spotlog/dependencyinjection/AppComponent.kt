package com.developments.samu.spotlog.dependencyinjection

import androidx.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.developments.samu.spotlog.data.SongRepository
import com.developments.samu.spotlog.preference.PrefsFragment
import com.developments.samu.spotlog.service.LoggerService
import com.developments.samu.spotlog.service.SpotLogTileService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    // Subcomponents
    fun songRepository(): SongRepository
    fun sharedPreferences(): SharedPreferences
    fun viewModelFactory(): ViewModelProvider.Factory

    fun injectPrefsFragment(fragment: PrefsFragment)
    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}