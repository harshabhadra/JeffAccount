package com.example.jeffaccount

import android.app.Application
import timber.log.Timber

class JeffApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}