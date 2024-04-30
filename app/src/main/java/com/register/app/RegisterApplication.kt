package com.register.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RegisterApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}