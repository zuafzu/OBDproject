package com.cy.obdproject.net

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.cy.obdproject.activity.MainTestActivity
import com.cy.obdproject.app.MyApp
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft_17
import java.net.URI

class WebSocketServie : Service() {

    var msgClient: WebSocketClient? = null

    companion object {

        private var webSocketServie: WebSocketServie? = null

        fun getIntance(): WebSocketServie? {
            return webSocketServie
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("cyf", "onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("cyf", "开始服务")
        webSocketServie = this
        createWebSocket()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("cyf", "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("cyf", "onDestroy")
        webSocketServie = null
        if (msgClient != null) {
            msgClient!!.close()
        }
    }

    /**
     * 创建websocket连接
     */
    private fun createWebSocket() {
        val map = HashMap<String, String>()
        msgClient = object : WebSocketClient(URI("ws://10.133.73.119:8883/websocket"), Draft_17(), map, 12000) {

            override fun onMessage(message: String?) {
                super.onMessage(message)
                Log.e("cyf", message)
                // 如果是专家端传过来的信息处理点击事件，如果是用户端传来的信息处理显示数据的事件
                for (i in 0 until (application as MyApp).activityList.size ){
                    if((application as MyApp).activityList[i].localClassName.contains(message!!.split(",")[0])){
                        ((application as MyApp).activityList[i] as MainTestActivity).doMethod(message!!.split(",")[1])
                        break
                    }
                }
            }
        }
        msgClient!!.connect()
        Log.e("cyf", "开始连接")
    }

    fun sendMsg(msg: String) {
        Log.e("cyf", "msg : $msg")
        if (msgClient != null && msgClient!!.readyState == WebSocket.READYSTATE.OPEN) {
            msgClient!!.sendMsg(msg)
        }
    }

}
