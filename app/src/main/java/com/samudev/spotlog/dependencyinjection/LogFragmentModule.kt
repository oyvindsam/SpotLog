package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.log.LogAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class LogFragmentModule {


    @Provides
    @Singleton
    fun provideLogAdapter() = LogAdapter()

}