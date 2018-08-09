package com.samudev.spotlog

import android.app.Application
import com.samudev.spotlog.dependencyinjection.AppComponent
import com.samudev.spotlog.dependencyinjection.ContextModule
import com.samudev.spotlog.dependencyinjection.DaggerAppComponent


class SpotLogApplication : Application() {

    companion object {

        private lateinit var appComponent: AppComponent

        fun getAppComponent() = appComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }
}