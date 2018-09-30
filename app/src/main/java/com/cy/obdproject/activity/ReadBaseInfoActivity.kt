package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.tools.LogTools
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_read_base_info.*
import org.jetbrains.anko.toast
import org.json.JSONObject

class ReadBaseInfoActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var code = ""

    private var list: ArrayList<BaseInfoBean>? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_base_info)
        initView()
        initData(code.split(",")[0])
    }

    private fun initView() {
        code = intent.getStringExtra("code")
        list = ArrayList()
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
    }

    private fun initData(mCode: String) {
        if (isProfessionalConnected) {// 专家连接
            showDissWait()
            doMethod("tv_refresh")
        } else {
            showProgressDialog()
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
                            3 -> {
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
        }
    }

    override fun setData(data: String) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            if(data.startsWith("toast")){
                toast(data!!.replace("toast", ""))
                dismissProgressDialog()
            }else{
                try {
                    val mlist = Gson().fromJson<List<BaseInfoBean>>(data, object : TypeToken<ArrayList<BaseInfoBean>>() {}.type) as ArrayList<BaseInfoBean>?
                    list!!.clear()
                    list!!.addAll(mlist!!)
                    if (baseInfoAdapter == null) {
                        baseInfoAdapter = BaseInfoAdapter(list!!, this@ReadBaseInfoActivity, 1)
                        listView!!.adapter = baseInfoAdapter
                    } else {
                        baseInfoAdapter!!.notifyDataSetChanged()
                    }
                    dismissProgressDialog()
                } catch (e: Exception) {
                    Log.i("cyf", "e : ${e.message}")
                    try {
                        val jo = JSONObject(data)
                        val ja = jo.optJSONArray("List")
                        list!!.clear()
                        for (i in 0 until ja.length()) {
                            val jo1 = ja.optJSONObject(i)
                            val Name = jo1.optString("Name")
                            if (jo1.has("Value")) {
                                list!!.add(BaseInfoBean("", Name, "", "", "", jo1.optString("Value")))
                            } else {
                                list!!.add(BaseInfoBean("", Name, "", "", "", ""))
                            }
                        }
                        if (baseInfoAdapter == null) {
                            baseInfoAdapter = BaseInfoAdapter(list!!, this@ReadBaseInfoActivity, 1)
                            listView!!.adapter = baseInfoAdapter
                        } else {
                            baseInfoAdapter!!.notifyDataSetChanged()
                        }
                        if (list!!.size == 0 || list!![0].value != "") {
                            dismissProgressDialog()
                        }
                    } catch (e: Exception) {
                        LogTools.errLog(e)
                    }
                }
            }
            super.setData(data)
        }
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
        }
    }

}
