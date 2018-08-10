package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.log.LogAdapter
import dagger.Module
import dagger.Provides


@Module(includes = [ViewModelModule::class])
class LogFragmentModule {

    @Provides
    @LogActivityScope
    fun provideLogAdapter() = LogAdapter()

}