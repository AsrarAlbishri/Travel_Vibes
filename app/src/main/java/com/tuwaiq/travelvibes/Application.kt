package com.tuwaiq.travelvibes

import android.app.Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AppRepository.initialize(this)
    }
}