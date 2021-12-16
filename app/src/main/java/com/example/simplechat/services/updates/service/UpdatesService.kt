package com.example.simplechat.services.updates.service

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simplechat.R
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.core.coreui.base.BaseService
import com.example.simplechat.core.coreui.notification.NotificationChannelManager
import com.example.simplechat.screens.chat.domain.models.Update
import com.example.simplechat.services.updates.websocket.UpdatesWebSocket
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdatesService: BaseService() {

    @Inject
    lateinit var updatesWebSocket: UpdatesWebSocket

    @Inject
    lateinit var userPreferenceStorage: UserPreferenceStorage

    private val binder = UpdatesBinder()

    private var listener: UpdateListener? = null

    fun setListener(listener: UpdateListener) {
        this.listener = listener

        if (updatesWebSocket.isOpen)
            listener.onOpen()
    }

    fun removeListener() {
        listener = null
    }

    private val updatesConnectionListener = object : UpdatesWebSocket.UpdatesConnectionListener {
        override fun onOpen() {
            listener?.onOpen()
        }

        override fun onNewUpdates(updates: List<Update>) {

            for (update in updates) {
                if (update.message != null &&
                    update.message.senderId.toString() == userPreferenceStorage.id) continue

                val listenerResult = listener?.onUpdate(update) ?: false

                if (!listenerResult) {
                    if (update.message != null)
                        showNotification(
                            title = getString(R.string.message_label),
                            message = "${update.message.sender.username}: ${update.message.text}"
                        )
                }
            }
        }

        override fun onClosed(text: String) {
            listener?.onClosed(text)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updatesWebSocket.setUpdatesConnectionListener(updatesConnectionListener)

        serviceScope.launch {
            updatesWebSocket.start()
        }

        return START_STICKY
    }

    fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, NotificationChannelManager.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId++, notification)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    interface UpdateListener {
        fun onOpen()

        fun onUpdate(update: Update): Boolean

        fun onClosed(text: String)
    }

    inner class UpdatesBinder: Binder() {
        fun getService(): UpdatesService = this@UpdatesService
    }

    override fun onDestroy() {
        super.onDestroy()
        isCreated = false
    }

    companion object {
        private var isCreated = false

        fun createService(application: Application, context: Context = application) {
            if (isCreated) return

            val serviceIntent = Intent(context, UpdatesService::class.java)
            application.startService(serviceIntent)

            isCreated = true
        }

        fun bindService(application: Application, connection: ServiceConnection) {
            if (!isCreated)
                createService(application)

            val serviceIntent = Intent(application, UpdatesService::class.java)
            application.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }

        private var notificationId = 0
    }
}