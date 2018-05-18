package com.cy.obdproject.socket

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft_17
import org.json.JSONObject
import java.net.URI


class WebSocketService : Service() {

    private var msgClient: MyWebSocketClient? = null

    companion object {

        private var webSocketServie: WebSocketService? = null

        fun getIntance(): WebSocketService? {
            return webSocketServie
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.e("cyf", "WebSocketServie onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("cyf", "WebSocketServie 开始服务")
        webSocketServie = this
        createWebSocket()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("cyf", "WebSocketServie onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("cyf", "WebSocketServie onDestroy")
//        if ((application as MyApp).userType == Constant.userNormal) {
//            // 用户退出
//            sendMsg("{\"S\":\"user1\",\"R\":\"user\",\"C\":\"K\",\"D\":\"\"}")
//        } else {
//            // 专家退出
//            sendMsg("{\"S\":\"zuser\",\"R\":\"user\",\"C\":\"K\",\"D\":\"\"}")
//        }
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
        msgClient = object : MyWebSocketClient(URI(Urls.ws_url), Draft_17(), map, 12000) {

            override fun onMessage(message: String?) {
                super.onMessage(message)
                Log.e("cyf", message)
                val webSocketBean = Gson().fromJson(message, WebSocketBean::class.java)
                if (webSocketBean.c == "D") {// 专家操作或普通用户反馈指令的透传
                    if (webSocketBean.d.length == 1) {
                        if (webSocketBean.d == "S") {
                            // 透传发送成功
                        } else {
                            // 透传发送失败
                        }
                    } else {
                        val jsonObject = JSONObject(webSocketBean.d.toString())
                        val activityName = jsonObject.opt("activity").toString()
                        if (WebSocketService.getIntance() != null && (application as MyApp).userType == Constant.userProfessional) {
                            // 用户端传过来的信息处理显示数据的事件
                            val data = jsonObject.opt("data").toString()
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains(activityName)) {
                                    ((application as MyApp).activityList[i] as BaseActivity).setData(data)
                                    break
                                }
                            }
                        }
                        if (WebSocketService.getIntance() != null && (application as MyApp).userType == Constant.userNormal) {
                            // 专家端传来的信息处理点击事件
                            val tag = jsonObject.opt("tag").toString()
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains(activityName)) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        ((application as MyApp).activityList[i] as BaseActivity.ClickMethoListener).doMethod(tag)
                                    }
                                    break
                                }
                            }
                        }
                    }
                } else if (webSocketBean.c == "L") {//登录
                    if (webSocketBean.d.length == 1) {
                        if (webSocketBean.d == "S") {
                            // 登录成功
                        } else {
                            // 登录失败
                        }
                    }
                } else if (webSocketBean.c == "C") {//专家同意普通用户请求
                    if (webSocketBean.d.length == 1) {
                        if (webSocketBean.d == "S") {
                            // 专家同意普通用户请求成功
                        } else {
                            // 专家同意普通用户请求失败
                        }
                    }
                }
            }
        }
        msgClient!!.connect()
        // 开始连接
        // -----------------------------后期可优化---------------------------------
        Handler().postDelayed({
            if ((application as MyApp).userType == Constant.userNormal) {
                // 用户登录
                sendMsg("{\"S\":\"" + SPTools[this, Constant.USERID, ""].toString()
                        + "\",\"R\":\"\",\"C\":\"L\",\"D\":{\"T\":\"N\",\"P\":\"pwd\"}}")
            } else {
                // 专家同意普通用户请求
                sendMsg("{\"S\":\"" + SPTools[this, Constant.USERID, ""].toString()
                        + "\",\"R\":\"user1\",\"C\":\"C\",\"D\":\"user1\"}")
            }
        }, 3000)
        Log.e("cyf", "WebSocketServie 开始连接")
    }

    fun sendMsg(msg: String) {
        Log.e("cyf", "WebSocketServie msg : $msg")
        if (msgClient != null && msgClient!!.readyState == WebSocket.READYSTATE.OPEN) {
            msgClient!!.sendMsg(msg)
        }
    }

    fun isConnected(): Boolean {
        if (msgClient != null && msgClient!!.readyState == WebSocket.READYSTATE.OPEN) {
            return true
        }
        return false
    }

}
