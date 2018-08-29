package com.developments.samu.spotlog

import android.app.Application
import com.developments.samu.spotlog.dependencyinjection.AppComponent
import com.developments.samu.spotlog.dependencyinjection.ContextModule
import com.developments.samu.spotlog.dependencyinjection.DaggerAppComponent
import com.developments.samu.spotlog.service.LoggerService
import com.jakewharton.threetenabp.AndroidThreeTen


class SpotLogApplication : Application() {

    companion object {

        private lateinit var appComponent: AppComponent

        fun getAppComponent() = appComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
        AndroidThreeTen.init(this)
        LoggerService.createNotificationChannel(this)
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }
}