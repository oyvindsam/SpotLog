package com.developments.samu.spotlog.dependencyinjection

import javax.inject.Qualifier
import javax.inject.Scope

// Application level scope
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class SpotLogScope

// Scope for fragment's viewmodel
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class LogActivityScope

@Qualifier
annotation class ApplicationContext

// Not used, but might be useful in future
@Qualifier
annotation class FragmentContext