package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.RequestBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
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
        for (i in 1 until 4) {
            var bean = RequestBean(""+i, "请求协助$i")
            list!!.add(bean)
        }

        var bean = RequestBean("1", "请求协助1")
        list!!.add(bean)

        if (adapter == null) {
            adapter = SelectRequestAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView!!.setOnItemClickListener { _, _, position, id ->
            SPTools[this@RequestListActivity,Constant.ZFORUID,""+list!![position].id]
            startActivity(Intent(this@RequestListActivity,MainActivity::class.java))
            finish()
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
