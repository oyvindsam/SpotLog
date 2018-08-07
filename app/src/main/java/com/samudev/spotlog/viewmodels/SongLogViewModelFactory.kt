package com.samudev.spotlog.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.samudev.spotlog.data.SongRepository


class SongLogViewModelFactory(private val repository: SongRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = SongLogViewModel(repository) as T
}