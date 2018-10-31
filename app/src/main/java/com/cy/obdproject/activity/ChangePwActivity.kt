package com.cy.obdproject.activity

import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.R.id.*
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.url.Urls
import kotlinx.android.synthetic.main.activity_change_pw.*
import org.jetbrains.anko.toast

class ChangePwActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pw)
        initView()
    }

    private fun initView() {
        iv_back.setOnClickListener {
            finish()
        }
        btn_submit.setOnClickListener {
            if (et_pw0.text.toString() == "") {
                toast("原始密码不能为空")
                return@setOnClickListener
            }
            if (et_pw1.text.toString() == "") {
                toast("新密码不能为空")
                return@setOnClickListener
            }
            if (et_pw1.text.toString() != et_pw2.text.toString()) {
                toast("两次密码设置需要一致")
                return@setOnClickListener
            }
            net_changePwd()
        }
    }

    private fun net_changePwd() {
        val map = hashMapOf<String, String>()
        map["old"] = et_pw0.text.toString()
        map["new"] = et_pw1.text.toString()
        NetTools.net(map, Urls().changePwd, this) { response ->
            toast("" + response.msg!!)
            finish()
        }
    }
}
