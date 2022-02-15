package com.example.simplechat

import android.app.Application
import com.example.simplechat.core.ui.notification.NotificationChannelManager
import com.example.simplechat.services.updates.service.UpdatesService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        NotificationChannelManager.createChannel(this)
        UpdatesService.createService()
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}