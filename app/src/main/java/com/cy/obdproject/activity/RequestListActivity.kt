package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import kotlinx.android.synthetic.main.activity_request_list.*

class RequestListActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_list)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)

        startActivity(Intent(this@RequestListActivity,MainActivity::class.java))
        finish()
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
        }

    }
}
