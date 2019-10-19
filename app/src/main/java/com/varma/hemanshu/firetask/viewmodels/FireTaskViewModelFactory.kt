package com.varma.hemanshu.firetask.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FireTaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FireTaskViewModel::class.java)) {
            return FireTaskViewModel(application) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}