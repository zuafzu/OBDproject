package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_car_type.*

class SelectCarTypeActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<String>? = null
    private var adapter: SelectAdapter? = null

    companion object {
        var INSTANCE: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_type)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)
        INSTANCE = this

        list = ArrayList()
        list!!.add("红旗EV")
        list!!.add("红旗HS5")

        if (adapter == null) {
            adapter = SelectAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, id ->
            if (position == 0) {
                SPTools.put(this@SelectCarTypeActivity, Constant.CARTYPE, "1")
            } else if (position == 1) {
                SPTools.put(this@SelectCarTypeActivity, Constant.CARTYPE, "2")
            }
            SPTools.put(this@SelectCarTypeActivity, Constant.CARNAME, list!![position])


            startActivity(Intent(this@SelectCarTypeActivity, SelectSystemActivity::class.java))
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
