package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.LoggerService
import com.samudev.spotlog.SpotLogTileService
import com.samudev.spotlog.viewmodels.SongLogViewModel
import dagger.Component

@SpotLogScope
@Component(dependencies = [], modules = [RepositoryModule::class])
interface SpotLogComponent {

    fun injectSongLogViewModel(viewModel: SongLogViewModel)
    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}