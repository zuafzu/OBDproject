package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import org.jetbrains.anko.toast

class TestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 默认连接
        val mIntent2 = Intent(this, SocketService::class.java)
        stopService(mIntent2)
        Handler().postDelayed({
            Constant.mDstName = "192.168.43.56"
            startService(mIntent2)
            Handler().postDelayed({
                dismissProgressDialog()
                if (SocketService.getIntance() != null &&
                        SocketService.getIntance()!!.isConnected()) {
                    this.finish()
                    startActivity(Intent(this@TestActivity, SelectCarTypeActivity::class.java))
                } else {
                    toast("请先确认连接设备")
                    startActivity(Intent(this@TestActivity, ConnentOBDActivity::class.java))
                    finish()
                }
            }, 2000)
        }, 1000)
    }
}
