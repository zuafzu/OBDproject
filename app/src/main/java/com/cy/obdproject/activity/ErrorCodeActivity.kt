package com.cy.obdproject.activity

import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.ErrorCodeAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.ErrorCodeBean
import kotlinx.android.synthetic.main.activity_error_code.*
import org.jetbrains.anko.toast

class ErrorCodeActivity : BaseActivity(), BaseActivity.ClickMethoListener {
    private var list: ArrayList<ErrorCodeBean>? = null
    private var adapter: ErrorCodeAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_code)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
        setClickMethod(btn_clean)

        list = ArrayList()
        for (i in 0 until 10) {
            var bean = ErrorCodeBean("水温", "65摄氏度")
            list!!.add(bean)
        }

        if (adapter == null) {
            adapter = ErrorCodeAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        adapter!!.setOnErrorCodeClick { id, position ->
            toast("" + position)
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_refresh" -> {

            }
            "btn_clean" -> {
                list!!.clear()
                adapter!!.notifyDataSetChanged()
            }

        }
    }


}
