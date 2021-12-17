package com.example.simplechat.services.updates.service

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simplechat.App
import com.example.simplechat.R
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.core.coreui.base.BaseService
import com.example.simplechat.core.coreui.notification.NotificationChannelManager
import com.example.simplechat.services.updates.models.Update
import com.example.simplechat.services.updates.models.UpdateNet
import com.example.simplechat.services.updates.websocket.UpdatesWebSocket
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
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

        if (updatesWebSocket.isConnected)
            listener.onOpen()
    }

    fun removeListener() {
        listener = null
    }

    private var isReconnecting = false

    private val updatesConnectionListener = object : UpdatesWebSocket.UpdatesConnectionListener {
        override fun onOpen() {
            isReconnecting = false
            listener?.onOpen()
        }

        override fun onNewUpdates(updateNets: List<UpdateNet>) {

            val updates = updateNets.map { it.transform(this@UpdatesService, userPreferenceStorage.id.orEmpty()) }
            proceedUpdates(updates)
        }

        override fun onClosed(text: String) {
            updatesWebSocket.start()

            if (isReconnecting) return

            listener?.onClosed(text)
            isReconnecting = true
        }
    }

    fun proceedUpdates(updates: List<Update>) {
        for (update in updates) {
            val listenerResult = listener?.onUpdate(update) ?: false

            if (!listenerResult) {
                if (update.message != null && update.message.senderId.toString() == userPreferenceStorage.id)
                    showNotification(
                        title = getString(R.string.message_label),
                        message = "${update.message.sender.username}: ${update.message.text}"
                    )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startWebSocketListen()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        if (!updatesWebSocket.isConnected) {
            startWebSocketListen()
        }

        return binder
    }

    private fun startWebSocketListen() {
        updatesWebSocket.setUpdatesConnectionListener(updatesConnectionListener)

        serviceScope.launch {
            updatesWebSocket.start()
        }
    }

    private fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, NotificationChannelManager.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId++, notification)
    }

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

        fun createService() {
            if (isCreated) return

            val serviceIntent = Intent(App.INSTANCE, UpdatesService::class.java)
            App.INSTANCE.startService(serviceIntent)

            isCreated = true
        }

        fun bindService(connection: ServiceConnection) {
            if (!isCreated)
                createService()

            val serviceIntent = Intent(App.INSTANCE, UpdatesService::class.java)
            App.INSTANCE.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        }

        fun unbindService(connection: ServiceConnection) {
            try {
                App.INSTANCE.unbindService(connection)
            }
            catch (e: IllegalArgumentException) {}
        }

        private var notificationId = 0
    }
}