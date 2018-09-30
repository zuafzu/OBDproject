package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.SPTools
import kotlinx.android.synthetic.main.activity_select_car_type.*
import org.jetbrains.anko.toast
import org.json.JSONObject


class SelectCarTypeActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var jsonObject: JSONObject? = null

    private var mExitTime: Long = 0

    private var list: ArrayList<String>? = null
    private var adapter: SelectAdapter? = null

    companion object {
        var INSTANCE: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_type)
        initView()
    }

    private fun initView() {
        try {
            setClickMethod(iv_back)
            INSTANCE = this

            list = ArrayList()

            val mJsonString = myApp.publicUnit.GetUI()
            jsonObject = JSONObject(mJsonString)

            val it = jsonObject!!.keys()
            while (it.hasNext()) {
                val key = it.next().toString()
                list!!.add(key)
            }

            if (adapter == null) {
                adapter = SelectAdapter(list!!, this)
                listView!!.adapter = adapter
            } else {
                adapter!!.notifyDataSetChanged()
            }

            listView.onItemClickListener = this
            if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
                iv_back.visibility = View.INVISIBLE
            } else {
                iv_back.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            LogTools.errLog(e)
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sendClick(this@SelectCarTypeActivity.localClassName, "" + p2)
        SPTools.put(this@SelectCarTypeActivity, Constant.CARNAME, list!![p2])
        val mIntent = Intent(this@SelectCarTypeActivity, SelectSystemActivity::class.java)
        mIntent.putExtra("CARTYPE", jsonObject!!.optJSONArray(list!![p2]).toString())
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {

            } else {
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    toast("再按一次退出程序")
                    mExitTime = System.currentTimeMillis()
                } else {
                    if( WebSocketService.getIntance()!=null){
                        WebSocketService.getIntance()!!.stopSelf()
                    }
                    val ip = SPTools[this@SelectCarTypeActivity, Constant.IP, ""]
                    val ModuleFile = SPTools[this, "ModuleFile", ""] as String
                    val ControlFile = SPTools[this, "ControlFile", ""] as String
                    val USERNAME = SPTools[this, Constant.USERNAME, ""] as String
                    val PASSWORD = SPTools[this, Constant.PASSWORD, ""] as String
                    SPTools.clear(this@SelectCarTypeActivity)
                    SPTools.put(this@SelectCarTypeActivity, Constant.IP, ip!!)
                    SPTools.put(this@SelectCarTypeActivity, "ModuleFile", ModuleFile)
                    SPTools.put(this@SelectCarTypeActivity, "ControlFile", ControlFile)
                    SPTools.put(this@SelectCarTypeActivity, Constant.USERNAME, USERNAME)
                    SPTools.put(this@SelectCarTypeActivity, Constant.PASSWORD, PASSWORD)
                    finish()
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
