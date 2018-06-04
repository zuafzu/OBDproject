package com.cy.obdproject.activity

import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.ErrorCodeAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.ErrorCodeBean
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.worker.ErrorCodeClearWorker
import com.cy.obdproject.worker.ErrorCodeWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_error_code.*
import org.jetbrains.anko.toast

class ErrorCodeActivity : BaseActivity(), BaseActivity.ClickMethoListener, ErrorCodeAdapter.OnErrorCodeClick {

    private var list: ArrayList<ErrorCodeBean>? = null
    private var adapter: ErrorCodeAdapter? = null
    private var errorCodeWorker: ErrorCodeWorker? = null
    private var errorCodeClearWorker: ErrorCodeClearWorker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_code)
        initView()
        initData()
    }

    private fun initData() {
        if (isProfessionalConnected) {// 专家连接
            doMethod("tv_refresh")
        } else {
            showProgressDialog()
            if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                errorCodeWorker!!.start()
            } else {
                list!!.clear()
                setData(Gson().toJson(list))
            }
        }
    }

    private fun initView() {
        errorCodeWorker = ErrorCodeWorker()
        errorCodeWorker!!.init(this) { data ->
            setData(data)
        }
        errorCodeClearWorker = ErrorCodeClearWorker()
        errorCodeClearWorker!!.init(this) { data ->
            setData(data)
        }
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
        setClickMethod(btn_clean)
        list = ArrayList()
    }

    override fun setData(data: String?) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            dismissProgressDialog()
            try {
                val mlist = Gson().fromJson<List<ErrorCodeBean>>(data, object : TypeToken<ArrayList<ErrorCodeBean>>() {}.type) as ArrayList<ErrorCodeBean>?
                list!!.clear()
                list!!.addAll(mlist!!)
                if (adapter == null) {
                    adapter = ErrorCodeAdapter(list!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
                adapter!!.setOnErrorCodeClick(this)

            } catch (e: Exception) {
                Log.i("cyf", "e : ${e.message}")
                toast(data!!)
                if("清空数据成功" == data){
                    list!!.clear()
                    adapter!!.notifyDataSetChanged()
                }
            }
            super.setData(data)
        }
    }

    override fun setOnErrorCodeClick(id: String?, position: Int) {
        sendClick(this@ErrorCodeActivity.localClassName, "" + position)
        toast("" + position)
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_refresh" -> {
                if (!isProfessionalConnected) {// 专家连接
                    initData()
                }
            }
            "btn_clean" -> {
                if (!isProfessionalConnected) {// 专家连接
                    errorCodeClearWorker!!.start()
                }
            }
            else -> {
                setOnErrorCodeClick(string, string!!.toInt())
            }

        }
    }


}
