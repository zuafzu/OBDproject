package com.cy.obdproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.adapter.WriteDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.bean.WriteDataBean
import kotlinx.android.synthetic.main.activity_write_data.*
import org.jetbrains.anko.toast

class WriteDataActivity : BaseActivity(), BaseActivity.ClickMethoListener {


    private var list: ArrayList<WriteDataBean>? = null
    private var adapter: WriteDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)
        initView()
    }


    private fun initView() {
        setClickMethod(iv_back)

        list = ArrayList()
        for (i in 0 until 10){

            var bean = WriteDataBean(""+i,"65摄氏度","filePath"+i)
            list!!.add(bean)
        }

        if (adapter == null) {
            adapter = WriteDataAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            toast("filePath = "+list!![position].filePath)
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
