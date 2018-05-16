package com.cy.obdproject.activity

import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.R.id.*
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.ECUConstant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.worker.BaseInfoWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_read_base_info.*
import org.jetbrains.anko.toast

class ReadBaseInfoActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var readBaseInfoWorker: BaseInfoWorker? = null
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
        readBaseInfoWorker = BaseInfoWorker()
        readBaseInfoWorker!!.init(this, ECUConstant.getReadBaseInfoData()) { data ->
            setData(data)
        }
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
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
                    val bean = BaseInfoBean()
                    bean.name = ECUConstant.getReadBaseInfoData()[i].name
                    list!!.add(bean)
                }
                setData(Gson().toJson(list))
            }
        }
    }

    override fun setData(data: String) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            dismissProgressDialog()
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
                if (isUserConnected) {// 用户连接
                    val str = "{\"activity\":\"" + this@ReadBaseInfoActivity.localClassName + "\",\"method\":\"" + "setData" + "\",\"data\":\"" + Gson().toJson(list).replace("\"", "\\\"") + "\"}"
                    val webSocketBean = WebSocketBean()

                    webSocketBean.s = "user1"// 自己（专家）id
                    webSocketBean.r = "zuser"// 连接用户id

//                    webSocketBean.s = "zuser"// 自己（专家）id
//                    webSocketBean.r = "user1"// 连接用户id

                    webSocketBean.c = "D"
                    webSocketBean.d = str// 自定义的json串
                    WebSocketService.getIntance()!!.sendMsg(Gson().toJson(webSocketBean))
                }
            } catch (e: Exception) {
                Log.i("cyf", "e : ${e.message}")
                toast(data!!)
            }
        }
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
        }
    }

}
