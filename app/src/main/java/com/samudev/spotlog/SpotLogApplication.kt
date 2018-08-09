package com.samudev.spotlog

import android.app.Application
import com.samudev.spotlog.dependencyinjection.ContextModule
import com.samudev.spotlog.dependencyinjection.DaggerSpotLogComponent
import com.samudev.spotlog.dependencyinjection.SpotLogComponent


class SpotLogApplication : Application() {

    companion object {

        private lateinit var appComponent: SpotLogComponent

        fun getAppComponent() = appComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerSpotLogComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }
}