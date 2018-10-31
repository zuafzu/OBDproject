package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.LoginBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_select_role.*
import org.jetbrains.anko.toast

class SelectRoleActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<String>? = null
    private var username = ""
    private var password = ""
    private var adapter: SelectAdapter? = null

    companion object {
        var INSTANCE: Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_role)
        initView()
    }

    private fun initView() {
        INSTANCE = this

        setClickMethod(iv_back)

        if (intent.hasExtra("list")) {
            username = intent.getStringExtra("username")
            password = intent.getStringExtra("password")
            list = intent.getStringArrayListExtra("list")

            if (adapter == null) {
                adapter = SelectAdapter(list, this)
                listView!!.adapter = adapter
            } else {
                adapter!!.notifyDataSetChanged()
            }

            listView.setOnItemClickListener { _, _, position, id ->
                //s:受控端；z：专家端
                if ("受控端" == list!![position].toString()) {
                    net_login(username, password, "s")
                } else if ("专家端" == list!![position].toString()) {
                    net_login(username, password, "z")
                }
            }
        }

    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
        }
    }

    private fun net_login(username: String, pwd: String, type: String) {
        val map = hashMapOf<String, String>()
        map["username"] = username
        map["pwd"] = pwd
        map["loginType"] = type
        NetTools.net(map, Urls().auth_login, this, { response ->
            Log.e("zj", "auth_login = " + response!!.data)
            var loginBean = Gson().fromJson(response.data, LoginBean::class.java)

            SPTools.put(this@SelectRoleActivity, Constant.TOKEN, "" + loginBean.token)
            SPTools.put(this@SelectRoleActivity, Constant.USERID, "" + loginBean.userId)

            var list = ArrayList<String>()
            list.addAll(loginBean.userType.toLowerCase().replace("s", "受控端").replace("z", "专家端").split(","))

            if (list.size == 1) {
                if ("受控端" == list!![0].toString()) {
                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userNormal)
//                    startActivity(Intent(this@SelectRoleActivity, SelectCarTypeActivity::class.java))
//                    finish()
                    if (SPTools[this@SelectRoleActivity, Constant.IP, ""].toString() == "") {
                        // 第一次登录设置IP
                        // startActivity(Intent(this@LoginActivity, ConnentOBDActivity::class.java))
                        startActivity(Intent(this@SelectRoleActivity, SettingIpActivity::class.java))
                        finish()
                    } else {
                        // 默认连接
                        val mIntent2 = Intent(this, SocketService::class.java)
                        stopService(mIntent2)
                        Handler().postDelayed({
                            Constant.mDstName = SPTools[this@SelectRoleActivity, Constant.IP, ""].toString()
                            myApp.publicUnit.setScriptManagerParam(true, true, Constant.mDstName, Constant.mDstPort)
                            startService(mIntent2)
                            Handler().postDelayed({
                                dismissProgressDialog()
                                // 结束LoginActivity
                                for (i in 0 until (application as MyApp).activityList.size) {
                                    if ((application as MyApp).activityList[i].localClassName.contains("LoginActivity")) {
                                        (application as MyApp).activityList[i].finish()
                                        break
                                    }
                                }
                                if (SocketService.getIntance() != null &&
                                        SocketService.getIntance()!!.isConnected()) {
                                    this.finish()
                                    startActivity(Intent(this@SelectRoleActivity, SelectCarTypeActivity::class.java))
                                } else {
                                    toast("请先确认连接设备")
                                    // startActivity(Intent(this@LoginActivity, ConnentOBDActivity::class.java))
                                    startActivity(Intent(this@SelectRoleActivity, SettingIpActivity::class.java))
                                    finish()
                                }
                            }, 2000)
                        }, 1000)
                    }
                } else if ("专家端" == list!![0].toString()) {
//                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userProfessional)
//                    startActivity(Intent(this@SelectRoleActivity, RequestListActivity::class.java))
//                    finish()
                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userProfessional)
                    val mIntent1 = Intent(this@SelectRoleActivity, WebSocketService::class.java)
                    startService(mIntent1)
                }
            } else {
                toast("返回角色信息错误")
                dismissProgressDialog()
            }
        }, "正在加载...", true, false)
    }

}
