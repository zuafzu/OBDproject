package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_system.*

class SelectSystemActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

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
        if ("1" == SPTools[this@SelectSystemActivity, Constant.CARTYPE, ""]) {
            list!!.add("整车控制系统")

        } else if ("2" == SPTools[this@SelectSystemActivity, Constant.CARTYPE, ""]) {
            list!!.add("电动助力转向系统")
        }

        if (adapter == null) {
            adapter = SelectAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
        listView.onItemClickListener = this
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sendClick(this@SelectSystemActivity.localClassName, "" + p2)
        val name = SPTools[this@SelectSystemActivity, Constant.CARNAME, ""].toString()
        SPTools.put(this@SelectSystemActivity, Constant.CARNAME, name + " - " + list!![p2])
        startActivity(Intent(this@SelectSystemActivity, MainActivity::class.java))
        finish()
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }
}
