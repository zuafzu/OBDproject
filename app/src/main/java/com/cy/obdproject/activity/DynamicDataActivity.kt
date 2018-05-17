package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.DynamicDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.DynamicDataBean
import kotlinx.android.synthetic.main.activity_dynamic_data.*
import org.jetbrains.anko.toast

class DynamicDataActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<DynamicDataBean>? = null
    private var adapter: DynamicDataAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_data)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)
        setClickMethod(btn_next)

        list = ArrayList()
        for (i in 0 until 50) {

            var bean = DynamicDataBean("" + i, "65摄氏度", "" + i, "0")
            list!!.add(bean)
        }

        if (adapter == null) {
            adapter = DynamicDataAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { adapterView, view, i, l ->
            if (list!![i].isSelect == "1") {
                list!![i].isSelect = "0"
            } else {
                list!![i].isSelect = "1"
            }
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_next" -> {
                var listData = ArrayList<DynamicDataBean>()
                if (list!!.size > 0) {
                    for (i in 0 until list!!.size) {

                        if (list!![i].isSelect == "1") {
                            listData.add(list!![i])
                        }
                    }
                    var intent = Intent(this@DynamicDataActivity, DynamicData2Activity::class.java)
                    intent.putExtra("listData", listData)
                    startActivity(intent)

                } else {
                    toast("请选择要监控数据")
                }
            }
        }
    }
}
