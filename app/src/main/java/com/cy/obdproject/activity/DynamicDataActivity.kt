package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.DynamicDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.DynamicDataBean
import com.cy.obdproject.worker.BaseInfoWorker
import kotlinx.android.synthetic.main.activity_dynamic_data.*
import org.jetbrains.anko.toast

class DynamicDataActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var list: ArrayList<DynamicDataBean>? = null
    private var adapter: DynamicDataAdapter? = null
    private var readBaseInfoWorker: BaseInfoWorker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_data)
        initView()
    }

    private fun initView() {
        list = ArrayList()
        setClickMethod(iv_back)
        setClickMethod(btn_next)

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

        listView.setOnItemClickListener(this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        sendClick(this@DynamicDataActivity.localClassName, "" + position)
        if (list!![position].isSelect == "1") {
            list!![position].isSelect = "0"
        } else {
            list!![position].isSelect = "1"
        }
        adapter!!.notifyDataSetChanged()

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
                    if (listData!!.size > 0) {
                        var intent = Intent(this@DynamicDataActivity, DynamicData2Activity::class.java)
                        intent.putExtra("listData", listData)
                        startActivity(intent)
                    } else {
                        toast("请选择要监控数据")
                    }

                }
            }
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }
}
