package com.example.user

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.concurrent.schedule


open class WsClient(uri: URI) : WebSocketClient(uri) {
    companion object{
        /**
         * server remote ip address and port for WebSocket
         */
        val serverRemote = URI("ws://3.143.243.86:8889")

        /**
         * ping ID that is used in request
         */
        const val pingID: Int = 987

        /**
         * value of normal closure
         */
        const val NORMAL_CLOSURE: Int = 1000
    }

    /**
     * the connection is closed or not. init by false
     */
    private var CLOSE_STATE: Boolean = false

    /**
     * raw string of ping request
     */
    private val pingReq = """{"jsonrpc": 2.0, "id": $pingID, "method": "ping", "params": {}}"""

    /**
     * duration of sending ping request. represented in milli second. default = 10sec
     */
    private val pingDuration: Long = 10000

    /**
     * TimerTask of ping request. this task will send ping request to server every pingDuration second
     */
    private var sendPing: TimerTask.() -> Unit = {
        throwPing()
    }

    /**
     * send ping if the connection is alive
     */
    private fun throwPing(){
        if(!this.CLOSE_STATE){
            this.send(pingReq)
            Log.i(javaClass.simpleName, "sent ping req")
        }
    }

    /**
     * process when the connection open.
     * when connection open, start ping TimerTask
     */
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i(javaClass.simpleName, "connected to ws server")
        Log.i(javaClass.simpleName, "executing on ${Thread.currentThread()}")
        Timer().schedule(0, this.pingDuration, this.sendPing)
        this.CLOSE_STATE = false
    }

    /**
     * process when the connection close.
     * if the <a href = "https://developer.mozilla.org/ja/docs/Web/API/CloseEvent">closeEvent</a>
     * is not Normal Closure(1000), try reconnect.
     * else, just close connection.
     */
    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i(javaClass.simpleName, "connection closed")
        Log.i(javaClass.simpleName, "executing on ${Thread.currentThread()}")
        if(code != NORMAL_CLOSURE) {
            Log.i(javaClass.simpleName, "connection closed illegally")
        }else{
            Log.i(javaClass.simpleName, "connection closed successfully")
        }
        this.CLOSE_STATE = true
    }

    /**
     * process when the error occurred
     */
    override fun onError(ex: Exception?) {
        Log.i(javaClass.simpleName, "error occurred")
        Log.i(javaClass.simpleName, ex.toString())
        Log.i(javaClass.simpleName, "executing on ${Thread.currentThread()}")
    }

    /**
     * process when the message arrived.
     * individual classes that is implemented in each page, override this onMessage and define
     * process what you want to do.
     */
    override fun onMessage(message: String?) {
        Log.i(javaClass.simpleName, "message arrived")
        Log.i(javaClass.simpleName, "executing on ${Thread.currentThread()}")
    }
}