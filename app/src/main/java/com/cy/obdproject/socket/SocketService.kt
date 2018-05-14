package com.cy.obdproject.socket

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.cy.obdproject.constant.Constant

class SocketService : Service() {

    private var msgClient: MySocketClient? = null

    companion object {

        private var socketServie: SocketService? = null

        fun getIntance(): SocketService? {
            return socketServie
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("cyf", "SocketService onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("cyf", "SocketService 开始服务")
        socketServie = this
        createSocket()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("cyf", "SocketService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("cyf", "SocketService onDestroy")
        socketServie = null
        if (msgClient != null) {
            msgClient!!.disconnect()
        }
    }

    /**
     * 创建socket连接
     */
    private fun createSocket() {
        msgClient = MySocketClient(Constant.mDstName, Constant.mDstPort)
        Thread { msgClient!!.connect() }.start()
        Log.e("cyf", "SocketService 开始连接")
    }

    fun sendMsg(msg: String, connectLinstener: MySocketClient.ConnectLinstener) {
        if (msgClient != null && msgClient!!.isConnected) {
            msgClient!!.setOnConnectLinstener(connectLinstener)
            msgClient!!.send(msg)
        }
    }

    fun sendMsg(data: ByteArray, connectLinstener: MySocketClient.ConnectLinstener) {
        if (msgClient != null && msgClient!!.isConnected) {
            msgClient!!.setOnConnectLinstener(connectLinstener)
            msgClient!!.send(data)
        }
    }

    fun isConnected(): Boolean {
        if (msgClient != null && msgClient!!.isConnected) {
            return true
        }
        return false
    }

}

