package com.cy.obdproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.bean.BaseInfoBean
import kotlinx.android.synthetic.main.activity_read_base_info.*

class ReadBaseInfoActivity : AppCompatActivity() , View.OnClickListener{
    private var list: ArrayList<BaseInfoBean>? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_base_info)
        initView()
        initData()
    }

    private fun initView() {
        iv_back.setOnClickListener(this)
        tv_refresh.setOnClickListener(this)
    }

    private fun initData() {
        list = ArrayList()
        for (i in 0 until 10){

            var bean = BaseInfoBean("水温"+i+"：","65摄氏度")
            list!!.add(bean)
        }

        if (baseInfoAdapter == null) {
            baseInfoAdapter = BaseInfoAdapter(list!!, this,1)
            listView!!.adapter = baseInfoAdapter
        } else {
            baseInfoAdapter!!.notifyDataSetChanged()
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
