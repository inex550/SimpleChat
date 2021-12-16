package com.example.simplechat.services.updates.websocket

import android.content.Context
import com.example.simplechat.R
import com.example.simplechat.core.coreimpl.network.di.NetworkModule
import com.example.simplechat.core.coreapi.common.preference.UserPreferenceStorage
import com.example.simplechat.screens.chat.domain.models.Update
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neovisionaries.ws.client.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

typealias OnNewUpdatesListener = (List<Update>) -> Unit

class UpdatesWebSocket @Inject constructor(
    private val gson: Gson,
    private val userPreferenceStorage: UserPreferenceStorage,

    @ApplicationContext
    private val context: Context,
): WebSocketAdapter() {

    private var updatesConnectionListener: UpdatesConnectionListener? = null

    fun setUpdatesConnectionListener(listener: UpdatesConnectionListener) {
        updatesConnectionListener = listener
    }

    private var webSocket: WebSocket? = null

    val isOpen: Boolean get() = webSocket?.isOpen == true

    override fun onTextMessage(websocket: WebSocket, text: String) {
        super.onTextMessage(websocket, text)

        try {
            val listType = object: TypeToken<List<Update>>(){}.type
            val updates: List<Update> = gson.fromJson(text, listType)

            updatesConnectionListener?.onNewUpdates(updates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConnected(
        websocket: WebSocket?,
        headers: MutableMap<String, MutableList<String>>?
    ) {
        super.onConnected(websocket, headers)
        updatesConnectionListener?.onOpen()
    }

    override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
        super.onConnectError(websocket, exception)
        updatesConnectionListener?.onClosed(context.getString(R.string.error_connecting_error))
    }

    override fun onDisconnected(
        websocket: WebSocket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    ) {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)
        updatesConnectionListener?.onClosed(context.getString(R.string.error_connection_closed))
    }

    fun start() {
        webSocket = WebSocketFactory()
            .createSocket("ws://${NetworkModule.BASE_ADDRESS}/getUpdates?token=${userPreferenceStorage.token}", 5000)

        webSocket?.addListener(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                webSocket?.connect()
            } catch (e: WebSocketException) {
                updatesConnectionListener?.onClosed(context.getString(R.string.error_connecting_error))
            }
        }
    }

    fun close() {
        webSocket?.disconnect()
    }

    override fun onError(websocket: WebSocket, cause: WebSocketException) {
        super.onError(websocket, cause)
        cause.printStackTrace()
    }

    interface UpdatesConnectionListener {

        fun onOpen()

        fun onNewUpdates(updates: List<Update>)

        fun onClosed(text: String)
    }
}