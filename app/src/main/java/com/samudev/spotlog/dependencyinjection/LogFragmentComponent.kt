package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.log.LogFragment
import dagger.Component


@LogActivityScope
@Component(modules = [LogFragmentModule::class], dependencies = [AppComponent::class])
interface LogFragmentComponent {

    fun injectLogFragment(fragment: LogFragment)

}