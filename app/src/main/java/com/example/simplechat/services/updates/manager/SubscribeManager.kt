package com.example.simplechat.services.updates.manager

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

object SubscribeManager {

    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            
        }

    }
}