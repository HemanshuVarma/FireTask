package com.varma.hemanshu.firetask

import android.app.Application
import timber.log.Timber

class FireTaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}