package com.cy.obdproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.cy.obdproject.R
import com.cy.obdproject.adapter.BaseInfoAdapter
import com.cy.obdproject.bean.BaseInfoBean
import kotlinx.android.synthetic.main.activity_write_base_info.*
import android.widget.Toast
import android.content.DialogInterface
import android.app.AlertDialog.THEME_HOLO_LIGHT
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.view.LayoutInflater





class WriteBaseInfoActivity : AppCompatActivity(), View.OnClickListener {

    private var list: ArrayList<BaseInfoBean>? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_base_info)
        initView()
        initData()
    }

    private fun initView() {
        iv_back.setOnClickListener(this)
        tv_refresh.setOnClickListener(this)

    }

    private fun initData() {
        list = ArrayList()
        for (i in 0 until 10){

            var bean = BaseInfoBean("水温"+i+"：","65摄氏度")
            list!!.add(bean)
        }

        if (baseInfoAdapter == null) {
            baseInfoAdapter = BaseInfoAdapter(list!!, this,2)
            listView!!.adapter = baseInfoAdapter
        } else {
            baseInfoAdapter!!.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            var view = LayoutInflater.from(this@WriteBaseInfoActivity).inflate(R.layout.alert_view, null)//这里必须是final的
            var edit = view.findViewById(R.id.et) as EditText//获得输入框对象
            edit.setText(list!![position].value.toString())
            if (!list!![position].value.isEmpty()){
                edit.setSelection(list!![position].value.length)
            }
            AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        val input = edit.text.toString()
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
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            iv_back.id -> {
                finish()
            }
            tv_refresh.id -> {

            }
        }
    }
}
