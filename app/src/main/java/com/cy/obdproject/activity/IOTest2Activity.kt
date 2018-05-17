package com.cy.obdproject.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.IOTestBean
import kotlinx.android.synthetic.main.activity_iotest2.*
import android.widget.ArrayAdapter




class IOTest2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var bean: IOTestBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iotest2)
        initView()
    }

    private fun initView() {
        iv_back
        setClickMethod(iv_back)
        setClickMethod(btn_startTest)
        setClickMethod(btn_stopTest)

        bean = intent.getSerializableExtra("bean") as IOTestBean?

        val datas = ArrayList<String>()
        for (i in 0..9) {
            datas.add("项目$i")
        }

        // 建立Adapter并且绑定数据源
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datas)
        spinner.adapter = adapter

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //点击事件
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                tv_log.text = "" + datas.get(i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_startTest" -> {

            }
            "btn_startTest" -> {

            }

        }
    }
}
