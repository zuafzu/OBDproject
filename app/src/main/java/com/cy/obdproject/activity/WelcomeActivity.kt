package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.FileUtils
import org.jetbrains.anko.toast
import java.io.File

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val file = File(Constant.xlsFilePath)
        file.deleteOnExit()
        Handler().postDelayed({
            FileUtils.getInstance(this).copyAssetsToSD(Constant.assXlsFilePath, Constant.xlsFilePathSub).setFileOperateCallback(object : FileUtils.FileOperateCallback {
                override fun onSuccess() {
                    startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    overridePendingTransition(0, 0)
                }

                override fun onFailed(error: String?) {
                    toast("启动异常，请重新启动")
                    finish()
                }

            })
        }, 1000)
    }
}
