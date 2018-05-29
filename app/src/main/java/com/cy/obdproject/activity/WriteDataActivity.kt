package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.WriteDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.WriteDataBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_write_data.*
import org.jetbrains.anko.toast

class WriteDataActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var list: ArrayList<WriteDataBean>? = null
    private var adapter: WriteDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)
        initView()
    }

    override fun onStart() {
        super.onStart()
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            net_fileList()
        }
    }

    private fun initView() {
        setClickMethod(iv_back)
        list = ArrayList()
        listView.onItemClickListener = this
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sendClick(this@WriteDataActivity.localClassName, "" + p2)
        val mIntent = Intent(this, WriteData2Activity::class.java)
        mIntent.putExtra("url", list!![p2].filePath)
        mIntent.putExtra("name", list!![p2].fileName)
        startActivity(mIntent)
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }

    override fun setData(data: String?) {
        try {
            val beans = Gson().fromJson<List<WriteDataBean>>(data, object : TypeToken<ArrayList<WriteDataBean>>() {}.type) as ArrayList<WriteDataBean>?
            list!!.clear()
            list!!.addAll(beans!!)
            if (adapter == null) {
                adapter = WriteDataAdapter(list!!, this)
                listView!!.adapter = adapter
            } else {
                adapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            toast(data!!)
        }
        super.setData(data)
    }

    private fun net_fileList() {
        val map = hashMapOf<String, String>()
        NetTools.net(map, Urls().fileList, this, { response ->
            setData(response.data)
        }, "正在加载...", true, true)
    }

}
