package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.WriteFileBean
import com.cy.obdproject.tools.ByteTools
import com.cy.obdproject.tools.StringTools
import com.cy.obdproject.worker.WriteDataWorker
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.activity_write_data2.*
import okhttp3.Call
import java.io.File

class WriteData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var url = ""
    private var writeDataWorker: WriteDataWorker? = null
    private var list = ArrayList<WriteFileBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data2)
        initView()
    }

    private fun initView() {
        url = intent.getStringExtra("url")
        tv_msg.movementMethod = ScrollingMovementMethod.getInstance()
        setClickMethod(btn_start)
        progressBar.progress = 0
        writeDataWorker = WriteDataWorker()
        writeDataWorker!!.init(this, { data ->
            tv_msg.append("\n" + data)
        })
    }

    override fun doMethod(string: String) {
        when (string) {
            "btn_start" -> {
                btn_start.text = "刷写中......"
                btn_start.isClickable = false
                btn_start.setBackgroundResource(R.drawable.shape_btn_gary)
                tv_msg.append("下载刷写文件中......")

                list.clear()
                // download()
                readData()
            }
        }
    }

    private fun download() {
        OkHttpUtils.get().url(url).build().execute(object : FileCallBack(Environment.getExternalStorageDirectory().absolutePath, "测试.apk") {

            override fun inProgress(progress: Float, total: Long, id: Int) {
                super.inProgress(progress, total, id)
                progressBar.progress = (100 * progress).toInt()
                if (progress == 1f) {
                    // progressBar.progress = 0
                    tv_msg.append("\n读取刷写文件中......")
                    readData()
                }
            }

            override fun onError(call: Call, e: Exception, id: Int) {

            }

            override fun onResponse(response: File, id: Int) {

            }
        })
    }

    @SuppressLint("StaticFieldLeak")
    fun readData() {
        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg p0: String?): String {
                val mIs = assets.open("HS3EV_EPS_20180327.eol")
                val length = mIs.available()
                val buffer = ByteArray(length)
                mIs.read(buffer)
                val ba0 = ByteArray(1)
                ba0[0] = buffer[6]
                val piece = StringTools.byte2hex(ba0)
                var a = 0
                for (i in 0 until piece.toInt()) {
                    val ba1 = ByteArray(4)
                    ba1[0] = buffer[i * 8 + 10 + a]
                    ba1[1] = buffer[i * 8 + 9 + a]
                    ba1[2] = buffer[i * 8 + 8 + a]
                    ba1[3] = buffer[i * 8 + 7 + a]
                    val ba2 = ByteArray(4)
                    ba2[0] = buffer[i * 8 + 14 + a]
                    ba2[1] = buffer[i * 8 + 13 + a]
                    ba2[2] = buffer[i * 8 + 12 + a]
                    ba2[3] = buffer[i * 8 + 11 + a]
                    val str = StringTools.byte2hex(ba1)
                    val str2 = StringTools.byte2hex(ba2)
                    val bean = WriteFileBean()
                    bean.address = str
                    bean.endAddress = str2
                    val b = (str2.toInt(16) - str.toInt(16) + 1)
                    bean.length = "" + b

                    bean.data = ByteTools.subBytes(buffer, i * 8 + 14 + a + 4, b)
                    list.add(bean)
                    a += b
                    Log.i("cyf88", bean.toString())
                }
                return ""
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                tv_msg.append("\n刷写文件中......")
                writeDataWorker!!.start(list)
            }

        }.execute()
    }

}
