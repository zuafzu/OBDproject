package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_system.*
import org.json.JSONArray

class SelectSystemActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var CARTYPE = ""
    private var listString = ArrayList<String>()

    private var list: ArrayList<String>? = null
    private var adapter: SelectAdapter? = null

    companion object {
        var INSTANCE: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_system)
        initView()
    }

    private fun initView() {
        INSTANCE = this
        CARTYPE = intent.getStringExtra("CARTYPE")
        setClickMethod(iv_back)
        list = ArrayList()
        listString.clear()
        val jsonArray = JSONArray(CARTYPE)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(i)
            val it = jsonObject!!.keys()
            while (it.hasNext()) {
                val key = it.next().toString()
                list!!.add(key)
                listString.add(jsonObject.getJSONArray(key).toString())
            }
        }

        if (adapter == null) {
            adapter = SelectAdapter(list!!, this)
            listView!!.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
        listView.onItemClickListener = this
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sendClick(this@SelectSystemActivity.localClassName, "" + p2)
        val name = SPTools[this@SelectSystemActivity, Constant.CARNAME, ""].toString()
        SPTools.put(this@SelectSystemActivity, Constant.CARNAME, name + " - " + list!![p2])
        val mIntent = Intent(this@SelectSystemActivity, MainActivity::class.java)
        mIntent.putExtra("CARSYSTEM", listString[p2])
        startActivity(mIntent)
        finish()
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
}
