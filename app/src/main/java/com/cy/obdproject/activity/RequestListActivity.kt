package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.RequestBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.SPTools
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_request_list.*

class RequestListActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<RequestBean>? = null
    private var adapter: SelectRequestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)
        initView()
    }

    private fun initView() {
        SPTools.put(this@RequestListActivity, Constant.ISLOGIN, "1")
        setClickMethod(iv_back)
        setClickMethod(iv_quit)

        list = ArrayList()

        val bean = RequestBean("1", "请求协助1")
        list!!.add(bean)

        if (adapter == null) {
            adapter = SelectRequestAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView!!.setOnItemClickListener { _, _, position, _ ->
            showProgressDialog()
            SPTools.put(this, Constant.ZFORUID, "" + list!![position].id)
            val webSocketBean = WebSocketBean()
            webSocketBean.s = SPTools[this, Constant.USERID, ""]!!.toString()
            webSocketBean.r = SPTools[this, Constant.ZFORUID, ""]!!.toString()
            webSocketBean.c = "C"
            WebSocketService.getIntance()!!.sendMsg(Gson().toJson(webSocketBean))
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "iv_quit" -> {
                AlertDialog.Builder(this).setTitle("提示").setMessage("确认退出当前账号？").setPositiveButton("确认") { _, _ ->
                    SPTools.clear(this@RequestListActivity)
                    for (i in 0 until (application as MyApp).activityList.size) {
                        (application as MyApp).activityList[i].finish()
                    }
                    startActivity(Intent(this@RequestListActivity, LoginActivity::class.java))
                }.setNegativeButton("取消") { _, _ -> }.show()
            }

        }
    }

    override fun onBackPressed() {
        finish()
    }

}
