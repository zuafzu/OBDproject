package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.DynamicDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.DynamicDataBean
import com.cy.obdproject.tools.LogTools
import kotlinx.android.synthetic.main.activity_dynamic_data.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

class DynamicDataActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var code = ""

    private var list = CopyOnWriteArrayList<DynamicDataBean>()
    private var adapter: DynamicDataAdapter? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_data)
        initView()
    }

    private fun initView() {
        code = intent.getStringExtra("code")

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                myApp.publicUnit.setBreak(true)
                setData("toast" + msg.obj.toString())
            }
        }
        try {
           myApp.publicUnit.setMessageHandler( @SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        4 -> {
                            val data = msg.obj.toString()
                            val jsonObject = JSONObject(data)
                            val jsonArray = jsonObject.optJSONArray("List")
                            list.clear()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject1 = jsonArray.getJSONObject(i)
                                val bean = DynamicDataBean()
                                bean.sid = jsonObject1!!.optString("SID")
                                bean.did = jsonObject1!!.optString("DID")
                                bean.name = jsonObject1!!.optString("Name")
                                bean.name_ENG = jsonObject1!!.optString("Name_ENG")
                                bean.byte_Start = jsonObject1!!.optString("Byte_Start")
                                bean.byte_Length = jsonObject1!!.optString("Byte_Length")
                                bean.bit_Start = jsonObject1!!.optString("Bit_Start")
                                bean.bit_Length = jsonObject1!!.optString("Bit_Length")
                                bean.coefficient = jsonObject1!!.optString("Coefficient")
                                bean.offset = jsonObject1!!.optString("Offset")
                                bean.type = jsonObject1!!.optString("Type")
                                bean.enum = jsonObject1!!.optString("Enum")
                                bean.unit = jsonObject1!!.optString("Unit")
                                bean.unit_ENG = jsonObject1!!.optString("Unit_ENG")
                                bean.value_Min = jsonObject1!!.optString("Value_Min")
                                bean.value_Max = jsonObject1!!.optString("Value_Max")
                                list.add(bean)
                            }
                            if (adapter == null) {
                                adapter = DynamicDataAdapter(list!!, this@DynamicDataActivity)
                                listView!!.adapter = adapter
                            } else {
                                adapter!!.notifyDataSetChanged()
                            }
                        }
                    }
                    super.handleMessage(msg)
                }
            })
            myApp.publicUnit.SetEvent(handler,code.split(",")[0])
        } catch (e: Exception) {
            LogTools.errLog(e)
        }
        setClickMethod(iv_back)
        setClickMethod(btn_next)
        listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        sendClick(this@DynamicDataActivity.localClassName, "" + position)
        if (list!![position].isSelect == "1") {
            list!![position].isSelect = "0"
        } else {
            list!![position].isSelect = "1"
        }
        adapter!!.notifyDataSetChanged()
    }


    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_next" -> {
                var listData = CopyOnWriteArrayList<DynamicDataBean>()
                if (list!!.size > 0) {
                    for (i in 0 until list!!.size) {
                        if (list!![i].isSelect == "1") {
                            listData.add(list!![i])
                        }
                    }
                    if (listData!!.size > 0) {
                        var intent = Intent(this@DynamicDataActivity, DynamicData2Activity::class.java)
                        intent.putExtra("listData", listData)
                        intent.putExtra("code",code)
                        startActivity(intent)
                    } else {
                        toast("请选择要监控数据")
                    }

                }
            }
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }
}
