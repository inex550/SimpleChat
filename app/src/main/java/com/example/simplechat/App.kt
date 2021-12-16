package com.example.simplechat

import android.app.Application
import com.example.simplechat.core.coreui.notification.NotificationChannelManager
import com.example.simplechat.services.updates.service.UpdatesService
import com.github.terrakok.cicerone.Cicerone
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationChannelManager.createChannel(this)

        UpdatesService.createService(this)
    }
}