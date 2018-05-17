package com.cy.obdproject.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_car_type.*

class SelectCarTypeActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<String>? = null
    private var adapter: SelectAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_type)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)

        Log.e("zj", "usertype = " + SPTools[this@SelectCarTypeActivity, Constant.UserType, 0])

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
            startActivity(Intent(this@SelectCarTypeActivity, SelectSystemActivity::class.java))
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
