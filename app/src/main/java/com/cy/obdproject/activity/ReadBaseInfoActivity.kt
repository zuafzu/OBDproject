package com.cy.obdproject.activity

import android.os.Bundle
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.worker.ReadBaseInfoWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_read_base_info.*
import org.jetbrains.anko.toast

class ReadBaseInfoActivity : BaseActivity(), View.OnClickListener {

    private var readBaseInfoWorker: ReadBaseInfoWorker? = null
    private var list: ArrayList<BaseInfoBean>? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_base_info)
        initView()
        initData()
    }

    private fun initView() {
        list = ArrayList()
        readBaseInfoWorker = ReadBaseInfoWorker()

        iv_back.setOnClickListener(this)
        tv_refresh.setOnClickListener(this)
    }

    private fun initData() {
        readBaseInfoWorker!!.start(this) { data ->
            try {
                val mlist = Gson().fromJson<List<BaseInfoBean>>(data, object : TypeToken<ArrayList<BaseInfoBean>>() {}.type) as ArrayList<BaseInfoBean>?
                list!!.clear()
                list!!.addAll(mlist!!)
                if (baseInfoAdapter == null) {
                    baseInfoAdapter = BaseInfoAdapter(list!!, this@ReadBaseInfoActivity, 1)
                    listView!!.adapter = baseInfoAdapter
                } else {
                    baseInfoAdapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                toast(data!!)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            iv_back.id -> {
                finish()
            }
            tv_refresh.id -> {
                readBaseInfoWorker!!.start()
            }
        }
    }
}
