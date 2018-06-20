package com.cy.obdproject.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import com.cy.obdproject.constant.ECUConstant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.ECUTools
import com.cy.obdproject.worker.BaseInfoWorker
import com.cy.obdproject.worker.WriteBaseInfoWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_write_base_info.*
import org.jetbrains.anko.toast

class WriteBaseInfoActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener, TextWatcher {

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

        list!!.clear()
        for (i in 0 until ECUConstant.getWriteBaseInfoData1().size) {
            val bean = BaseInfoBean()
            bean.name = ECUConstant.getWriteBaseInfoData1()[i].name
            list!!.add(bean)
        }
        setData(Gson().toJson(list))

        readBaseInfoWorker = BaseInfoWorker()
        readBaseInfoWorker!!.init(this, ECUConstant.getWriteBaseInfoData1()) { data ->
            setData(data)
        }
        writeBaseInfoWorker = WriteBaseInfoWorker()
        writeBaseInfoWorker!!.init(this) { data ->
            setData1(data)
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
            } catch (e: Exception) {
                Log.i("cyf", "e : ${e.message}")
                toast(data!!)
            }
            super.setData(data)
        }
    }

    override fun setData1(data: String?) {
        runOnUiThread {
            if (data == "修改成功") {
                initData()
            }
            toast(data!!)
            dismissProgressDialog()
            super.setData1(data)
        }
    }

    private fun putData(data: String) {
        showProgressDialog()
        var input = data.split("-")[0]
        // 远程赋值
        edit!!.setText(input)
        Handler().postDelayed({
            val position = data.split("-")[1].toInt()
            val length = input.length
//            if (list!![position].name.contains("模式配置")) {
//                if (length != 1) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("FAW车辆识别号码")) {
//                if (length != 17) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("维修店代码和/或诊断仪序列号")) {
//                if (length != 10) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("ECU安装日期")) {
//                if (length != 4) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("车辆规格编号")) {
//                if (length != 18) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("FAW生产线中的汽车制造日期")) {
//                if (length != 4) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("车辆运输模式")) {
//                if (length != 1) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("车辆售后服务模式")) {
//                if (length != 1) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("噪声Simu语音配置")) {
//                if (length != 1) {
//                    input = ""
//                }
//            } else if (list!![position].name.contains("车辆配置信息")) {
//                if (length != 4) {
//                    input = ""
//                }
//            }
            var max = 100
            try {
                max = Integer.valueOf(ECUConstant.getWriteBaseInfoData1()[position].byteLength)
            } catch (e: Exception) {

            }
            if (length != max) {
                input = ""
            }
            if (input == "") {
                toast("录入数据长度错误！")
                dismissProgressDialog()
            } else {
                if (isProfessionalConnected) {
                    // 专家端参数修改，什么都不操作，等待用户反馈，反馈方法需要实现

                } else {
                    val socketBean = ECUConstant.getWriteBaseInfoData2()[position]
                    socketBean.value = ECUTools.putData(input)
                    writeBaseInfoWorker!!.start(socketBean)
                }
            }
            dismiss()
        }, 1000)
    }

    private fun dismiss() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, mView: View?, position: Int, id: Long) {
        sendClick(this@WriteBaseInfoActivity.localClassName, "" + position)
        view = LayoutInflater.from(this@WriteBaseInfoActivity).inflate(R.layout.alert_view, null)
        edit = view!!.findViewById(R.id.et) as EditText
//        if (list!![position].name.contains("模式配置")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1)) //最大输入长度
//        } else if (list!![position].name.contains("FAW车辆识别号码")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(17)) //最大输入长度
//        } else if (list!![position].name.contains("维修店代码和/或诊断仪序列号")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10)) //最大输入长度
//        } else if (list!![position].name.contains("ECU安装日期")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4)) //最大输入长度
//        } else if (list!![position].name.contains("车辆规格编号")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(18)) //最大输入长度
//        } else if (list!![position].name.contains("FAW生产线中的汽车制造日期")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4)) //最大输入长度
//        } else if (list!![position].name.contains("车辆运输模式")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1)) //最大输入长度
//        } else if (list!![position].name.contains("车辆售后服务模式")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1)) //最大输入长度
//        } else if (list!![position].name.contains("噪声Simu语音配置")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1)) //最大输入长度
//        } else if (list!![position].name.contains("车辆配置信息")) {
//            edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4)) //最大输入长度
//        }
        var max = 100
        try {
            max = Integer.valueOf(ECUConstant.getWriteBaseInfoData1()[position].byteLength)
            edit!!.hint = "输入长度$max"
        } catch (e: Exception) {
            edit!!.hint = "输入长度未知"
        }
        edit!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max)) //最大输入长度
        edit!!.setText("")
        if (list!![position].value != null) {
            // edit!!.setText(list!![position].value.toString())
            // edit!!.setSelection(list!![position].value.length)
        }
        dialog = AlertDialog.Builder(this)
                .setTitle(list!![position].name)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确定", { _, _ ->
                    val input = edit!!.text.toString()
                    sendClick(this@WriteBaseInfoActivity.localClassName, "$input-$position")
                    putData("$input-$position")
                })
                .setNegativeButton("取消") { _, _ ->
                    sendClick(this@WriteBaseInfoActivity.localClassName, "")
                    dismiss()
                }.create()
        dialog!!.setOnDismissListener {
            if (isUserConnected) {

            } else {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        dialog!!.show()
        edit!!.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        sendClick(this@WriteBaseInfoActivity.localClassName, "statusEdit" + edit!!.text.toString())

    }

    override fun doMethod(string: String?) {
        when {
            string!!.contains("-") -> putData(string)
            string == "" -> dismiss()
            else -> when (string) {
                "iv_back" -> {
                    finish()
                }
                "tv_refresh" -> {
                    if (!isProfessionalConnected) {// 专家连接
                        initData()
                    }
                }
                else -> {
                    if (string!!.contains("statusEdit")) {
                        var title: String = string!!.substring(0, 10)
                        var content: String = string!!.substring(10, string.length)
                        edit!!.setText(content)
                        edit!!.setSelection(content!!.length)

                    } else {
                        onItemClick(null, null, string!!.toInt(), string.toLong())
                    }
                }
            }
        }
    }

}
