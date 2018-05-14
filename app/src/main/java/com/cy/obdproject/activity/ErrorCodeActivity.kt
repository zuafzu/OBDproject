package com.cy.obdproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.cy.obdproject.R
import com.cy.obdproject.adapter.ErrorCodeAdapter
import com.cy.obdproject.bean.ErrorCodeBean
import kotlinx.android.synthetic.main.activity_error_code.*

class ErrorCodeActivity : AppCompatActivity() , View.OnClickListener{
    private var list: ArrayList<ErrorCodeBean>? = null
    private var adapter: ErrorCodeAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_code)
        initView()
        initData()
    }

    private fun initView() {

    }

    private fun initData() {
        list = ArrayList()
        for (i in 0 until 10){

            var bean = ErrorCodeBean("水温","65摄氏度")
            list!!.add(bean)
        }

        if (adapter == null) {
            adapter = ErrorCodeAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            iv_back.id -> {
                finish()
            }
            tv_refresh.id -> {

            }
        }
    }
}
