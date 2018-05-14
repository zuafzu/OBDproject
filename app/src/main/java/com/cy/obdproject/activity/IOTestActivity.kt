package com.cy.obdproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.cy.obdproject.R
import kotlinx.android.synthetic.main.activity_iotest.*

class IOTestActivity : AppCompatActivity() , View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iotest)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            iv_back.id -> {
                finish()
            }

        }
    }
}
