package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.RequestBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_response_list.*

class ResponseListActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var mIntent1: Intent? = null
    private var list: ArrayList<RequestBean>? = null
    private var adapter: SelectRequestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response_list)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)

        mIntent1 = Intent(this, WebSocketService::class.java)
        list = ArrayList()

        val bean = RequestBean("1", "专家1")
        list!!.add(bean)

        if (adapter == null) {
            adapter = SelectRequestAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView!!.setOnItemClickListener { _, _, position, _ ->
            SPTools.put(this, Constant.ZFORUID, "" + list!![position].id)
            startService(mIntent1)
            showProgressDialog()
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
