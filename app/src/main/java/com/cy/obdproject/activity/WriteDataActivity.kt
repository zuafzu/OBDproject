package com.cy.obdproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import kotlinx.android.synthetic.main.activity_write_data.*

class WriteDataActivity : BaseActivity(), BaseActivity.ClickMethoListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)
        initView()
    }


    private fun initView() {
        setClickMethod(iv_back)
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
        }
    }
}
