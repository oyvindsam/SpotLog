package com.samudev.spotlog.dependencyinjection

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    @SpotLogScope
    fun provideContext(): Context = context

}