package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_system.*

class SelectSystemActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<String>? = null
    private var adapter: SelectAdapter? = null

    companion object {
        var INSTANCE: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_system)
        initView()
    }

    private fun initView() {
        INSTANCE = this

        setClickMethod(iv_back)
        list = ArrayList()
        list!!.add("整车控制系统")

        if (adapter == null) {
            adapter = SelectAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, id ->
            startActivity(Intent(this@SelectSystemActivity,MainActivity::class.java))
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
