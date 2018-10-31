package com.cy.obdproject.socket

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.AsyncTask
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.cy.obdproject.activity.LoginActivity
import com.cy.obdproject.activity.MainActivity
import com.cy.obdproject.activity.RequestListActivity
import com.cy.obdproject.activity.ResponseListActivity
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.NetworkUtil
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
import java.net.URI


class WebSocketService : Service() {

    private var msgClient: MyWebSocketClient? = null
    private var sendTime = 0L

    companion object {

        private var webSocketServie: WebSocketService? = null

        fun getIntance(): WebSocketService? {
            return webSocketServie
        }

        var spaceTime = 5 * 1000L
        var outTime = 5 * 1000L
        var state = 0//0初始状态，1登录状态，2呼叫成功状态
    }

    private var isFinalHeart = true//是否是隔固定的时间发送心跳，而不是参杂着透传指令

    private var handler = Handler()
    private var runnableTimeOut = Runnable {
        // 返回数据超时
        if (msgClient != null) {
            msgClient!!.close("1111")
        }
        val i = (application as MyApp).activityList.size - 1
        if (i >= 0) {
            ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                ((application as MyApp).activityList[i] as BaseActivity).showWebSocketStopDialog("服务器返回数据超时，已断开连接。")
                handler.removeCallbacks(runnableHeart)
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
    }
    private var runnableHeart = Runnable {
        if (isFinalHeart) {
            if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                // 用户
                if (state == 2) {
                    sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                            "\",\"R\":\"" + SPTools[this@WebSocketService, Constant.ZFORUID, ""]!!.toString() +
                            "\",\"C\":\"T\",\"D\":\"s\",\"E\":\"\"}")
                } else {
                    sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                            "\",\"R\":\"" + "" +
                            "\",\"C\":\"T\",\"D\":\"s\",\"E\":\"\"}")
                }
            } else {
                // 专家
                if (state == 2) {
                    sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                            "\",\"R\":\"" + SPTools[this@WebSocketService, Constant.ZFORUID, ""]!!.toString() +
                            "\",\"C\":\"T\",\"D\":\"z\",\"E\":\"\"}")
                } else {
                    sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                            "\",\"R\":\"" + "" +
                            "\",\"C\":\"T\",\"D\":\"z\",\"E\":\"\"}")
                }
            }
            sendHeart()
        } else {
            if (sendTime == 0L) {
                if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                    // 用户
                    if (state == 2) {
                        sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                                "\",\"R\":\"" + SPTools[this@WebSocketService, Constant.ZFORUID, ""]!!.toString() +
                                "\",\"C\":\"T\",\"D\":\"s\",\"E\":\"\"}")
                    } else {
                        sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                                "\",\"R\":\"" + "" +
                                "\",\"C\":\"T\",\"D\":\"s\",\"E\":\"\"}")
                    }
                } else {
                    // 专家
                    if (state == 2) {
                        sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                                "\",\"R\":\"" + SPTools[this@WebSocketService, Constant.ZFORUID, ""]!!.toString() +
                                "\",\"C\":\"T\",\"D\":\"z\",\"E\":\"\"}")
                    } else {
                        sendMsg("{\"S\":\"" + SPTools[this@WebSocketService, Constant.USERID, ""]!!.toString() +
                                "\",\"R\":\"" + "" +
                                "\",\"C\":\"T\",\"D\":\"z\",\"E\":\"\"}")
                    }
                }
            }
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
            msgClient!!.close("22222")
        }
        handler.removeCallbacks(runnableTimeOut)
        handler.removeCallbacks(runnableHeart)
        state = 0
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
                // 移除超时
                handler.removeCallbacks(runnableTimeOut)
                // 开始循环心跳
                if (!isFinalHeart) {
                    sendHeart()
                }
                // 处理返回结果
                val webSocketBean = Gson().fromJson(message, WebSocketBean::class.java)
                when {
                    webSocketBean.c == "D" -> // 专家操作或普通用户反馈指令的透传
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
                    webSocketBean.c == "L" -> //登录
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
                            state = 1
                            // 开始心跳
                            sendHeart()
                            // 处理返回数据
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
                                    if ((application as MyApp).activityList[i].localClassName.contains("SelectRoleActivity")) {
                                        ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                            (application as MyApp).activityList[i].finish()
                                        }
                                        break
                                    }
                                }
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
                    webSocketBean.c == "C" -> {//呼叫
                        if (webSocketBean.e != "0" && webSocketBean.e != "3") {
                            // 呼叫失败
                            if (SPTools[this@WebSocketService, Constant.USERTYPE, 0] == Constant.userNormal) {
                                // 用户
                                for (i in 0 until (application as MyApp).activityList.size) {
                                    if ((application as MyApp).activityList[i].localClassName.contains("ResponseListActivity")) {
                                        ((application as MyApp).activityList[i] as ResponseListActivity).runOnUiThread {
                                            ((application as MyApp).activityList[i] as ResponseListActivity).dismissProgressDialog()
                                            toast("申请协助失败")
                                            ((application as MyApp).activityList[i] as ResponseListActivity).net_login()
                                        }
                                        break
                                    }
                                }
                            } else {
                                // 专家
                                for (i in 0 until (application as MyApp).activityList.size) {
                                    if ((application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                        ((application as MyApp).activityList[i] as RequestListActivity).runOnUiThread {
                                            ((application as MyApp).activityList[i] as RequestListActivity).dismissProgressDialog()
                                            toast("连接失败")
                                            ((application as MyApp).activityList[i] as RequestListActivity).net_requestList(false)
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
                                                state = 2
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
                                                state = 2
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
                    }
                    webSocketBean.c == "K" ->
                        if (webSocketBean.s != "" || webSocketBean.d != "") {
                            if (SPTools[this@WebSocketService, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
                                // 专家端
                                for (i in 0 until (application as MyApp).activityList.size) {
                                    if (!(application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                        // ((application as MyApp).activityList[i] as Activity).finish()
                                    } else {
                                        ((application as MyApp).activityList[i] as RequestListActivity).net_requestList(false)
                                    }
                                    // activity最后一个是不是RequestListActivity，如果不是弹Toast,否则不弹
                                    if (i == (application as MyApp).activityList.size - 1 &&
                                            !(application as MyApp).activityList[i].localClassName.contains("RequestListActivity")) {
                                        ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                            ((application as MyApp).activityList[i] as BaseActivity).showWebSocketStopDialog("协助已断开。")
                                        }
                                    }
                                }
                                state = 1
                            } else {
                                if (state == 2) {
                                    for (i in 0 until (application as MyApp).activityList.size) {
                                        if ((application as MyApp).activityList[i].localClassName.contains("MainActivity")) {
                                            ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                                                ((application as MyApp).activityList[(application as MyApp).activityList.size - 1] as BaseActivity).showWebSocketStopDialog("协助已断开。")
                                            }
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    webSocketBean.c == "T" -> // 判断对方是否在线
                    {
//                        if (webSocketBean.e == "0") {
//                            // 正常
//
//                        } else {
//                            // 对方不在线
//                            val i = (application as MyApp).activityList.size - 1
//                            if (i >= 0) {
//                                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
//                                    ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
//                                    ((application as MyApp).activityList[i] as BaseActivity).showWebSocketStopDialog("由于对方掉线，已断开连接。")
//                                    handler.removeCallbacks(runnableHeart)
//                                }
//                            }
//                        }
                    }
                }
            }
        }
        // 开始连接
        if (!msgClient!!.isOpen) {
            WebSocketConfig.wssConfig(msgClient!!)
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

    fun sendMsg(msg: String) {
        Log.i("cyf", "WebSocketServie2 发送 : $msg")
        try {
            if (NetworkUtil.isNetworkAvailable(this) && msgClient != null && msgClient!!.readyState == WebSocket.READYSTATE.OPEN) {
                msgClient!!.sendMsg(msg)
                sendTime = System.currentTimeMillis()
                // 检测超时
                handler.postDelayed(runnableTimeOut, outTime)
            } else {
                // 自身webSocket断开
                val i = (application as MyApp).activityList.size - 1
                if (i >= 0) {
                    if (state != 0) {
                        ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                            ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                            ((application as MyApp).activityList[i] as BaseActivity).showWebSocketStopDialog("网络连接中断，已断开连接。")
                            handler.removeCallbacks(runnableHeart)
                            handler.removeCallbacks(runnableTimeOut)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LogTools.errLog(e)
        }
    }

    fun sendHeart() {
        if (state != 0) {
            Log.i("cyf78", "WebSocketServie 心跳")
            // 心跳检测
            // 判断是专家端还是用户端
            sendTime = 0
            handler.removeCallbacks(runnableHeart)
            handler.postDelayed(runnableHeart, spaceTime)
            handler.postDelayed(runnableTimeOut, outTime)
        }
    }

    fun onErr() {
        // 没连接上
        for (i in 0 until (application as MyApp).activityList.size) {
            if (i == (application as MyApp).activityList.size - 1 && SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
                ((application as MyApp).activityList[i] as BaseActivity).runOnUiThread {
                    ((application as MyApp).activityList[i] as BaseActivity).dismissProgressDialog()
                    ((application as MyApp).activityList[i] as BaseActivity).showWebSocketStopDialog("远程连接建立失败。")
                    handler.removeCallbacks(runnableHeart)
                    handler.removeCallbacks(runnableTimeOut)
                }
                break
            }
        }
    }

    fun close() {
        if (msgClient != null) {
            msgClient!!.close("55555")
        }
        state = 0
        stopSelf()
    }

    fun isConnected(): Boolean {
        if (msgClient != null && msgClient!!.readyState == WebSocket.READYSTATE.OPEN) {
            return true
        }
        return false
    }

}
