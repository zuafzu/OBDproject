package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.LoginBean
import com.cy.obdproject.constant.Constant
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
        setClickMethod(btn_login)

        // 判断是否自动登录
        if (SPTools.get(this@LoginActivity, Constant.ISLOGIN, "") != "") {
            for (i in 0 until (application as MyApp).activityList.size) {
                (application as MyApp).activityList[i].finish()
            }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
        NetTools.net(map, Urls().auth_login, this) { response ->
            Log.e("zj", "auth_login = " + response.data)
            var loginBean = Gson().fromJson(response.data, LoginBean::class.java)

            SPTools.put(this@LoginActivity, Constant.USERNAME, "" + et_name.text.toString())
            SPTools.put(this@LoginActivity, Constant.TOKEN, "" + loginBean.token)
            SPTools.put(this@LoginActivity, Constant.USERID, "" + loginBean.userId)

            var intent = Intent(Intent(this@LoginActivity, SelectRoleActivity::class.java))
            intent.putExtra("userType", "" + loginBean.userType)
            startActivity(intent)
            finish()
        }
    }


}
