package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.LoggerService
import com.samudev.spotlog.SpotLogTileService
import com.samudev.spotlog.log.LogFragment
import dagger.Component

@SpotLogScope
@Component(dependencies = [], modules = [RepositoryModule::class])
interface AppComponent {


    fun injectLogFragment(fragment: LogFragment)
    fun injectLoggerService(service: LoggerService)
    fun injectTileService(service: SpotLogTileService)
}