package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_role.*

class SelectRoleActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list = ArrayList<String>()
    private var adapter: SelectAdapter? = null

    companion object {
        var INSTANCE: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_role)
        initView()
    }

    private fun initView() {
        INSTANCE = this

        setClickMethod(iv_back)

        if (intent.hasExtra("userType")) {
            var userType = intent.getStringExtra("userType")
            list.addAll(userType.toLowerCase().replace("s", "受控端").replace("z", "专家端").split(","))
        }

        if (adapter == null) {
            adapter = SelectAdapter(list, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, id ->
            if ("受控端" == list!![position].toString()) {
                SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userNormal)
                startActivity(Intent(this@SelectRoleActivity, SelectCarTypeActivity::class.java))
            } else if ("专家端" == list[position].toString()) {
                SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userProfessional)
                startActivity(Intent(this@SelectRoleActivity, RequestListActivity::class.java))
            }

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
