package com.example.simplechat.services.updates.websocket

import android.content.Context
import com.example.simplechat.R
import com.example.simplechat.core.network.di.NetworkModule
import com.example.simplechat.core.preference.UserPreferenceStorage
import com.example.simplechat.services.updates.models.UpdateNet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neovisionaries.ws.client.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject

typealias OnNewUpdatesListener = (List<UpdateNet>) -> Unit

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

    var isConnected: Boolean = false
        private set

    override fun onTextMessage(websocket: WebSocket, text: String) {
        super.onTextMessage(websocket, text)

        try {
            val listType = object: TypeToken<List<UpdateNet>>(){}.type
            val updates: List<UpdateNet> = gson.fromJson(text, listType)

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
        isConnected = true

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
        isConnected = false

        updatesConnectionListener?.onClosed(context.getString(R.string.error_connection_closed))
    }

    fun start() {
        webSocket = WebSocketFactory()
            .createSocket("ws://${NetworkModule.BASE_ADDRESS}/getUpdates?token=${userPreferenceStorage.token}", 5000)

        webSocket?.addListener(this)

        try {
            webSocket?.connect()
        } catch (e: WebSocketException) {
            updatesConnectionListener?.onClosed(context.getString(R.string.error_connecting_error), whenConnecting = true)
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

        fun onNewUpdates(updateNets: List<UpdateNet>)

        fun onClosed(text: String, whenConnecting: Boolean=false)
    }
}