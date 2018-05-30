package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_car_type.*
import org.jetbrains.anko.toast

class SelectCarTypeActivity : BaseActivity(), BaseActivity.ClickMethoListener,AdapterView.OnItemClickListener {

    private var mExitTime: Long = 0

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

        listView.onItemClickListener = this
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            iv_back.visibility = View.INVISIBLE
        } else {
            iv_back.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sendClick(this@SelectCarTypeActivity.localClassName, "" + p2)
        if (p2 == 0) {
            SPTools.put(this@SelectCarTypeActivity, Constant.CARTYPE, "1")
        } else if (p2 == 1) {
            SPTools.put(this@SelectCarTypeActivity, Constant.CARTYPE, "2")
        }
        SPTools.put(this@SelectCarTypeActivity, Constant.CARNAME, list!![p2])

        startActivity(Intent(this@SelectCarTypeActivity, SelectSystemActivity::class.java))
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {

            } else {
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    toast("再按一次退出程序")
                    mExitTime = System.currentTimeMillis()
                } else {
                    SPTools.clear(this@SelectCarTypeActivity)
                    finish()
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
