package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.adapter.ErrorCodeAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.ErrorCodeBean
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.LogTools
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_error_code.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.util.*


class ErrorCodeActivity : BaseActivity(), BaseActivity.ClickMethoListener, ErrorCodeAdapter.OnErrorCodeClick {

    private var code = ""

    private var list: ArrayList<ErrorCodeBean>? = null
    private var adapter: ErrorCodeAdapter? = null
    // private var errorCodeWorker: ErrorCodeWorker? = null
    // private var errorCodeClearWorker: ErrorCodeClearWorker? = null

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_code)
        initView()
        initData(code.split(",")[0])
    }

    private fun initData(mCode: String) {
        if (isProfessionalConnected) {// 专家连接
            showDissWait()
            doMethod("tv_refresh")
        } else {
            showProgressDialog()
            if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                handler = @SuppressLint("HandlerLeak")
                object : Handler() {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        myApp.publicUnit.setBreak(true)
                        setData("toast" + msg.obj.toString())
                    }
                }
                try {
                    myApp.publicUnit.setMessageHandler(@SuppressLint("HandlerLeak")
                    object : Handler() {
                        override fun handleMessage(msg: Message) {
                            when (msg.what) {
                                0 -> {
                                    val data = msg.obj.toString()
                                    // toast(data)
                                    setData("toast2$data")
                                }
                                2 -> {
                                    val data = msg.obj.toString()
                                    setData(data)
                                }
                            }
                            super.handleMessage(msg)
                        }
                    })
                    myApp.publicUnit.SetEvent(handler, mCode)

                    Thread {
                        while (true) {
                            val isRun = myApp.publicUnit.GetScriptIsRun(mCode)
                            if (!isRun) {
                                runOnUiThread {
                                    // 完成
                                    dismissProgressDialog()
                                }
                                break
                            }
                        }
                    }.start()
                } catch (e: Exception) {
                    LogTools.errLog(e)
                }
            } else {
                list!!.clear()
                setData(Gson().toJson(list))
            }
        }
    }

    private fun initView() {
        code = intent.getStringExtra("code")
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
        setClickMethod(btn_clean)
        list = ArrayList()
    }

    override fun setData(data: String?) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            if (data!!.startsWith("toast")) {
                // Log.i("cyf", "e : ${e.message}")
                toast(data!!.replace("toast2", "").replace("toast", ""))
                if ("清空数据成功" == data) {
                    list!!.clear()
                    tv_msg.visibility = View.VISIBLE
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                if (!data!!.startsWith("toast2")) {
                    dismissProgressDialog()
                }
                try {
                    list!!.clear()
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val iterator = jsonArray.getJSONObject(i).keys()
                        while (iterator.hasNext()) {
                            val key = iterator.next() + ""
                            if (key != "data") {
                                list!!.add(ErrorCodeBean(key, jsonArray.getJSONObject(i).optString(key), jsonArray.getJSONObject(i).optString("data")))
                            }
                        }
                    }
                    if (adapter == null) {
                        adapter = ErrorCodeAdapter(list!!, this)
                        listView!!.adapter = adapter
                    } else {
                        adapter!!.notifyDataSetChanged()
                    }
                    adapter!!.setOnErrorCodeClick(this)
                    if (list!!.size == 0) {
                        tv_msg.visibility = View.VISIBLE
                    } else {
                        tv_msg.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    LogTools.errLog(e)
                }
            }
            super.setData(data)
        }
    }

    override fun setOnErrorCodeClick(id: String?, position: Int) {
        sendClick(this@ErrorCodeActivity.localClassName, "" + position)
        val mIntent = Intent(this, ErrorCode2Activity::class.java)
        mIntent.putExtra("data", list!![position].data)
        mIntent.putExtra("code", code)
        startActivity(mIntent)
        // toast("" + position)
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_refresh" -> {
                if (!isProfessionalConnected) {// 专家连接
                    initData(code.split(",")[1])
                } else {
                    showDissWait()
                }
            }
            "btn_clean" -> {
                showProgressDialog()
                if (!isProfessionalConnected) {// 专家连接
                    handler = @SuppressLint("HandlerLeak")
                    object : Handler() {
                        override fun handleMessage(msg: Message) {
                            super.handleMessage(msg)
                            myApp.publicUnit.setBreak(true)
                            setData("toast" + msg.obj.toString())
                        }
                    }
                    try {
                        myApp.publicUnit.setMessageHandler(@SuppressLint("HandlerLeak")
                        object : Handler() {
                            override fun handleMessage(msg: Message) {
                                when (msg.what) {
                                    0 -> {
                                        val data = msg.obj.toString()
                                        // toast(data)
                                        setData("toast2$data")
                                    }
                                    2 -> {
                                        val data = msg.obj.toString()
                                        setData(data)
                                    }
                                }
                                super.handleMessage(msg)
                            }
                        })
                        myApp.publicUnit.SetEvent(handler, code.split(",")[3])

                        Thread {
                            while (true) {
                                val isRun = myApp.publicUnit.GetScriptIsRun(code.split(",")[3])
                                if (!isRun) {
                                    runOnUiThread {
                                        // 完成
                                        dismissProgressDialog()
                                    }
                                    break
                                }
                            }
                        }.start()
                    } catch (e: Exception) {
                        LogTools.errLog(e)
                    }
                }
            }
            else -> {
                setOnErrorCodeClick(string, string!!.toInt())
            }

        }
    }


}
