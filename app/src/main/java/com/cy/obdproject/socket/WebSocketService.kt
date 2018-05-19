package com.cy.obdproject.socket

import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.cy.obdproject.activity.LoginActivity
import com.cy.obdproject.activity.MainActivity
import com.cy.obdproject.activity.RequestListActivity
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
        val webSocketBean = WebSocketBean()
        webSocketBean.s = SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString()
        webSocketBean.r = SPTools[this@WebSocketService, Constant.ZFORUID, ""]!!.toString()
        webSocketBean.c = "K"
        this@WebSocketService.sendMsg(Gson().toJson(webSocketBean))
        for (i in 0 until (application as MyApp).activityList.size) {
            if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                    ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                }
                break
            }
        }
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
                    if (webSocketBean.e != "0" && webSocketBean.e != "") {
                        // 透传发送失败

                    } else {
                        // 透传发送成功
                        if (webSocketBean.d.toString() == "") {

                        } else {
                            val jsonObject = JSONObject(webSocketBean.d.toString())
                            val activityName = jsonObject.opt("activity").toString()
                            if (WebSocketService.getIntance() != null && SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userProfessional) {
                                // 用户端传过来的信息处理显示数据的事件
                                val data = jsonObject.opt("data").toString()
                                val method = jsonObject.opt("method").toString()
                                for (i in 0 until (application as MyApp).activityList.size) {
                                    if ((application as MyApp).activityList[i].localClassName.contains(activityName)) {
                                        when (method) {
                                            "setData" -> ((application as MyApp).activityList[i] as BaseActivity).setData(data)
                                            "setData1" -> ((application as MyApp).activityList[i] as BaseActivity).setData1(data)
                                            "setData2" -> ((application as MyApp).activityList[i] as BaseActivity).setData2(data)
                                        }
                                        break
                                    }
                                }
                            }
                            if (WebSocketService.getIntance() != null && SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
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
                    }
                } else if (webSocketBean.c == "L") {//登录
                    if (webSocketBean.e != "0" && webSocketBean.e != "3") {
                        // 登录失败
                        for (i in 0 until (application as MyApp).activityList.size) {
                            if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                    ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                                }
                                break
                            }
                        }
                    } else {
                        // 登录成功
                        if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                            // 用户
                            val webSocketBean = WebSocketBean()
                            webSocketBean.s = SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString()
                            webSocketBean.r = "2"
                            webSocketBean.c = "C"
                            this@WebSocketService.sendMsg(Gson().toJson(webSocketBean))
                        } else {
                            // 专家
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("LoginActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        var mIntent = Intent(((application as MyApp).activityList[i] as LoginActivity),
                                                RequestListActivity::class.java)
                                        mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(mIntent)
                                        (application as MyApp).activityList[i].finish()
                                    }
                                    break
                                }
                            }
                        }
                    }
                } else if (webSocketBean.c == "C") {//呼叫
                    if (webSocketBean.e != "0" && webSocketBean.e != "3") {
                        // 呼叫失败
                        if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                            // 用户

                        } else {
                            // 专家
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        Toast.makeText(((application as MyApp).activityList[i] as BaseActivity),
                                                "连接失败", Toast.LENGTH_SHORT).show()
                                        ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                                    }
                                    break
                                }
                            }
                        }
                    } else {
                        // 呼叫成功
                        if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                            // 用户

                        } else {
                            // 专家
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        val mIntent = Intent(((application as MyApp).activityList[i] as RequestListActivity),
                                                MainActivity::class.java)
                                        mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(mIntent)
                                        ((application as MyApp).activityList[i] as RequestListActivity).finish()
                                    }
                                    break
                                }
                            }
                        }
                    }
                    for (i in 0 until (application as MyApp).activityList.size) {
                        if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                            ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                            }
                            break
                        }
                    }
                } else if (webSocketBean.c == "K") {
                    stopSelf()
                }
            }
        }
        msgClient!!.connect()
        // 开始连接
        // -----------------------------后期可优化---------------------------------
        Handler().postDelayed({
            if (msgClient!!.isOpen) {
                val webSocketBean = WebSocketBean()
                webSocketBean.s = SPTools[this, Constant.USERID, ""]!!.toString()// 自己（专家）id
                webSocketBean.r = ""
                webSocketBean.c = "L"
                sendMsg(Gson().toJson(webSocketBean))
            } else {
                // 没连接上
                for (i in 0 until (application as MyApp).activityList.size) {
                    if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                        ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                            Toast.makeText(((application as MyApp).activityList[i] as BaseActivity),
                                    "与OBD连接失败", Toast.LENGTH_SHORT).show()
                            ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                        }
                        break
                    }
                }
            }
        }, 3000)
        Log.e("cyf", "WebSocketServie 开始连接")
    }

    fun sendMsg(msg: String) {
        Log.i("cyf", "WebSocketServie 发送 : $msg")
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
