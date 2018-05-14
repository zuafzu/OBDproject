package com.cy.obdproject.activity

import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.worker.ReadBaseInfoWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_read_base_info.*
import org.jetbrains.anko.toast

class ReadBaseInfoActivity : BaseActivity(), BaseActivity.ClickMethoListener {

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
        readBaseInfoWorker!!.init(this) { data ->
            try {
                val mlist = Gson().fromJson<List<BaseInfoBean>>(data, object : TypeToken<ArrayList<BaseInfoBean>>() {}.type) as ArrayList<BaseInfoBean>?
                list!!.clear()
                list!!.addAll(mlist!!)
                setData()
            } catch (e: Exception) {
                toast(data!!)
            }
        }
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
    }

    private fun initData() {
        if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
            showProgressDialog()
            readBaseInfoWorker!!.start()
        } else {
            list!!.clear()
            for (i in 0 until ReadBaseInfoWorker.names.size) {
                val bean = BaseInfoBean()
                bean.name = ReadBaseInfoWorker.names[i]
                list!!.add(bean)
            }
            setData()
        }
    }

    private fun setData() {
        dismissProgressDialog()
        if (baseInfoAdapter == null) {
            baseInfoAdapter = BaseInfoAdapter(list!!, this@ReadBaseInfoActivity, 1)
            listView!!.adapter = baseInfoAdapter
        } else {
            baseInfoAdapter!!.notifyDataSetChanged()
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_refresh" -> {
                initData()
            }
        }
    }

}
