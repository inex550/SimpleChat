package com.example.simplechat.services.updates.service

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.transform.CircleCropTransformation
import com.example.simplechat.App
import com.example.simplechat.R
import com.example.simplechat.core.preference.UserPreferenceStorage
import com.example.simplechat.core.ui.base.BaseService
import com.example.simplechat.core.ui.notification.NotificationChannelManager
import com.example.simplechat.screens.chat.domain.models.Message
import com.example.simplechat.screens.chats.domain.models.Chat
import com.example.simplechat.services.updates.models.Update
import com.example.simplechat.services.updates.models.UpdateNet
import com.example.simplechat.services.updates.models.UpdateType
import com.example.simplechat.services.updates.websocket.UpdatesWebSocket
import dagger.hilt.android.AndroidEntryPoint
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

        override fun onClosed(text: String, whenConnecting: Boolean) {
            if (INSTANCE == null) return

            if (!isReconnecting) {
                listener?.onClosed(text)
                isReconnecting = true
            }

            if (!whenConnecting)
                updatesWebSocket.start()
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    fun proceedUpdates(updates: List<Update>) {
        for (update in updates) {
            val listenerResult = listener?.onUpdate(update) ?: false

            if (!listenerResult && update.type == UpdateType.NEW) {
                if (update.message != null)
                    showNewMessageNotification(update.message)

                else if (update.chat != null)
                    showNewChatNotification(update.chat)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updatesWebSocket.setUpdatesConnectionListener(updatesConnectionListener)

        serviceScope.launch {
            updatesWebSocket.start()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun showNewMessageNotification(message: Message) {
        val notification = NotificationCompat.Builder(this, NotificationChannelManager.CHANNEL_ID)
            .setContentTitle(getString(R.string.message_label))
            .setContentText("${message.sender.username}: ${message.text}")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId++, notification)
    }

    private fun showNewChatNotification(chat: Chat) {
        val notification = NotificationCompat.Builder(this, NotificationChannelManager.CHANNEL_ID)
            .setContentTitle("Новый чат")
            .setContentText(
                if (chat.user == null)
                    "Вас добавили в ${chat.name}"
                else
                    "${chat.user.username} начал переписку с вами"
            )
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
        INSTANCE = null
//        updatesWebSocket.close()
        super.onDestroy()
    }

    companion object {

        var INSTANCE: UpdatesService? = null
            private set

        fun createService() {
            if (INSTANCE != null) return

            val serviceIntent = Intent(App.INSTANCE, UpdatesService::class.java)
            App.INSTANCE.startService(serviceIntent)
        }

        fun bindService(connection: ServiceConnection) {
            if (INSTANCE == null)
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

        fun stopService() {
            if (INSTANCE == null) return

            val serviceIntent = Intent(App.INSTANCE, UpdatesService::class.java)
            App.INSTANCE.stopService(serviceIntent)
        }

        private var notificationId = 0
    }
}