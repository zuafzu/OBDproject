package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cy.obdproject.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    private fun initView() {
//        // 判断是否自动登录
//        if (SPTools.get(this@LoginActivity, "passWord", "") != "") {
//            hideSoftInput()
//            for (activity in MyApp.getActivies()) {
//                activity.finish()
//            }
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//        }
        btn_login.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        // thinkgem
//        if (et_name.text.toString() == "") {
//            toast(R.string.yhmbnwk)
//            return
//        }
//        // 123456
//        if (et_pw.text.toString() == "") {
//            toast(R.string.mmbnwk)
//            return
//        }
//        net_login(et_name.text.toString(), et_pw.text.toString())

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}
