package com.cy.obdproject.activity

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.WriteDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.WriteDataBean
import com.cy.obdproject.tools.StringTools
import kotlinx.android.synthetic.main.activity_write_data.*
import org.jetbrains.anko.toast

class WriteDataActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<WriteDataBean>? = null
    private var adapter: WriteDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)
        initView()
        test()
    }


    private fun initView() {
        setClickMethod(iv_back)

        list = ArrayList()
        for (i in 0 until 10) {
            var bean = WriteDataBean("" + i, "65摄氏度", "filePath" + i)
            list!!.add(bean)
        }

        if (adapter == null) {
            adapter = WriteDataAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            toast("filePath = " + list!![position].filePath)
        }

    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
        }
    }

    fun test() {
        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg p0: String?): String {
                val mIs = assets.open("HS3EV_EPS_20180327.eol")
                val length = mIs.available()
                Log.e("cyf88", "length " + length)
                val buffer = ByteArray(length)
                mIs.read(buffer)
                val ba1 = ByteArray(4)
                ba1[0] = buffer[10]
                ba1[1] = buffer[9]
                ba1[2] = buffer[8]
                ba1[3] = buffer[7]
                val ba2 = ByteArray(4)
                ba2[0] = buffer[14]
                ba2[1] = buffer[13]
                ba2[2] = buffer[12]
                ba2[3] = buffer[11]
                var str = StringTools.byte2hex(ba1)
                var str2 = StringTools.byte2hex(ba2)
                val a = (str2.toInt(16) - str.toInt(16) + 1)
                Log.e("cyf88", "" + str2.toInt(16) + " - " + str.toInt(16) + " = " + a)
                ba1[0] = buffer[14 + a + 4]
                ba1[1] = buffer[14 + a + 3]
                ba1[2] = buffer[14 + a + 2]
                ba1[3] = buffer[14 + a + 1]
                ba2[0] = buffer[14 + a + 8]
                ba2[1] = buffer[14 + a + 7]
                ba2[2] = buffer[14 + a + 6]
                ba2[3] = buffer[14 + a + 5]
                str = StringTools.byte2hex(ba1)
                str2 = StringTools.byte2hex(ba2)
                val b = (str2.toInt(16) - str.toInt(16) + 1)
                Log.e("cyf88", "" + str2.toInt(16) + " - " + str.toInt(16) + " = " + b)
                ba1[0] = buffer[14 + a + 8 + b + 4]
                ba1[1] = buffer[14 + a + 8 + b + 3]
                ba1[2] = buffer[14 + a + 8 + b + 2]
                ba1[3] = buffer[14 + a + 8 + b + 1]
                ba2[0] = buffer[14 + a + 8 + b + 8]
                ba2[1] = buffer[14 + a + 8 + b + 7]
                ba2[2] = buffer[14 + a + 8 + b + 6]
                ba2[3] = buffer[14 + a + 8 + b + 5]
                str = StringTools.byte2hex(ba1)
                str2 = StringTools.byte2hex(ba2)
                val c = (str2.toInt(16) - str.toInt(16) + 1)
                Log.e("cyf88", "" + str2.toInt(16) + " - " + str.toInt(16) + " = " + c)
                return ""
            }

        }.execute()
    }

}
