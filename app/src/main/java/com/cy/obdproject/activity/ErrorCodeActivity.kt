package com.cy.obdproject.activity

import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.adapter.ErrorCodeAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.bean.ErrorCodeBean
import com.cy.obdproject.constant.ECUConstant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.worker.BaseInfoWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_error_code.*
import org.jetbrains.anko.toast

class ErrorCodeActivity : BaseActivity(), BaseActivity.ClickMethoListener,ErrorCodeAdapter.OnErrorCodeClick {

    private var list: ArrayList<ErrorCodeBean>? = null
    private var adapter: ErrorCodeAdapter? = null
    private var readBaseInfoWorker: BaseInfoWorker? = null

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
                readBaseInfoWorker!!.start()
            } else {
                list!!.clear()
                for (i in 0 until ECUConstant.getReadBaseInfoData().size) {
                    val bean = ErrorCodeBean()
                    bean.msg = ECUConstant.getReadBaseInfoData()[i].name
                    list!!.add(bean)
                }
                setData(Gson().toJson(list))
            }
        }
    }
    private fun initView() {
        readBaseInfoWorker = BaseInfoWorker()
        readBaseInfoWorker!!.init(this, ECUConstant.getReadBaseInfoData()) { data ->
            setData(data)
        }

        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
        setClickMethod(btn_clean)

        list = ArrayList()
//        for (i in 0 until 10) {
//            var bean = ErrorCodeBean("水温", "65摄氏度")
//            list!!.add(bean)
//        }

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
            }
            super.setData(data)
        }
    }
    override fun setOnErrorCodeClick(id: String?, position: Int) {
        sendClick(this@ErrorCodeActivity.localClassName,""+position)
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
                list!!.clear()
                adapter!!.notifyDataSetChanged()
            }
            else ->{
                setOnErrorCodeClick(string,string!!.toInt())
            }

        }
    }


}
