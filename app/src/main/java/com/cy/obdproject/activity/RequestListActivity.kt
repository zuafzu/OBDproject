package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.RequestBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_request_list.*
import org.jetbrains.anko.toast

// 用户列表
class RequestListActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var mExitTime: Long = 0

    private var requestList: ArrayList<RequestBean>? = null
    private var adapter: SelectRequestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)
        initView()
    }

    override fun onResume() {
        super.onResume()
        net_requestList(true)
    }

    private fun initView() {
        SPTools.put(this@RequestListActivity, Constant.ISLOGIN, "1")
//        setClickMethod(iv_back)
//        setClickMethod(iv_quit)
        requestList = ArrayList()
        listView!!.setOnItemClickListener { _, _, position, _ ->
            showProgressDialog()
            SPTools.put(this, Constant.ZFORUID, "" + requestList!![position].requestId)
            val webSocketBean = WebSocketBean()
            webSocketBean.s = SPTools[this, Constant.USERID, ""]!!.toString()
            webSocketBean.r = SPTools[this, Constant.ZFORUID, ""]!!.toString()
            webSocketBean.c = "C"
            webSocketBean.d = "z"
            WebSocketService.getIntance()!!.sendMsg(Gson().toJson(webSocketBean))
        }
        iv_quit.visibility = View.VISIBLE
        iv_back.setOnClickListener {
            finish()
        }
        iv_quit.setOnClickListener {
            val mIntent = Intent(this@RequestListActivity, SettingActivity::class.java)
            startActivity(mIntent)
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "iv_quit" -> {
                val mIntent = Intent(this@RequestListActivity, SettingActivity::class.java)
                startActivity(mIntent)
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                toast("再按一次退出程序")
                mExitTime = System.currentTimeMillis()
            } else {
                if (WebSocketService.getIntance() != null) {
                    WebSocketService.getIntance()!!.stopSelf()
                }
                val ip = SPTools[this@RequestListActivity, Constant.IP, ""]
                val ModuleFile = SPTools[this, "ModuleFile", ""] as String
                val ControlFile = SPTools[this, "ControlFile", ""] as String
                val USERNAME = SPTools[this, Constant.USERNAME, ""] as String
                val PASSWORD = SPTools[this, Constant.PASSWORD, ""] as String
                SPTools.clear(this@RequestListActivity)
                SPTools.put(this@RequestListActivity, Constant.IP, ip!!)
                SPTools.put(this@RequestListActivity, "ModuleFile", ModuleFile)
                SPTools.put(this@RequestListActivity, "ControlFile", ControlFile)
                SPTools.put(this@RequestListActivity, Constant.USERNAME, USERNAME)
                SPTools.put(this@RequestListActivity, Constant.PASSWORD, PASSWORD)
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun net_requestList(isShow: Boolean) {
        val map = hashMapOf<String, String>()
        map["requestUserType"] = "s"
        NetTools.net(map, Urls().requestList, this, { response ->
            if (response.code == "0") {
                val beans = Gson().fromJson<List<RequestBean>>(response.data, object : TypeToken<ArrayList<RequestBean>>() {}.type) as ArrayList<RequestBean>?
                requestList!!.clear()
                requestList!!.addAll(beans!!)
                if (adapter == null) {
                    adapter = SelectRequestAdapter(requestList!!, this)
                    if (listView != null) {
                        listView!!.adapter = adapter
                    }
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                toast(response.msg!!)
            }
        }, "正在加载...", isShow, true)
    }

}
