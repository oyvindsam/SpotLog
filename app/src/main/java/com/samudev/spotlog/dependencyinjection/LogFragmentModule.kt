package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.log.LogAdapter
import dagger.Module
import dagger.Provides


@Module
class LogFragmentModule {

    @Provides
    @LogActivityScope
    fun provideLogAdapter() = LogAdapter()

}