package com.register.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.register.app.util.Utils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RegisterApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.createNotificationChannel(this)
    }
}