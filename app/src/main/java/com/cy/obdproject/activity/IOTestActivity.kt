package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.IOTestAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.IOTestBean
import kotlinx.android.synthetic.main.activity_iotest.*
import org.jetbrains.anko.toast

class IOTestActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<IOTestBean>? = null
    private var adapter: IOTestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iotest)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)

        list = ArrayList()
        for (i in 0 until 10){

            var bean = IOTestBean(""+i,"65摄氏度")
            list!!.add(bean)
        }

        if (adapter == null) {
            adapter = IOTestAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        adapter!!.setOnTestClick { id, position ->
            var intent = Intent(this@IOTestActivity,IOTest2Activity::class.java)
            intent.putExtra("bean",list!![position])
            startActivity(intent)
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
