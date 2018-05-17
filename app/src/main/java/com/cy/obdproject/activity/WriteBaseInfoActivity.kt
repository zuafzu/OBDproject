package com.cy.obdproject.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.ECUConstant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.ECUTools
import com.cy.obdproject.worker.BaseInfoWorker
import com.cy.obdproject.worker.WriteBaseInfoWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_write_base_info.*
import org.jetbrains.anko.toast

class WriteBaseInfoActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var readBaseInfoWorker: BaseInfoWorker? = null
    private var writeBaseInfoWorker: WriteBaseInfoWorker? = null
    private var list: ArrayList<BaseInfoBean>? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null

    private var dialog: AlertDialog? = null
    private var view: View? = null
    private var edit: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_base_info)
        initView()
        initData()
    }

    private fun initView() {
        list = ArrayList()
        readBaseInfoWorker = BaseInfoWorker()
        readBaseInfoWorker!!.init(this, ECUConstant.getWriteBaseInfoData1()) { data ->
            setData(data)
        }
        writeBaseInfoWorker = WriteBaseInfoWorker()
        writeBaseInfoWorker!!.init(this) { data ->
            if (data == "0") {
                initData()
            } else {
                toast(data)
            }
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
                for (i in 0 until ECUConstant.getWriteBaseInfoData1().size) {
                    val bean = BaseInfoBean()
                    bean.name = ECUConstant.getWriteBaseInfoData1()[i].name
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
                    baseInfoAdapter = BaseInfoAdapter(list!!, this@WriteBaseInfoActivity, 2)
                    listView!!.adapter = baseInfoAdapter
                    listView!!.onItemClickListener = this
                } else {
                    baseInfoAdapter!!.notifyDataSetChanged()
                }
                if (isUserConnected) {// 用户连接
                    val str = "{\"activity\":\"" + this@WriteBaseInfoActivity.localClassName + "\",\"method\":\"" + "setData" + "\",\"data\":\"" + Gson().toJson(list).replace("\"", "\\\"") + "\"}"
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

    override fun onItemClick(parent: AdapterView<*>?, mView: View?, position: Int, id: Long) {
        sendClick(this@WriteBaseInfoActivity.localClassName, "" + position)
        view = LayoutInflater.from(this@WriteBaseInfoActivity).inflate(R.layout.alert_view, null)
        edit = view!!.findViewById(R.id.et) as EditText
        edit!!.setText("")
        if (list!![position].value != null) {
            edit!!.setText(list!![position].value.toString())
            edit!!.setSelection(list!![position].value.length)
        }
        dialog = AlertDialog.Builder(this)
                .setTitle(list!![position].name)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确定", { _, _ ->
                    var input = edit!!.text.toString()
                    val length = input.length
                    if (list!![position].name.contains("模式配置")) {
                        if (length != 1) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("FAW车辆识别号码")) {
                        if (length != 17) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("维修店代码和/或诊断仪序列号")) {
                        if (length != 10) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("ECU安装日期")) {
                        if (length != 4) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("车辆规格编号")) {
                        if (length != 18) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("FAW生产线中的汽车制造日期")) {
                        if (length != 4) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("车辆运输模式")) {
                        if (length != 1) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("车辆售后服务模式")) {
                        if (length != 1) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("噪声Simu语音配置")) {
                        if (length != 1) {
                            input = ""
                        }
                    } else if (list!![position].name.contains("车辆配置信息")) {
                        if (length != 4) {
                            input = ""
                        }
                    }
                    if (input == "") {
                        toast("录入数据长度错误！")
                    } else {
                        Log.e("cyf88","ECUTools.putData(input) : "+ECUTools.putData(input))
                        val socketBean = ECUConstant.getWriteBaseInfoData2()[position]
                        // input需要处理
                        socketBean.data = socketBean.data + ECUTools.putData(input)
                        writeBaseInfoWorker!!.start(socketBean)
                    }
                    dialog!!.dismiss()
                })
                .setNegativeButton("取消") { _, _ ->
                    dialog!!.dismiss()
                }.create()
        dialog!!.show()
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
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }

}
