package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.cy.obdproject.R
import com.cy.obdproject.adapter.WriteDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.BaseBean
import com.cy.obdproject.bean.WriteDataBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qiming.eol_public.InitClass
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.Callback
import kotlinx.android.synthetic.main.activity_write_data.*
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Response
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File
import java.util.*

class WriteDataActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var code = ""

    private var list: ArrayList<WriteDataBean>? = null
    private var adapter: WriteDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)
        initView()
    }

    override fun onStart() {
        super.onStart()
        showDissWait()
        if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
            net_fileList()
        }
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_notice).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tv_notice).text = "暂无生产文件"
        code = intent.getStringExtra("code")
        setClickMethod(iv_back)
        setClickMethod(tv_refresh)
        list = ArrayList()
        listView.onItemClickListener = this
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sendClick(this@WriteDataActivity.localClassName, "" + p2)
        val mIntent = Intent(this, WriteData2Activity::class.java)
        mIntent.putExtra("code", code)
        if (list!![p2].isLocal == null) {
            mIntent.putExtra("isLocal", "0")
        } else {
            mIntent.putExtra("isLocal", list!![p2].isLocal)
        }
        mIntent.putExtra("url", list!![p2].filePath)
        mIntent.putExtra("name", list!![p2].fileName)
        startActivity(mIntent)
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "tv_refresh" -> {
                if (!isProfessionalConnected) {
                    showProgressDialog()
                    net_fileList()
                } else {
                    showDissWait()
                }
            }
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }

    override fun setData(data: String?) {
        if (data!!.startsWith("toast")) {
            toast(data!!.replace("toast", ""))
        } else {
            try {
                val mdata = data!!.replace("//", "\\/\\/").replace("/?", "\\/?").replace("\\", "\\\\")
                val beans = Gson().fromJson<List<WriteDataBean>>(mdata, object : TypeToken<ArrayList<WriteDataBean>>() {}.type) as ArrayList<WriteDataBean>?
                list!!.clear()
                list!!.addAll(beans!!)
                if (Constant.userNormal == SPTools[this@WriteDataActivity, Constant.USERTYPE, Constant.userNormal] as Int) {
                    // 用户端
                    // 读取本地文件
                    val file = File(InitClass.pathShuaxie)
                    val fileList = file.listFiles()
                    for (i in 0 until fileList.size) {
                        if (fileList[i].isFile && (fileList[i].name.toLowerCase().endsWith(".eol") ||
                                        fileList[i].name.toLowerCase().endsWith(".s19") ||
                                        fileList[i].name.toLowerCase().endsWith(".hex"))) {
                            var isHas = false
                            for (j in 0 until list!!.size) {
                                if (fileList[i].name == list!![j].fileName) {
                                    list!![j].localHas = "1"
                                    list!![j].filePath = fileList[i].absolutePath
                                    isHas = true
                                    break
                                }
                            }
                            if (!isHas) {
                                val bean = WriteDataBean()
                                bean.localHas = "1"
                                bean.fileName = fileList[i].name
                                bean.filePath = fileList[i].absolutePath
                                list!!.add(bean)
                            }
                        }
                    }
                }

                if (list!!.size > 0) {
                    findViewById<TextView>(R.id.tv_notice).visibility = View.GONE
                } else {
                    findViewById<TextView>(R.id.tv_notice).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tv_notice).text = "暂无生产文件"
                }
                listView.visibility = View.VISIBLE
                if (adapter == null) {
                    adapter = WriteDataAdapter(list!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
                dismissProgressDialog()
            } catch (e: Exception) {
                LogTools.errLog(e)
            }
        }
        super.setData(data)
    }

    private fun net_fileList() {
        val map = hashMapOf<String, String>()
//        NetTools.net(map, Urls().fileList, this, { response ->
//            if (response.code == "0") {
//                setData(response.data)
//            } else {
//                setData("toast" + response.msg)
//            }
//        }, "正在加载...", true, true)
        val call = OkHttpUtils.postString().url(Urls().fileList)
                .addHeader(Constant.TOKEN, SPTools[this, Constant.TOKEN, ""] as String?)
                .mediaType(MediaType.parse("application/json"))
                .content(Gson().toJson(map))
                .build().execute(object : Callback<BaseBean>() {

                    override fun parseNetworkResponse(response: Response?, id: Int): BaseBean {
                        val json = response!!.body().string()
                        Log.e("cyf7", "response : $json")
                        val jsonObject = JSONObject(json)
                        val bean = BaseBean()
                        bean.code = jsonObject.optString("code")
                        bean.msg = jsonObject.optString("msg")
                        val json2 = jsonObject.optString("data")
                        if ("" != json2 && "{}" != json2 && "{ }" != json2) {
                            bean.data = json2
                        }
                        return bean
                    }

                    override fun onResponse(baseBean: BaseBean?, id: Int) {
                        if (baseBean!!.code == "0") {
                            setData(baseBean.data)
                        } else if (baseBean!!.code == "1002") {
                            // 登录信息失效
                            Toast.makeText(this@WriteDataActivity, baseBean.msg, Toast.LENGTH_SHORT).show()
                            SPTools.put(this@WriteDataActivity, Constant.ISLOGIN, "")
                            for (i in 0 until myApp.activityList.size) {
                                myApp.activityList[i].finish()
                            }
                            startActivity(Intent(this@WriteDataActivity, LoginActivity::class.java))
                        } else {
                            setData("toast" + baseBean.msg)
                        }
                    }

                    override fun onError(call: Call?, e: java.lang.Exception?, id: Int) {
                        val textView = findViewById<TextView>(R.id.tv_notice)
                        if (textView != null) {
                            textView!!.visibility = View.VISIBLE
                            textView!!.text = "获取生产文件失败，请重试"
                        }
                        if (findViewById<ListView>(R.id.listView) != null) {
                            findViewById<ListView>(R.id.listView).visibility = View.GONE
                        }
                    }
                })
    }

}
