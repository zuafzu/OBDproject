package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.ErrorCodeBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.FastBlurUtil
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.WifiTools
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var wifiTools: WifiTools? = null
    private var modeMap = HashMap<String, String>()

    private var isShowToast = false
    private var mData = ""
    private var items = ""
    var homes: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initOBDStart()
    }

    private fun initOBDStart() {
        if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {

        } else {
            if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                showProgressDialog()
                SocketService.isConnected = true
                isShowToast = false
                setData1("连接成功")
            } else {
                toast("OBD未连接")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
        isShowToast = false
        if (isUserConnected) {
            setData1(mData)
        }
        if (WebSocketService.getIntance() != null && WebSocketService.getIntance()!!.isConnected()) {
            tv_ycxz.text = "断开协助"
        }
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            ibtn_setting.visibility = View.VISIBLE
            iv_back.visibility = View.VISIBLE
            tv_connnect_obd.visibility = View.INVISIBLE
        } else {
            ibtn_setting.visibility = View.INVISIBLE
            iv_back.visibility = View.VISIBLE
            // ll_obd.visibility = View.INVISIBLE
            tv_connnect_obd.visibility = View.INVISIBLE
        }
        // 目前专家就是已连接，后期可优化
        if (isProfessionalConnected) {
            mData = "连接成功"
        }
        if (mData == "连接成功") {
            tv_obd_state.text = "已连接"
        } else {
            tv_obd_state.text = "未连接"
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
        if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {
            if (null != SelectCarTypeActivity.INSTANCE) {
                SelectCarTypeActivity.INSTANCE!!.finish()
            }
        }

        wifiTools = WifiTools(this)
        setClickMethod(tv_connnect_obd)
        setClickMethod(tv_ycxz)

        setClickMethod(ibtn_setting)
        setClickMethod(iv_back)

        tv_title.text = getString(R.string.app_name)
        tv_username.text = SPTools[this@MainActivity, Constant.USERNAME, ""].toString()

        val bean = ErrorCodeBean()
        // bean.code = SPTools[this, Constant.CARTYPE, "1"].toString()//车型
        bean.msg = SPTools[this, Constant.CARNAME, ""].toString()// 车名
        setData(Gson().toJson(bean))
    }

    private fun changOBDState(data: String?) {
        if (data == "连接成功") {
            tv_obd_state.text = "已连接"
            tv_connnect_obd.text = "断开OBD"
        } else {
            tv_obd_state.text = "未连接"
            tv_connnect_obd.text = "连接OBD"
        }
    }

    private fun click(string: String?) {
        if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected() && SocketService.isConnected) {
            sendClick(this@MainActivity.localClassName, string)
            doMethod(string)
        } else {
            if (isProfessionalConnected && mData == "连接成功") {
                sendClick(this@MainActivity.localClassName, string)
                doMethod(string)
            } else {
                toast("请先连接obd")
            }
        }
    }

    override fun setData(data: String?) {
        try {
            runOnUiThread {
                var bean: ErrorCodeBean = Gson().fromJson(data, object : TypeToken<ErrorCodeBean>() {}.type)
                var carName = bean.msg
                // var carType = bean.code
                tv_carName.text = carName
                homes = null
                homes = ArrayList()
                items = ""
                modeMap.clear()

                val mJsonString = myApp.publicUnit.GetUI();
                val jsonObject = JSONObject(mJsonString)

                val jsonArray1 = jsonObject.getJSONArray(carName.split(" - ")[0])
                for (i in 0 until jsonArray1.length()) {
                    val jsonObject1 = jsonArray1.optJSONObject(i)
                    val it = jsonObject1!!.keys()
                    while (it.hasNext()) {
                        val key = it.next().toString()
                        if (key == carName.split(" - ")[1]) {
                            val jsonArray = JSONArray(jsonObject1.getJSONArray(key).toString())
                            for (j in 0 until jsonArray.length()) {
                                val mJsonObject = jsonArray.optJSONObject(j)
                                val name = mJsonObject.optString("name")
                                val code = mJsonObject.optString("init_code")
                                when (name) {
                                    "基本信息" -> {
                                        val refresh_code = mJsonObject.optString("refresh_code")
                                        if (code != "" && refresh_code != "") {
                                            items += "221,"
                                            modeMap["221"] = "$code,$refresh_code"
                                        }
                                    }
                                    "参数修改" -> {
                                        val refresh_code = mJsonObject.optString("refresh_code")
                                        if (code != "" && refresh_code != "") {
                                            items += "222,"
                                            modeMap["222"] = "$code,$refresh_code"
                                        }
                                    }
                                    "故障信息" -> {
                                        val refresh_code = mJsonObject.optString("refresh_code")
                                        val read_freeze_code = mJsonObject.optString("read_freeze_code")
                                        val clear_dtc_code = mJsonObject.optString("clear_dtc_code")
                                        if (code != "" && refresh_code != "" && read_freeze_code != "" && clear_dtc_code != "") {
                                            items += "223,"
                                            modeMap["223"] = "$code,$refresh_code,$read_freeze_code,$clear_dtc_code"
                                        }
                                    }
                                    "动态数据" -> {
                                        val start_read_code = mJsonObject.optString("start_read_code")
                                        val stop_read_code = mJsonObject.optString("stop_read_code")
                                        if (code != "" && start_read_code != "" && stop_read_code != "") {
                                            items += "224,"
                                            modeMap["224"] = "$code,$start_read_code,$stop_read_code"
                                        }
                                    }
                                    "刷写" -> {
                                        val flush_code = mJsonObject.optString("flush_code")
                                        if (flush_code != "") {
                                            items += "226,"
                                            modeMap["226"] = flush_code
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (items.isNotEmpty()) {
                    items = items.substring(0, items.length - 1)
                    homes = items.split(",")
                } else {
                    homes = ArrayList()
                }
                recyclerview.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?
                recyclerview.adapter = HomeAdapter()
                setData1(mData)
            }
        } catch (e: Exception) {
            LogTools.errLog(e)
        }
        super.setData(data)
    }

    override fun setData1(data: String?) {
        Log.e("cyf111", data)
        runOnUiThread {
            mData = data!!
            changOBDState(data)
            if (isShowToast) {
                toast(data!!)
            }
            dismissProgressDialog()
        }
        super.setData1(data)
    }

    @SuppressLint("SetTextI18n")
    override fun doMethod(string: String?) {
        when (string) {
            "tv_connnect_obd" -> {//连接obd
                startActivity(Intent(this@MainActivity, ConnentOBD2Activity::class.java))
            }
            "tv_ycxz" -> {//远程协作
                // showProgressDialog()
                if ("远程协助" == tv_ycxz.text) {
                    // startActivity(Intent(this@MainActivity, ResponseListActivity::class.java))
                    // 正常逻辑，后期开放
                    if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected() && SocketService.isConnected) {
                        startActivity(Intent(this@MainActivity, ResponseListActivity::class.java))
                    } else {
                        toast("请先连接obd")
                        dismissProgressDialog()
                    }
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
                        Handler().postDelayed({
                            this@MainActivity.finish()
                        }, 3000)
                    } else {

                    }
                }
            }
            "ll_main1" -> {//读基本信息
                val mIntent = Intent(this@MainActivity, ReadBaseInfoActivity::class.java)
                mIntent.putExtra("code", modeMap["221"])
                startActivity(mIntent)
            }
            "ll_main2" -> {//写基本信息
                val mIntent = Intent(this@MainActivity, WriteBaseInfoActivity::class.java)
                mIntent.putExtra("code", modeMap["222"])
                startActivity(mIntent)
            }
            "ll_main3" -> {//故障代码
                val mIntent = Intent(this@MainActivity, ErrorCodeActivity::class.java)
                mIntent.putExtra("code", modeMap["223"])
                startActivity(mIntent)
            }
            "ll_main4" -> {//动态数据
                val mIntent = Intent(this@MainActivity, DynamicDataActivity::class.java)
                mIntent.putExtra("code", modeMap["224"])
                startActivity(mIntent)
            }
            "ll_main5" -> {//IO测试
                val mIntent = Intent(this@MainActivity, IOTestActivity::class.java)
                mIntent.putExtra("code", modeMap["225"])
                startActivity(mIntent)
            }
            "ll_main6" -> {//刷写文件
                val mIntent = Intent(this@MainActivity, WriteDataActivity::class.java)
                mIntent.putExtra("code", modeMap["226"])
                startActivity(mIntent)
            }
            "ibtn_setting" -> {
                val mIntent = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(mIntent)
            }
            "iv_back" -> {
                startActivity(Intent(this@MainActivity, SelectCarTypeActivity::class.java))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (SPTools[this, Constant.USERTYPE, Constant.userProfessional] == Constant.userProfessional) {

            } else {
                finish()
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
                        click("ll_main1")
                    }
                    holder.textView!!.text = getString(R.string.djbxx)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card1)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "222" -> {
                    holder.ll_main!!.setOnClickListener {
                        click("ll_main2")
                    }
                    holder.textView!!.text = getString(R.string.xjbxx)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card2)//4f5d73
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "223" -> {
                    holder.ll_main!!.setOnClickListener {
                        click("ll_main3")
                    }
                    holder.textView!!.text = getString(R.string.gzdm)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card3)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#77b3d4"))
                }
                "224" -> {
                    holder.ll_main!!.setOnClickListener {
                        click("ll_main4")
                    }
                    holder.textView!!.text = getString(R.string.dtsj)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card4)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "225" -> {
                    holder.ll_main!!.setOnClickListener {
                        click("ll_main5")
                    }
                    holder.textView!!.text = getString(R.string.iotest)
                    holder.imageView!!.setImageResource(R.mipmap.ic_card5)
                    holder.imageView!!.setBackgroundColor(Color.parseColor("#4f5d73"))
                }
                "226" -> {
                    holder.ll_main!!.setOnClickListener {
                        click("ll_main6")
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
