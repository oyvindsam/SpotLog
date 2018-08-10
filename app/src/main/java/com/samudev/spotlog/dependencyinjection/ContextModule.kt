package com.samudev.spotlog.dependencyinjection

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(private val context: Context) {

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = context

}