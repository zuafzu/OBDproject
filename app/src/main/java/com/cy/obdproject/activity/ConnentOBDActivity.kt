package com.cy.obdproject.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.R.id.*
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.StringTools
import com.cy.obdproject.tools.WifiTools
import com.cy.obdproject.worker.OBDStart1Worker
import kotlinx.android.synthetic.main.activity_connent_obd.*
import org.jetbrains.anko.toast


class ConnentOBDActivity : BaseActivity(), BaseActivity.ClickMethoListener {
    private var mIntent2: Intent? = null
    private var startWorker: OBDStart1Worker? = null
    private var wifiTools: WifiTools? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connent_obd)
        initView()
    }

    private fun initView() {
        setClickMethod(iv_back)
        setClickMethod(btn_openWifi)
        setClickMethod(btn_seeIp)
        setClickMethod(btn_ok)

        mIntent2 = Intent(this, SocketService::class.java)
        startWorker = OBDStart1Worker()
        wifiTools = WifiTools(this)

    }

    override fun onResume() {
        super.onResume()
        if (wifiTools!!.isWifiApEnabled) {
            tv_wifiState.text = "热点是否开启：已开启"
        } else {
            tv_wifiState.text = "热点是否开启：未开启"
        }

        if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
            btn_ok.text = "断开OBD"
        } else {
            btn_ok.text = "启动OBD"
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_openWifi" -> {
                var isOpen = wifiTools!!.setWifiApEnabled(true)
                if (isOpen) {
                    tv_wifiState.text = "热点是否开启：已开启"
                    ll_ip.visibility = View.VISIBLE
                    ll_input_ip.visibility = View.VISIBLE
                    ll_ok.visibility = View.VISIBLE

                } else {
                    tv_wifiState.text = "热点是否开启：未开启"
                    ll_ip.visibility = View.GONE
                    ll_input_ip.visibility = View.GONE
                    ll_ok.visibility = View.GONE

                }
            }
            "btn_seeIp" -> {
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //打开网络共享与热点设置页面
                val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$TetherSettingsActivity")
                intent.component = comp
                startActivity(intent)
            }
            "btn_ok" -> {
                if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                    stopService(mIntent2)
                } else {
                    // 建立长连接
                    if (!StringTools.isIP(et_input_ip.text.toString())) {
                        toast("IP地址格式不正确")
                        return
                    }
                    Constant.mDstName = et_input_ip.text.toString()
                    startService(mIntent2)
                    startWorker!!.init(this@ConnentOBDActivity, { data ->
                        this.finish()
                        toast(data)
                        dismissProgressDialog()
                    })
                    Handler().postDelayed({
                        // 发送开始信息
                        if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                            startWorker!!.start()
                        } else {
                            stopService(mIntent2)
                        }
                    }, 5000)
                }
            }
        }
    }
}
