package com.cy.obdproject.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.IOTestBean
import com.cy.obdproject.socket.SocketService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_iotest2.*
import org.jetbrains.anko.toast

class IOTest2Activity : BaseActivity(), BaseActivity.ClickMethoListener, TextWatcher {

    private var bean: IOTestBean? = null
    private var isFirst: Boolean = true
    private val datas = ArrayList<String>()
    private var spinnerItem: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iotest2)
        initView()
    }

    // private var readBaseInfoWorker: BaseInfoWorker? = null

    private fun initView() {
        //worker待更改
        // readBaseInfoWorker = BaseInfoWorker()
//        readBaseInfoWorker!!.init(this, ECUConstant.getDynamicData()) { data ->
//            setData(data)
//        }

        setClickMethod(iv_back)
        setClickMethod(btn_startTest)
        setClickMethod(btn_stopTest)
        tv_log.setMovementMethod(ScrollingMovementMethod.getInstance());
        bean = intent.getSerializableExtra("bean") as IOTestBean?

        for (i in 0..9) {
            datas.add("项目$i")
        }
        // 建立Adapter并且绑定数据源
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datas)
        spinner.adapter = adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnet点击事件
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (!isFirst) {
                    sendClick(this@IOTest2Activity.localClassName, "status2" + i)
                }
                isFirst = false
                tv_log.text = "" + datas.get(i)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        //输入文字监听
        et_name.addTextChangedListener(this)

    }

    override fun afterTextChanged(s: Editable?) {
        //To change body of created functions use File | Settings | File Templates.
        sendClick(this@IOTest2Activity.localClassName, "status1" + et_name.text.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_startTest" -> {
                initData()

            }
            "btn_stopTest" -> {

            }
            else -> {
                if (string!!.length >= 7) {
                    putData(string)// 非专家端显示数据（分多种情况：status1 为EditText输入；status2 为spin）
                }
            }

        }
    }

    private fun putData(string: String) {
        var title: String = string!!.substring(0, 7)
        var content: String = string!!.substring(7, string.length)
        when (title) {
            "status1" -> {
                et_name.setText(content)
                et_name.setSelection(content!!.length)
            }
            "status2" -> {
                spinner.setSelection(content.toInt())
                spinnerItem = content.toInt()
            }
        }
    }

    private var workData: String?= null

    private fun initData() {

        if (isProfessionalConnected) {// 专家连接

        } else {
            showProgressDialog()
            if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                // readBaseInfoWorker!!.start()
            } else {
                workData = datas.get(spinnerItem!!)
            }
            setData(Gson().toJson(workData))
        }
    }

    private var logCat: String?= ""

    override fun setData(data: String?) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            dismissProgressDialog()
            try {
                for (i in 0..9){
                    logCat += data + '\n'
                }
                tv_log.setText(logCat)
            } catch (e: Exception) {
                Log.i("cyf", "e : ${e.message}")
                toast(data!!)
            }
            super.setData(data)
        }

    }


}
