package com.cy.obdproject.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseInfoBean
import kotlinx.android.synthetic.main.activity_write_base_info.*


class WriteBaseInfoActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var list: ArrayList<BaseInfoBean>? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null
    private var view: View? = null
    private var edit: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_base_info)
        initView()
        initData()
    }

    private fun initView() {
        view = LayoutInflater.from(this@WriteBaseInfoActivity).inflate(R.layout.alert_view, null)
        edit = view!!.findViewById(R.id.et) as EditText
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
    }

    private fun initData() {
        list = ArrayList()
        var bean = BaseInfoBean("模式配置", "")
        list!!.add(bean)
        var bean2 = BaseInfoBean("FAW车辆识别号码", "")
        list!!.add(bean2)
        var bean3 = BaseInfoBean("维修店代码和/或诊断仪序列号", "")
        list!!.add(bean3)
        var bean4 = BaseInfoBean("ECU安装日期", "")
        list!!.add(bean4)
        var bean5 = BaseInfoBean("车辆规格编号", "")
        list!!.add(bean5)
        var bean6 = BaseInfoBean("FAW生产线中的汽车制造日期", "")
        list!!.add(bean6)
        var bean7 = BaseInfoBean("车辆运输模式", "")
        list!!.add(bean7)
        var bean8 = BaseInfoBean("车辆售后服务模式", "")
        list!!.add(bean8)
        var bean9 = BaseInfoBean("噪声Simu语音配置", "")
        list!!.add(bean9)
        var bean10 = BaseInfoBean("车辆配置信息", "")
        list!!.add(bean10)

        if (baseInfoAdapter == null) {
            baseInfoAdapter = BaseInfoAdapter(list!!, this, 2)
            listView!!.adapter = baseInfoAdapter
        } else {
            baseInfoAdapter!!.notifyDataSetChanged()
        }
        listView.onItemClickListener = this
    }

    override fun setData(data: String?) {
        runOnUiThread {

        }
    }

    override fun onItemClick(parent: AdapterView<*>?, mView: View?, position: Int, id: Long) {
        onItemClick(position)
    }

    private fun onItemClick(position: Int) {

        sendClick(this@WriteBaseInfoActivity.localClassName, "" + position)
        edit!!.setText(list!![position].value.toString())
        if (!list!![position].value.isEmpty()) {
            edit!!.setSelection(list!![position].value.length)
        }
        AlertDialog.Builder(this)
                .setTitle(list!![position].name)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确定", { _, _ ->
                    val input = edit!!.text.toString()
                    if (input == "") {
                        Toast.makeText(applicationContext, "不能为空！", Toast.LENGTH_LONG).show()
                    } else {
                        list!![position].value = input
                        baseInfoAdapter!!.notifyDataSetChanged()
                    }
                })
                .setNegativeButton("取消", null)
                .show()
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                onItemClick(null, null, 5, 5)
                // finish()
            }
            "tv_refresh" -> {

            }
            else -> {
                onItemClick(string!!.toInt())
            }
        }
    }

}
