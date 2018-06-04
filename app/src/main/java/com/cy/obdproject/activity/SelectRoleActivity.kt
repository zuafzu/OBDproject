package com.cy.obdproject.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.LoginBean
import com.cy.obdproject.constant.Constant
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

//                if ("受控端" == list!![0].toString()) {
//                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userNormal)
//                    startActivity(Intent(this@SelectRoleActivity, SelectCarTypeActivity::class.java))
//                    finish()
//                } else if ("专家端" == list!![0].toString()) {
//                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userProfessional)
//                    startActivity(Intent(this@SelectRoleActivity, RequestListActivity::class.java))
//                    finish()
//                }
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

            SPTools.put(this@SelectRoleActivity, Constant.USERNAME, "" + username)
            SPTools.put(this@SelectRoleActivity, Constant.TOKEN, "" + loginBean.token)
            SPTools.put(this@SelectRoleActivity, Constant.USERID, "" + loginBean.userId)

            var list = ArrayList<String>()
            list.addAll(loginBean.userType.toLowerCase().replace("s", "受控端").replace("z", "专家端").split(","))

            if (list.size == 1) {
                if ("受控端" == list!![0].toString()) {
                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userNormal)
                    startActivity(Intent(this@SelectRoleActivity, SelectCarTypeActivity::class.java))
                    finish()
                } else if ("专家端" == list!![0].toString()) {
                    SPTools.put(this@SelectRoleActivity, Constant.USERTYPE, Constant.userProfessional)
                    startActivity(Intent(this@SelectRoleActivity, RequestListActivity::class.java))
                    finish()
                }
            } else {
                toast("返回角色信息错误")
                dismissProgressDialog()
            }
        }, "正在加载...", true, true)
    }

}
