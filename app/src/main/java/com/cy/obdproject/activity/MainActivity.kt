package com.cy.obdproject.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.cy.obdproject.R
import com.google.gson.Gson
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.Callback
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response

import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), View.OnClickListener {

    //    var loginBean: LoginBean? = null
    var items = "221,222,223,224,225,226"
    var homes: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()


        //        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                startActivity(new Intent(MainActivity.this, MainTestActivity.class));
        //            }
        //        });
    }

    private fun initView() {
        ll_main1.setOnClickListener(this)
        ll_main2.setOnClickListener(this)
        ll_main3.setOnClickListener(this)
        ll_main4.setOnClickListener(this)
        ll_main5.setOnClickListener(this)
        ll_main6.setOnClickListener(this)


//        var data = SPTools.get(this, "login", "").toString()
//        loginBean = Gson().fromJson<LoginBean>(data, LoginBean::class.java)

        tv_title.text = getString(R.string.app_name)


        homes = ArrayList()
        homes = items.split(",")
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        recyclerview.adapter = HomeAdapter()

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
//            ibtn_setting.id -> {
//                AlertDialog.Builder(this).setItems(R.array.setting, object : DialogInterface.OnClickListener {
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        when (p1) {
//                            0 -> {
//                                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
//                            }
//                            1 -> {
//                                AlertDialog.Builder(this@MainActivity).
//                                        setTitle("提示").
//                                        setMessage("确认退出当前账号吗？").
//                                        setPositiveButton("确认", object : DialogInterface.OnClickListener {
//                                            override fun onClick(p0: DialogInterface?, p1: Int) {
//                                                finish()
//                                            }
//                                        }).setNegativeButton("取消", object : DialogInterface.OnClickListener {
//                                    override fun onClick(p0: DialogInterface?, p1: Int) {
//
//                                    }
//                                }).show()
//                            }
//                        }
//                    }
//                }).show()
//            }
            ll_main1.id -> {//读基本信息
                startActivity(Intent(this@MainActivity, ReadBaseInfoActivity::class.java))
            }
            ll_main2.id -> {//写基本信息
                startActivity(Intent(this@MainActivity, WriteBaseInfoActivity::class.java))
            }
            ll_main3.id -> {

//                    startActivity(Intent(this@MainActivity, MainLL3Activity::class.java))
            }
            ll_main4.id -> {

//                    startActivity(Intent(this@MainActivity, MainLL4Activity::class.java))

            }
            ll_main5.id -> {
//                startActivity(Intent(this@MainActivity, MainLL5Activity::class.java))

            }
            ll_main6.id -> {
                startActivity(Intent(this@MainActivity, MainTestActivity::class.java))

            }
        }
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        AlertDialog.Builder(this).setTitle("提示").setMessage("确认退出吗？").setPositiveButton("确认", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }
        }).setNegativeButton("取消", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {

            }
        }).show()
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
                    holder.ll_main!!.setOnClickListener { onClick(ll_main1) }
                    holder.textView!!.text = "读基本信息"
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "222" -> {
                    holder.ll_main!!.setOnClickListener { onClick(ll_main2) }
                    holder.textView!!.text = "写基本信息"
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "223" -> {
                    holder.ll_main!!.setOnClickListener { onClick(ll_main3) }
                    holder.textView!!.text = "故障代码"
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#76c2af"))
                }
                "224" -> {
                    holder.ll_main!!.setOnClickListener { onClick(ll_main4) }
                    holder.textView!!.text = "动态数据"
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "225" -> {
                    holder.ll_main!!.setOnClickListener { onClick(ll_main5) }
                    holder.textView!!.text = "IO测试"
                    holder.imageView!!.setImageResource(R.mipmap.ic_launcher_round)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#76c2af"))
                }
                "226" -> {
                    holder.ll_main!!.setOnClickListener { onClick(ll_main5) }
                    holder.textView!!.text = "刷写数据"
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
