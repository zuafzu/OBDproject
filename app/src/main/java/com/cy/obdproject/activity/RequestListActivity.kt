package com.cy.obdproject.activity

import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
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
        setClickMethod(iv_back)

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
        }
    }
}
