package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.StringTools
import com.cy.obdproject.tools.WifiTools
import kotlinx.android.synthetic.main.activity_connent_obd.*
import org.jetbrains.anko.toast

class ConnentOBD2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var mIntent2: Intent? = null
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
        wifiTools = WifiTools(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (wifiTools!!.isWifiApEnabled) {
            tv_wifiState.text = "热点是否开启：已开启"
        } else {
            tv_wifiState.text = "热点是否开启：未开启"
        }

        if (SocketService.getIntance() != null &&
                SocketService.getIntance()!!.isConnected() &&
                SocketService.isConnected) {
            btn_ok.text = "断开OBD"
            et_input_ip.setText(Constant.mDstName)
        } else {
            btn_ok.text = getString(R.string.a_qdobd)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_openWifi" -> {
                var isOpen = wifiTools!!.setWifiApEnabled(true,"","")
                if (isOpen) {
                    tv_wifiState.text = "热点是否开启：已开启"
                    ll_ip.visibility = View.VISIBLE
                    ll_input_ip.visibility = View.VISIBLE
                    ll_ok.visibility = View.VISIBLE

                } else {
                    tv_wifiState.text = "热点是否开启：未开启"
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
                showProgressDialog()
                if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                    stopService(mIntent2)
                    Handler().postDelayed({
                        btn_ok.text = "连接OBD"
                        dismissProgressDialog()
                    }, 2000)
                } else {
                    //
                    if (!wifiTools!!.isWifiApEnabled) {
                        toast("请先开启热点")
                        dismissProgressDialog()
                        return
                    }
                    // 建立长连接
                    if (!StringTools.isIP(et_input_ip.text.toString())) {
                        toast("IP地址格式不正确")
                        dismissProgressDialog()
                        return
                    }
                    Constant.mDstName = et_input_ip.text.toString()
                    myApp.publicUnit.setScriptManagerParam(true, true, et_input_ip.text.toString(), Constant.mDstPort)
                    startService(mIntent2)
                    // 等待长连接建立，然后开始发送连接obd
                    Handler().postDelayed({
                        // 发送开始信息
                        if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                            SocketService.isConnected = true
                            this.finish()
                            toast("连接成功")
                            dismissProgressDialog()
                        } else {
                            stopService(mIntent2)
                        }
                    }, 5000)
                }
            }
        }
    }
}
