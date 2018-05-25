package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.LoginBean
import com.cy.obdproject.constant.Constant
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

    private fun initView() {
        if(WebSocketService.getIntance()!=null){
            WebSocketService.getIntance()!!.close()
        }
        setClickMethod(btn_login)
        // 判断是否自动登录
        if (SPTools[this@LoginActivity, Constant.ISLOGIN, ""] != "") {
            if (SPTools[this@LoginActivity, Constant.USERTYPE, 0] == Constant.userNormal) {
                for (i in 0 until (application as MyApp).activityList.size) {
                    (application as MyApp).activityList[i].finish()
                }
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                showProgressDialog()
                val mIntent1 = Intent(this@LoginActivity, WebSocketService::class.java)
                startService(mIntent1)
            }
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
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
        NetTools.net(map, Urls().auth_login, this, { response ->
            Log.e("zj", "auth_login = " + response!!.data)
            var loginBean = Gson().fromJson(response.data, LoginBean::class.java)

            SPTools.put(this@LoginActivity, Constant.USERNAME, "" + et_name.text.toString())
            SPTools.put(this@LoginActivity, Constant.TOKEN, "" + loginBean.token)
            SPTools.put(this@LoginActivity, Constant.USERID, "" + loginBean.userId)

            var list = ArrayList<String>()
            list.addAll(loginBean.userType.toLowerCase().replace("s", "受控端").replace("z", "专家端").split(","))

            if (list.size == 1) {
                if ("受控端" == list!![0]) {
                    SPTools.put(this@LoginActivity, Constant.USERTYPE, Constant.userNormal)
                    startActivity(Intent(this@LoginActivity, SelectCarTypeActivity::class.java))
                    finish()
                } else if ("专家端" == list[0]) {
                    SPTools.put(this@LoginActivity, Constant.USERTYPE, Constant.userProfessional)
                    val mIntent1 = Intent(this@LoginActivity, WebSocketService::class.java)
                    startService(mIntent1)
                    // startActivity(Intent(this@LoginActivity, RequestListActivity::class.java))
                }

            } else if (list.size > 1) {
                var intent = Intent(Intent(this@LoginActivity, SelectRoleActivity::class.java))
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
