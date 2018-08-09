package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.log.LogFragment
import dagger.Module
import dagger.Provides

@Module(includes = [ViewModelModule::class])
class LogFragmentModule(private val logFragment: LogFragment) {

    @Provides
    @LogActivityScope
    fun provideLogFragment(): LogFragment = logFragment
}