package com.samudev.spotlog.dependencyinjection

import com.samudev.spotlog.log.LogFragment
import dagger.Component


@LogActivityScope
@Component(dependencies = [AppComponent::class])
interface LogFragmentComponent {

    fun injectLogFragment(fragment: LogFragment)

}