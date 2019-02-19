package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.StringTools
import com.cy.obdproject.tools.WifiTools
import com.hhkj.cyfqrcode.WeChatCaptureActivity
import kotlinx.android.synthetic.main.activity_setting_ip.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class SettingIpActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var SEND_IP = "255.255.255.255" //发送IP
    private var listenStatus = true //接收线程的循环标识
    private var buf: ByteArray? = null
    private var receiveSocket: DatagramSocket? = null
    private var sendSocket: DatagramSocket? = null
    private var serverAddr: InetAddress? = null
    private var sendHandler = SendHandler()
    private var receiveHandler = ReceiveHandler()
    private var lock: WifiManager.MulticastLock? = null

    internal inner class ReceiveHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            listenStatus = false
            if (et_input_ip.text.toString() != stringIp) {
                et_input_ip.setText(stringIp)
                stringIp = ""
            }
            // Log.e("cyf_udp", "接收到数据了 : " + receiveInfo.toString())
        }
    }

    internal inner class SendHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // UDP报文发送成功
            Log.e("cyf_udp", "UDP报文发送成功")
        }
    }

    private var stringIp = ""
    private var mIntent2: Intent? = null
    private var mAlertDialog: AlertDialog? = null
    private var wifiTools: WifiTools? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_ip)
        initView()
        initUdp()
    }

    private fun initView() {
        setClickMethod(iv_back)
        setClickMethod(btn_crcode)
        setClickMethod(btn_copy1)
        setClickMethod(btn_copy2)
        setClickMethod(btn_jumpWifi)
        setClickMethod(btn_openWifi)
        setClickMethod(btn_seeIp)
        setClickMethod(btn_ok)

        mIntent2 = Intent(this, SocketService::class.java)
        wifiTools = WifiTools(this)

        stopService(mIntent2)
        btn_ok.text = "下一步"
        iv_back.visibility = View.INVISIBLE
    }

    private fun initUdp() {
        val manager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        lock = manager!!.createMulticastLock("test wifi")
        lock!!.acquire()
        UdpSendThread().start()
        UdpReceiveThread().start()
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        showProgressDialog()
        Handler().postDelayed({
            changeWifiBtnState(wifiTools!!.isWifiApEnabled)
            btn_jumpWifi.visibility = View.VISIBLE
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                btn_openWifi.text = "当前版本无法自动开启热点"
                btn_openWifi.isClickable = false
                btn_openWifi.backgroundResource = R.drawable.shape_btn_gary
            }
            if (SocketService.getIntance() != null &&
                    SocketService.getIntance()!!.isConnected() &&
                    SocketService.isConnected) {
                btn_ok.text = "断开OBD"
            } else {
                btn_ok.text = "下一步"
            }
            if (intent.hasExtra("isSave")) {
                if (intent.getBooleanExtra("isSave", false)) {
                    iv_back.visibility = View.VISIBLE
                    btn_ok.text = "保存"
                }
            }
            if (SPTools[this@SettingIpActivity, Constant.IP, ""].toString() != "") {
                et_input_ip.setText(SPTools[this@SettingIpActivity, Constant.IP, ""].toString())
            }
            if (SPTools[this@SettingIpActivity, Constant.SSID, ""].toString() != "") {
                et_name.setText(SPTools[this@SettingIpActivity, Constant.SSID, ""].toString())
            }
            if (SPTools[this@SettingIpActivity, Constant.SSPW, ""].toString() != "") {
                et_pw.setText(SPTools[this@SettingIpActivity, Constant.SSPW, ""].toString())
            }
            dismissProgressDialog()
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        lock!!.release()
        //停止接收线程，关闭套接字连接
        listenStatus = false
        if (receiveSocket != null) {
            receiveSocket!!.close()
        }
    }

    private fun changeWifiBtnState(isOpen: Boolean) {
        if (isOpen) {
            tv_wifiState.text = "热点是否开启：已开启"
        } else {
            tv_wifiState.text = "热点是否开启：未开启"
        }
    }

    private fun showAutoDialog(message: String) {
        mAlertDialog = AlertDialog.Builder(this).setTitle("提示").setMessage(message).setCancelable(false).setPositiveButton("自动获取") { _, _ ->
            val list = wifiTools!!.connectedHotIP
            if (list.size > 1) {
                et_input_ip.setText(list[1])
            } else {
                showAutoDialog("请确保手机Wi-Fi已经关闭，热点已经打开，并且有且只有一个OBD设备已经连接。\n" +
                        "自动获取已失败，请手动获取。")
            }
        }.setNegativeButton("手动获取") { _, _ ->
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            //打开网络共享与热点设置页面
            val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$TetherSettingsActivity")
            intent.component = comp
            startActivity(intent)
        }.show()
    }

    @SuppressLint("SetTextI18n")
    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_copy1" -> {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // 将文本内容放到系统剪贴板里。
                cm.text = et_name.text.toString()
                toast("复制成功")
            }
            "btn_copy2" -> {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // 将文本内容放到系统剪贴板里。
                cm.text = et_pw.text.toString()
                toast("复制成功")
            }
            "btn_crcode" -> {
                WeChatCaptureActivity.startQR(this) {
                    // {"ssid":"StationTest1","pw":""}
                    val json = JSONObject(it)
                    val ssid = json.optString("ssid")
                    val pw = json.optString("pw")
                    et_name.setText(ssid)
                    et_pw.setText(pw)
                }
            }
            "btn_jumpWifi" -> {
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //打开网络共享与热点设置页面
                val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$TetherSettingsActivity")
                intent.component = comp
                startActivity(intent)
            }
            "btn_openWifi" -> {
                if (et_name.text.toString() == "") {
                    toast("热点名称不能为空")
                    return
                }
                if (et_pw.text.toString() != "" && et_pw.text.toString().length < 8) {
                    toast("热点密码长度不能小于8位")
                    return
                }
                val isOpen = wifiTools!!.setWifiApEnabled(true, et_name.text.toString(), et_pw.text.toString())
                changeWifiBtnState(isOpen)
            }
            "btn_seeIp" -> {
                showAutoDialog("请确保手机Wi-Fi已经关闭，热点已经打开，并且有且只有一个OBD设备已经连接。\n" +
                        "自动获取会有获取失败的可能，如有失败请手动获取。")
            }
            "btn_ok" -> {
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
                if (btn_ok.text == "保存") {
                    SPTools.put(this@SettingIpActivity, Constant.IP, et_input_ip.text.toString())
                    SPTools.put(this@SettingIpActivity, Constant.SSID, et_name.text.toString())
                    SPTools.put(this@SettingIpActivity, Constant.SSPW, et_pw.text.toString())
                    myApp.publicUnit.setScriptManagerParam(true, true, et_input_ip.text.toString(), Constant.mDstPort)
                    toast("保存成功")
                    finish()
                } else {
                    showProgressDialog()
                    if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                        stopService(mIntent2)
                        Handler().postDelayed({
                            btn_ok.text = "下一步"
                            dismissProgressDialog()
                        }, 2000)
                    } else {
                        stopService(mIntent2)
                        Handler().postDelayed({
                            Constant.mDstName = et_input_ip.text.toString()
                            startService(mIntent2)
                            Handler().postDelayed({
                                dismissProgressDialog()
                                if (SocketService.getIntance() != null &&
                                        SocketService.getIntance()!!.isConnected()) {
                                    this.finish()
                                    SPTools.put(this@SettingIpActivity, Constant.IP, et_input_ip.text.toString())
                                    SPTools.put(this@SettingIpActivity, Constant.SSID, et_name.text.toString())
                                    SPTools.put(this@SettingIpActivity, Constant.SSPW, et_pw.text.toString())
                                    myApp.publicUnit.setScriptManagerParam(true, true, et_input_ip.text.toString(), Constant.mDstPort)
                                    startActivity(Intent(this@SettingIpActivity, SelectCarTypeActivity::class.java))
                                } else {
                                    toast("连接失败，请重试")
                                }
                            }, 2000)
                        }, 1000)
                    }
                }
            }
        }
    }

    /*
     *   UDP数据发送线程
     * */ inner class UdpSendThread : Thread() {
        override fun run() {
            try {
                buf = "send".toByteArray() // 创建DatagramSocket对象，使用随机端口
                sendSocket = DatagramSocket()
                serverAddr = InetAddress.getByName(SEND_IP)
                val outPacket = DatagramPacket(buf, buf!!.size, serverAddr, Constant.SEND_PORT)
                sendSocket!!.send(outPacket)
                sendSocket!!.close()
                sendHandler.sendEmptyMessage(1)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    /*
    *   UDP数据接收线程
    * */ inner class UdpReceiveThread : Thread() {
        override fun run() {
            try {
                receiveSocket = DatagramSocket(Constant.RECEIVE_PORT)
                while (listenStatus) {
                    val inBuf = ByteArray(1024)
                    val inPacket = DatagramPacket(inBuf, inBuf.size)
                    receiveSocket!!.receive(inPacket)
                    val length = inPacket.length
                    val mbyte = inPacket.data
                    var message = StringTools.byte2hex(mbyte).substring(0, length * 2)
                    if (message.toLowerCase().startsWith("aa") && message.toLowerCase().endsWith("bb") && length == 8) {
                        message = message.substring(2, message.length - 2)
                        for (i in 0 until 4) {
                            val hex = message.substring(i * 2, i * 2 + 2)
                            stringIp = if (stringIp == "") {
                                "" + Integer.parseInt(hex, 16)
                            } else {
                                stringIp + "." + Integer.parseInt(hex, 16)
                            }
                        }
                    }
                    Log.e("cyf_udp", "stringIp : $stringIp")
                    // 判断协议里面IP地址和真实IP地址是否一致
                    if (inPacket.address.hostAddress == stringIp) {
                        receiveHandler.sendEmptyMessage(1)
                    } else {
                        stringIp = ""
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}
