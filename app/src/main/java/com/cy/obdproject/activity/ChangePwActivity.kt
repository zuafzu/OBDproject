package com.cy.obdproject.activity

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
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
        val strUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1545047587086&di=ed48ea529b1cef3a981a8ea02b70ed61&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F010ae45a3626b7a80121db80f25c77.jpg%401280w_1l_2o_100sh.jpg"
        Glide.with(this).load(strUrl).apply(RequestOptions.bitmapTransform(CircleCrop()).error(R.mipmap.ic_head)).into(iv_head)
        tv_name.text = SPTools[this@ChangePwActivity, Constant.USERNAME, ""].toString()
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
