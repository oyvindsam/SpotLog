package com.developments.samu.spotlog.dependencyinjection

import com.developments.samu.spotlog.log.LogFragment
import dagger.Component


@LogActivityScope
@Component(dependencies = [AppComponent::class])
interface LogFragmentComponent {

    fun injectLogFragment(fragment: LogFragment)

}