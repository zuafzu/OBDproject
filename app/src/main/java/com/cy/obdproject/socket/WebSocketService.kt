package com.cy.obdproject.socket

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.cy.obdproject.activity.LoginActivity
import com.cy.obdproject.activity.MainActivity
import com.cy.obdproject.activity.RequestListActivity
import com.cy.obdproject.activity.ResponseListActivity
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.StrZipUtil
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.Callback
import okhttp3.Call
import okhttp3.Response
import org.java_websocket.WebSocket
import org.java_websocket.drafts.Draft_17
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.lang.Exception
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

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("cyf", "WebSocketServie onStartCommand")
        webSocketServie = this
        createWebSocket()
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

                            val map = hashMapOf<String, String>()
                            map["id"] = webSocketBean.d.toString()
                            OkHttpUtils.postString().url(Urls().getMsg).addHeader(Constant.TOKEN, SPTools[this@WebSocketService, Constant.TOKEN, ""] as String?)
                                    .content(Gson().toJson(map)).build()
                                    .execute(object : Callback<BaseBean>() {

                                        override fun parseNetworkResponse(response: Response?, id: Int): BaseBean {
                                            val json = response!!.body().string()
                                            Log.e("cyf7", "response : $json")
                                            val jsonObject = JSONObject(json)
                                            val bean = BaseBean()
                                            bean.code = jsonObject.optString("code")
                                            bean.msg = jsonObject.optString("msg")
                                            val json2 = jsonObject.optString("data")
                                            if ("" != json2 && "{}" != json2 && "{ }" != json2) {
                                                bean.data = json2
                                            }
                                            return bean
                                        }

                                        @SuppressLint("StaticFieldLeak")
                                        override fun onResponse(response: BaseBean?, id: Int) {
                                            if (response != null && "0" == response.code) {
                                                val jo = JSONObject(response.data)
                                                val msg = jo.optString("msg")

                                                object : AsyncTask<String, Void, String>() {
                                                    override fun doInBackground(vararg p0: String?): String {
                                                        return StrZipUtil.uncompress(p0[0])
                                                    }

                                                    override fun onPostExecute(result: String?) {
                                                        super.onPostExecute(result)
                                                        val jsonObject = JSONObject(result)
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
                                                            val tag = jsonObject.opt("tag")
                                                            if (tag != null) {
                                                                for (i in 0 until (application as MyApp).activityList.size) {
                                                                    if ((application as MyApp).activityList[i].localClassName.contains(activityName)) {
                                                                        ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                                                            ((application as MyApp).activityList[i] as BaseActivity.ClickMethoListener).doMethod(tag.toString())
                                                                        }
                                                                        break
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                }.execute(msg)
                                            }
                                        }

                                        override fun onError(call: Call?, e: Exception?, id: Int) {

                                        }

                                    })
                        }
                    }
                } else if (webSocketBean.c == "L") {//登录
                    if (webSocketBean.e != "0" && webSocketBean.e != "3") {
                        // 登录失败
                        for (i in 0 until (application as MyApp).activityList.size) {
                            if ((application as MyApp).activityList[i].localClassName.contains("MainActivity") ||
                                    (application as MyApp).activityList[i].localClassName.contains("ResponseListActivity")) {
                                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                    ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                                }
                            }
                        }
                    } else {
                        // 登录成功
                        if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                            // 用户
                            val webSocketBean = WebSocketBean()
                            webSocketBean.s = SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString()
                            webSocketBean.r = SPTools[this@WebSocketService, Constant.ZFORUID, ""]!!.toString()
                            webSocketBean.c = "C"
                            webSocketBean.d = "s"
                            this@WebSocketService.sendMsg(Gson().toJson(webSocketBean))
                        } else {
                            // 专家
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("LoginActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        val mIntent = Intent(((application as MyApp).activityList[i] as LoginActivity),
                                                RequestListActivity::class.java)
                                        mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(mIntent)
                                        (application as MyApp).activityList[i].finish()
                                    }
                                    break
                                }
                            }
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if (!(application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                    (application as MyApp).activityList[i].finish()
                                }
                            }
                        }
                    }
                } else if (webSocketBean.c == "C") {//呼叫
                    if (webSocketBean.e != "0" && webSocketBean.e != "3") {
                        // 呼叫失败
                        if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                            // 用户
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("ResponseListActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        Toast.makeText(((application as MyApp).activityList[i] as BaseActivity),
                                                "申请协助失败", Toast.LENGTH_SHORT).show()
                                        ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                                    }
                                    break
                                }
                            }
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
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("ResponseListActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        if (webSocketBean.s == "") {
                                            // 弹等待窗
                                            ((application as MyApp).activityList[i] as ResponseListActivity).showWaitDialog(true)
                                        } else {
                                            // 结束等待窗
                                            ((application as MyApp).activityList[i] as ResponseListActivity).showWaitDialog(false)
                                        }
                                    }
                                    break
                                }
                            }
                        } else {
                            // 专家
                            for (i in 0 until (application as MyApp).activityList.size) {
                                if ((application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                        if (webSocketBean.s == "") {
                                            val mIntent = Intent(((application as MyApp).activityList[i] as RequestListActivity),
                                                    MainActivity::class.java)
                                            mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(mIntent)
                                            // ((application as MyApp).activityList[i] as RequestListActivity).finish()
                                        } else {
                                            ((application as MyApp).activityList[i] as RequestListActivity).net_requestList(false)
                                        }
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
                    for (i in 0 until (application as MyApp).activityList.size) {
                        if (SPTools[this@WebSocketService, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
                            if (!(application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                ((application as MyApp).activityList[i] as Activity).finish()
                            } else {
                                ((application as MyApp).activityList[i] as RequestListActivity).net_requestList(false)
                            }
                            if (i == (application as MyApp).activityList.size - 1) {
                                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                    toast("用户断开连接！")
                                }
                            }
                        } else {
                            if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                    toast("协助已断开！")
                                    val mIntent = Intent(((application as MyApp).activityList[i] as MainActivity),
                                            MainActivity::class.java)
                                    mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(mIntent)
                                    ((application as MyApp).activityList[i] as MainActivity).finish()
                                    if (msgClient != null) {
                                        msgClient!!.close()
                                    }
                                    stopSelf()
                                }
                                break
                            }
                        }
                    }
                }
            }
        }
        // 开始连接
        if (!msgClient!!.isOpen) {
            msgClient!!.connect()
        }
        Log.e("cyf", "WebSocketServie 开始连接")
    }

    fun startLogin() {
        val webSocketBean = WebSocketBean()
        webSocketBean.s = SPTools[this, Constant.USERID, ""]!!.toString()// 自己（专家）id
        webSocketBean.r = ""
        webSocketBean.c = "L"
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            webSocketBean.d = "s"
        } else {
            webSocketBean.d = "z"
        }
        sendMsg(Gson().toJson(webSocketBean))
    }

    fun onErr() {
        // 没连接上
        for (i in 0 until (application as MyApp).activityList.size) {
            if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
                if ((application as MyApp).activityList[i].localClassName.contains("LoginActivity")) {
                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                        Toast.makeText(((application as MyApp).activityList[i] as BaseActivity),
                                "登录失败", Toast.LENGTH_SHORT).show()
                        ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                    }
                    break
                }
            } else {
                if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                    ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                        Toast.makeText(((application as MyApp).activityList[i] as BaseActivity),
                                "连接失败", Toast.LENGTH_SHORT).show()
                        ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                    }
                    break
                }
            }
        }
    }

    fun close() {
        if (msgClient != null) {
            msgClient!!.close()
        }
        stopSelf()
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
