package com.samudev.spotlog.dependencyinjection

import android.service.quicksettings.TileService
import com.samudev.spotlog.LoggerService
import com.samudev.spotlog.viewmodels.SongLogViewModel
import dagger.Component

@SpotLogScope
@Component(dependencies = [], modules = [RepositoryModule::class])
interface SpotLogComponent {

    fun injectSongLogViewModel(viewModel: SongLogViewModel)
    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: TileService)
}