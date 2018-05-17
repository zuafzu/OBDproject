package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Intent
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
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.WifiTools
import com.cy.obdproject.worker.OBDStart1Worker
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

    var items = "221,222,223,224,225,226"
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
    }

    private fun initView() {
        mIntent1 = Intent(this, WebSocketService::class.java)
        mIntent2 = Intent(this, SocketService::class.java)
        startWorker = OBDStart1Worker()
        wifiTools = WifiTools(this)
        setClickMethod(tv_connnect_obd)
        setClickMethod(tv_ycxz)
        setClickMethod(ll_main1)
        setClickMethod(ll_main2)
        setClickMethod(ll_main3)
        setClickMethod(ll_main4)
        setClickMethod(ll_main5)
        setClickMethod(ll_main6)
        setClickMethod(ibtn_setting)

        tv_title.text = getString(R.string.app_name)
        homes = ArrayList()
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
                if ("远程协助" == tv_ycxz.text) {
                    tv_ycxz.text = "断开协助"
                    startService(mIntent1)
                } else {
                    tv_ycxz.text = "远程协助"
                    stopService(mIntent1)
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
                AlertDialog.Builder(this).setTitle("提示").setMessage("确认退出吗？").setPositiveButton("确认") { _, _ ->
                    SPTools.clear(this@MainActivity)
                    for (i in 0 until (application as MyApp).activityList.size) {
                        (application as MyApp).activityList[i].finish()
                    }
                }.setNegativeButton("取消") { _, _ -> }.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mIntent1)
        stopService(mIntent2)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                toast("再按一次退出程序")
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {

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
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "222" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main2")
                        doMethod("ll_main2")
                    }
                    holder.textView!!.text = getString(R.string.xjbxx)
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "223" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main3")
                        doMethod("ll_main3")
                    }
                    holder.textView!!.text = getString(R.string.gzdm)
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#76c2af"))
                }
                "224" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main4")
                        doMethod("ll_main4")
                    }
                    holder.textView!!.text = getString(R.string.dtsj)
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "225" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main5")
                        doMethod("ll_main5")
                    }
                    holder.textView!!.text = getString(R.string.iotest)
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#76c2af"))
                }
                "226" -> {
                    holder.ll_main!!.setOnClickListener {
                        sendClick(this@MainActivity.localClassName, "ll_main6")
                        doMethod("ll_main6")
                    }
                    holder.textView!!.text = getString(R.string.sxwj)
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#76c2af"))
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
