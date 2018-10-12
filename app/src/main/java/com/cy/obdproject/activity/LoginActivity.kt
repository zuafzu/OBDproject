package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.LoginBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        if (WebSocketService.getIntance() != null) {
            WebSocketService.getIntance()!!.close()
        }
        setClickMethod(btn_login)
        setClickMethod(tv_setting)
        // 判断是否自动登录
        if (SPTools[this@LoginActivity, Constant.USERNAME, ""] != "") {
            // 自动输入用户名和密码
            et_name.setText("" + SPTools[this@LoginActivity, Constant.USERNAME, ""])
            et_pw.setText("" + SPTools[this@LoginActivity, Constant.PASSWORD, ""])
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_setting" -> {
//                val mIntent = Intent(this@LoginActivity, ConnentOBDActivity::class.java)
//                mIntent.putExtra("isSave", true)
//                startActivity(mIntent)
                val mIntent = Intent(this@LoginActivity, SettingIpActivity::class.java)
                mIntent.putExtra("isSave", true)
                startActivity(mIntent)
            }
            "btn_login" -> {
                if (et_name.text.toString() == "") {
                    toast(R.string.yhmbnwk)
                    return
                }
                if (et_pw.text.toString() == "") {
                    toast(R.string.mmbnwk)
                    return
                }
                net_login(et_name.text.toString(), et_pw.text.toString())
            }
        }
    }

    private fun net_login(username: String, pwd: String) {
        val map = hashMapOf<String, String>()
        map["username"] = username
        map["pwd"] = pwd
        map["loginType"] = ""
        NetTools.net(map, Urls().auth_login, this, { response ->
            Log.e("zj", "auth_login = " + response!!.data)
            val loginBean = Gson().fromJson(response.data, LoginBean::class.java)
            WebSocketService.spaceTime = loginBean.heartBeat!!.toLong() * 1000
            WebSocketService.outTime = 2 * ((loginBean.threshold!!.toLong() - loginBean.heartBeat!!.toLong()) * 1000)// 超时时间乘以2，增大容错空间

            SPTools.put(this@LoginActivity, Constant.USERNAME, "" + et_name.text.toString())
            SPTools.put(this@LoginActivity, Constant.TOKEN, "" + loginBean.token)
            SPTools.put(this@LoginActivity, Constant.USERID, "" + loginBean.userId)
            SPTools.put(this@LoginActivity, Constant.PASSWORD, "" + et_pw.text.toString())

            val list = ArrayList<String>()
            list.addAll(loginBean.userType.toLowerCase().replace("s", "受控端").replace("z", "专家端").split(","))

            if (list.size == 1) {
                if ("受控端" == list!![0]) {
                    SPTools.put(this@LoginActivity, Constant.USERTYPE, Constant.userNormal)
                    if (SPTools[this@LoginActivity, Constant.IP, ""].toString() == "") {
                        // 第一次登录设置IP
                        // startActivity(Intent(this@LoginActivity, ConnentOBDActivity::class.java))
                        startActivity(Intent(this@LoginActivity, SettingIpActivity::class.java))
                        finish()
                    } else {
                        // 默认连接
                        val mIntent2 = Intent(this, SocketService::class.java)
                        stopService(mIntent2)
                        Handler().postDelayed({
                            Constant.mDstName = SPTools[this@LoginActivity, Constant.IP, ""].toString()
                            myApp.publicUnit.setScriptManagerParam(true, true, Constant.mDstName, Constant.mDstPort)
                            startService(mIntent2)
                            Handler().postDelayed({
                                dismissProgressDialog()
                                if (SocketService.getIntance() != null &&
                                        SocketService.getIntance()!!.isConnected()) {
                                    this.finish()
                                    startActivity(Intent(this@LoginActivity, SelectCarTypeActivity::class.java))
                                } else {
                                    toast("请先确认连接设备")
                                    // startActivity(Intent(this@LoginActivity, ConnentOBDActivity::class.java))
                                    startActivity(Intent(this@LoginActivity, SettingIpActivity::class.java))
                                    finish()
                                }
                            }, 2000)
                        }, 1000)
                    }
                } else if ("专家端" == list[0]) {
                    SPTools.put(this@LoginActivity, Constant.USERTYPE, Constant.userProfessional)
                    val mIntent1 = Intent(this@LoginActivity, WebSocketService::class.java)
                    startService(mIntent1)
                    // startActivity(Intent(this@LoginActivity, RequestListActivity::class.java))
                }

            } else if (list.size > 1) {
                var intent = Intent(Intent(this@LoginActivity, SelectRoleActivity::class.java))
                intent.putExtra("username", et_name.text.toString())
                intent.putExtra("password", et_pw.text.toString())
                intent.putExtra("list", list)
                startActivity(intent)
                finish()
            } else {
                toast("返回角色信息错误")
                dismissProgressDialog()
            }
        }, "正在加载...", true, false)
    }

    override fun onBackPressed() {
        finish()
    }

}
