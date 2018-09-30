package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.LoginBean
import com.cy.obdproject.bean.RequestBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_response_list.*
import org.jetbrains.anko.toast
import kotlin.collections.set


// 专家列表
class ResponseListActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var mAlertDialog: AlertDialog? = null
    private var mIntent1: Intent? = null
    private var requestList: ArrayList<RequestBean>? = null
    private var adapter: SelectRequestAdapter? = null
    private val waitTimeMax = 15
    private var waitTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response_list)
        initView()
        net_login()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_notice).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_notice).text = "暂无可协助的专家"
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
        mIntent1 = Intent(this, WebSocketService::class.java)
        requestList = ArrayList()
        listView!!.setOnItemClickListener { _, _, position, _ ->
            SPTools.put(this, Constant.ZFORUID, "" + requestList!![position].requestId)
            startService(mIntent1)
            showProgressDialog()
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_refresh" -> {
                if (!isProfessionalConnected) {
                    net_login()
                } else {
                    showDissWait()
                }
            }
        }
    }

    fun showWaitDialog(isShow: Boolean) {
        dismissProgressDialog()
        if (isShow) {
            waitTime = waitTimeMax
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = AlertDialog.Builder(this).setTitle("提示").setMessage("等待专家应答(${waitTime}s)......").setCancelable(false).setPositiveButton("取消等待") { _, _ ->
                closeWait()
            }.show()
            setWaitMessage()
        } else {
            // 返回主页面
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
                finish()
            }
        }
    }

    private fun setWaitMessage() {
        Handler().postDelayed({
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                if (waitTime == 0) {
                    waitTime = waitTimeMax
                    closeWait()
                } else {
                    mAlertDialog!!.setMessage("等待专家应答(${--waitTime}s)......")
                    setWaitMessage()
                }
            }
        }, 1000)
    }

    private fun closeWait() {
        if (mAlertDialog != null && mAlertDialog!!.isShowing) {
            stopService(mIntent1)
            mAlertDialog!!.dismiss()
            finish()
            overridePendingTransition(0, 0)
            startActivity(Intent(this@ResponseListActivity, ResponseListActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    fun net_login() {
        val map = hashMapOf<String, String>()
        map["username"] = SPTools[this, Constant.USERNAME, ""].toString()
        map["pwd"] = SPTools[this, Constant.PASSWORD, ""].toString()
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            map["loginType"] = "s"
        } else {
            map["loginType"] = "z"
        }
        NetTools.net(map, Urls().auth_login, this, { response ->
            val loginBean = Gson().fromJson(response.data, LoginBean::class.java)
            SPTools.put(this@ResponseListActivity, Constant.TOKEN, "" + loginBean.token)
            net_requestList()
        }, "正在加载...", true, false)
    }

    private fun net_requestList() {
        val map = hashMapOf<String, String>()
        map["requestUserType"] = "z"
        NetTools.net(map, Urls().requestList, this, { response ->
            if (response.code == "0") {
                val beans = Gson().fromJson<List<RequestBean>>(response.data, object : TypeToken<ArrayList<RequestBean>>() {}.type) as ArrayList<RequestBean>?
                requestList!!.clear()
                requestList!!.addAll(beans!!)
                if (requestList!!.size > 0) {
                    findViewById<TextView>(R.id.tv_notice).visibility = View.GONE
                } else {
                    findViewById<TextView>(R.id.tv_notice).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tv_notice).text = "暂无可协助的专家"
                }
                listView.visibility = View.VISIBLE
                if (adapter == null) {
                    adapter = SelectRequestAdapter(requestList!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                toast(response.msg!!)
            }
        }, "正在加载...", false, true)
    }

}
