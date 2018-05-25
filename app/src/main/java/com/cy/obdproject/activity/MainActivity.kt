package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.app.MyApp
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.ErrorCodeBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.FastBlurUtil
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.WifiTools
import com.cy.obdproject.worker.OBDStart1Worker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast


class MainActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var mExitTime: Long = 0

    private var mIntent1: Intent? = null
    private var mIntent2: Intent? = null
    private var startWorker: OBDStart1Worker? = null
    private var wifiTools: WifiTools? = null

    private var items = "221,222,223,224,225,226"
    var homes: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onResume() {
        super.onResume()
        if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
            tv_obd_state.text = "已连接"
            tv_connnect_obd.text = "断开OBD"
        } else {
            tv_obd_state.text = "未连接"
            tv_connnect_obd.text = "连接OBD"
        }
        if (WebSocketService.getIntance() != null && WebSocketService.getIntance()!!.isConnected()) {
            tv_ycxz.text = "断开协助"
        }
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            ibtn_setting.visibility = View.VISIBLE
        } else {
            ibtn_setting.visibility = View.INVISIBLE
            ll_obd.visibility = View.INVISIBLE
        }
        dismissProgressDialog()
    }

    private fun initView() {
        // 获取需要被模糊的原图bitmap
        val res = resources
        val scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_background)

        //        scaledBitmap为目标图像，10是缩放的倍数（越大模糊效果越高）
        val blurBitmap = FastBlurUtil.toBlur(scaledBitmap, 2)
        iv_background.scaleType = ImageView.ScaleType.CENTER_CROP
        iv_background.setImageBitmap(blurBitmap)

        SPTools.put(this@MainActivity, Constant.ISLOGIN, "1")
        if (null != SelectRoleActivity.INSTANCE) {
            SelectRoleActivity.INSTANCE!!.finish()
        }
        if (null != SelectCarTypeActivity.INSTANCE) {
            SelectCarTypeActivity.INSTANCE!!.finish()
        }
        if (null != SelectSystemActivity.INSTANCE) {
            SelectSystemActivity.INSTANCE!!.finish()
        }

        mIntent1 = Intent(this, WebSocketService::class.java)
        mIntent2 = Intent(this, SocketService::class.java)
        startWorker = OBDStart1Worker()
        wifiTools = WifiTools(this)
        setClickMethod(tv_connnect_obd)
        setClickMethod(tv_ycxz)

        setClickMethod(ibtn_setting)

        tv_title.text = getString(R.string.app_name)
        tv_username.text = SPTools[this@MainActivity, Constant.USERNAME, ""].toString()
        tv_carName.text = SPTools[this@MainActivity, Constant.CARNAME, ""].toString()
        homes = ArrayList()
        if ("1" == SPTools[this@MainActivity, Constant.CARTYPE, ""]) {
            items = "221,222,223,224,225"
        } else if ("2" == SPTools[this@MainActivity, Constant.CARTYPE, ""]) {
            items = "226"
        }

        homes = items.split(",")
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        recyclerview.adapter = HomeAdapter()
    }

    override fun setData(data: String?) {
        var bean: ErrorCodeBean = Gson().fromJson(data, object : TypeToken<ErrorCodeBean>() {}.type)
        var carName = bean.msg
        var carType = bean.code
        tv_carName.setText(carName)
        homes = null
        homes = ArrayList()
        if ("1" == carType) {
            items = "221,222,223,224,225"
        } else if ("2" == carType) {
            items = "226"
        }

        homes = items.split(",")
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        recyclerview.adapter = HomeAdapter()

    }

    @SuppressLint("SetTextI18n")
    override fun doMethod(string: String?) {
        when (string) {
            "tv_connnect_obd" -> {//连接obd
                startActivity(Intent(this@MainActivity, ConnentOBDActivity::class.java))
            }
            "tv_ycxz" -> {//远程协作
                showProgressDialog()
                if ("远程协助" == tv_ycxz.text) {
                    // 正常逻辑，后期开放
//                    if (SocketService.getIntance() == null || !SocketService.getIntance()!!.isConnected()) {
//                        toast("请先连接obd")
//                        return
//                    }
                    startActivity(Intent(this@MainActivity, ResponseListActivity::class.java))
                } else {
                    if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
                        showProgressDialog()
                        val webSocketBean = WebSocketBean()
                        webSocketBean.s = SPTools[this@MainActivity, Constant.USERID, ""]!!.toString()
                        webSocketBean.r = SPTools[this@MainActivity, Constant.ZFORUID, ""]!!.toString()
                        webSocketBean.c = "K"
                        if (WebSocketService.getIntance() != null) {
                            WebSocketService.getIntance()!!.sendMsg(Gson().toJson(webSocketBean))
                        }
                    } else {
//                        tv_ycxz.text = "远程协助"
//                        stopService(mIntent1)
                    }
                }
            }
            "ll_main1" -> {//读基本信息
                startActivity(Intent(this@MainActivity, ReadBaseInfoActivity::class.java))
            }
            "ll_main2" -> {//写基本信息
                startActivity(Intent(this@MainActivity, WriteBaseInfoActivity::class.java))
            }
            "ll_main3" -> {//故障代码
                startActivity(Intent(this@MainActivity, ErrorCodeActivity::class.java))
            }
            "ll_main4" -> {//动态数据
                startActivity(Intent(this@MainActivity, DynamicDataActivity::class.java))
            }
            "ll_main5" -> {//IO测试
                startActivity(Intent(this@MainActivity, IOTestActivity::class.java))
            }
            "ll_main6" -> {
                startActivity(Intent(this@MainActivity, WriteDataActivity::class.java))
            }
            "ibtn_setting" -> {
                AlertDialog.Builder(this).setTitle("提示").setMessage("确认退出当前账号？").setPositiveButton("确认") { _, _ ->
                    SPTools.clear(this@MainActivity)
                    for (i in 0 until (application as MyApp).activityList.size) {
                        (application as MyApp).activityList[i].finish()
                    }
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }.setNegativeButton("取消") { _, _ -> }.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            stopService(mIntent1)
        }
        stopService(mIntent2)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
                finish()
            } else {
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    toast("再按一次退出程序")
                    mExitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    internal inner class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val holder = MyViewHolder(LayoutInflater.from(
                    this@MainActivity).inflate(R.layout.item_main, parent,
                    false))
            return holder
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val lp = LinearLayout.LayoutParams(matchParent, matchParent)
            lp.bottomMargin = dip(8)
            lp.topMargin = dip(8)
            if (position % 2 == 0) {
                lp.leftMargin = dip(16)
                lp.rightMargin = dip(8)
            } else {
                lp.leftMargin = dip(8)
                lp.rightMargin = dip(16)
            }
            holder.ll_main!!.layoutParams = lp
            when (homes!![position]) {
                "221" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main1")
                        doMethod("ll_main1")
                    }
                    holder.textView!!.text = getString(R.string.djbxx)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card1)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "222" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main2")
                        doMethod("ll_main2")
                    }
                    holder.textView!!.text = getString(R.string.xjbxx)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card2)//4f5d73
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "223" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main3")
                        doMethod("ll_main3")
                    }
                    holder.textView!!.text = getString(R.string.gzdm)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card3)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "224" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main4")
                        doMethod("ll_main4")
                    }
                    holder.textView!!.text = getString(R.string.dtsj)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card4)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "225" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main5")
                        doMethod("ll_main5")
                    }
                    holder.textView!!.text = getString(R.string.iotest)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card5)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "226" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main6")
                        doMethod("ll_main6")
                    }
                    holder.textView!!.text = getString(R.string.sxwj)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card6)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
            }
        }

        override fun getItemCount(): Int {
            return homes!!.size
        }

        internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            var textView: TextView? = null
            var imageView: ImageView? = null
            var ll_main: LinearLayout? = null

            init {
                textView = view.findViewById(R.id.textView) as TextView
                imageView = view.findViewById(R.id.imageView) as ImageView
                ll_main = view.findViewById(R.id.ll_main) as LinearLayout
            }
        }
    }

}
