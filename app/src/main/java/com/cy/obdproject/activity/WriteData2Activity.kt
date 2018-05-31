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
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.ByteTools
import com.cy.obdproject.tools.FileUtil
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.StringTools
import com.cy.obdproject.worker.WriteDataWorker
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.activity_write_data2.*
import okhttp3.Call
import okhttp3.Request
import org.jetbrains.anko.toast
import java.io.File

class WriteData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var url = ""
    private var name = ""
    private var writeDataWorker: WriteDataWorker? = null
    private var list = ArrayList<WriteFileBean>()
    private var mData = ""
    private var mFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data2)
        initView()
    }

    override fun onBackPressed() {
        if (iv_back.isClickable) {
            super.onBackPressed()
        }
    }

    private fun initView() {
        if (intent.hasExtra("url")) {
            url = intent.getStringExtra("url")
        }
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name")
        }
        tv_msg.movementMethod = ScrollingMovementMethod.getInstance()
        setClickMethod(btn_start)
        setClickMethod(iv_back)
        progressBar.progress = 0
        writeDataWorker = WriteDataWorker()
        writeDataWorker!!.init(this, { data ->
            if (mData != data) {
                mData = data
                setData(data)
            }
        })
    }

    override fun doMethod(string: String) {
        when (string) {
            "btn_start" -> {
                btn_start.text = "刷写中......"
                iv_back.isClickable = false
                btn_start.isClickable = false
                btn_start.setBackgroundResource(R.drawable.shape_btn_gary)
                list.clear()
                if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
                    download()
                }
            }
            "iv_back" -> {
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setData(data: String?) {
        Log.e("cyf88", "data = $data")
        runOnUiThread {
            if (data!!.contains("-")) {
                val str = data!!.replace("-", "")
                progressBar.progress = str.toInt()
            } else {
                if (data == "下载刷写文件中") {
                    tv_msg.text = "下载刷写文件中"
                } else if (data == "返回数据超时" || data == "OBD未连接") {
                    if (data == "返回数据超时") {
                        tv_msg.append("\n返回数据超时")
                    } else {
                        tv_msg.text = "OBD未连接"
                    }
                    btn_start.text = getString(R.string.write)
                    iv_back.isClickable = true
                    btn_start.isClickable = true
                    btn_start.setBackgroundResource(R.drawable.shape_btn_colorprimary)
                } else {
                    tv_msg.append("\n" + data)
                    if (data == "刷写完成") {
                        toast("刷写完成")
                        btn_start.text = getString(R.string.write)
                        iv_back.isClickable = true
                        btn_start.isClickable = true
                        btn_start.setBackgroundResource(R.drawable.shape_btn_colorprimary)
                        // 删除文件
                        if (mFile != null) {
                            mFile!!.deleteOnExit()
                        }
                    }
                }
            }
        }
        super.setData(data)
    }

    private fun download() {
        OkHttpUtils.get().url(url).addHeader("Accept-Encoding", "identity").build().execute(object : FileCallBack(Environment.getExternalStorageDirectory().absolutePath, name) {

            var mProgress = 0F

            override fun onBefore(request: Request?, id: Int) {
                super.onBefore(request, id)
                setData("下载刷写文件")
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                super.inProgress(progress, total, id)
                if (progress >= 0) {
                    if (progress == 1f) {
                        mProgress = 1f
                        setData("-" + (100 * progress).toInt())
                    } else {
                        if (progress - mProgress > 0.1f) {
                            mProgress = progress
                            setData("-" + (100 * progress).toInt())
                        }
                    }
                }
            }

            override fun onError(call: Call, e: Exception, id: Int) {
                setData("下载刷写文件失败")
            }

            override fun onResponse(response: File, id: Int) {
                if (mProgress == 1f) {
                    setData("下载刷写文件完成\n读取刷写文件")
                    if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                        readData(response)
                    } else {
                        if (isUserConnected) {
                            setData("OBD未连接")
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("StaticFieldLeak")
    fun readData(response: File) {
        object : AsyncTask<File, Void, String>() {

            var num = 0

            override fun doInBackground(vararg p0: File?): String {
                mFile = p0[0]

                val buffer = FileUtil.readFile(p0[0])
                val ba0 = ByteArray(1)
                ba0[0] = buffer[6]
                val piece = StringTools.byte2hex(ba0)
                num = piece.toInt()
                var a = 0
                for (i in 0 until num) {
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
                    bean.data = ByteTools.subBytes(buffer, i * 8 + 14 + a + 1, b)
                    val aa = ShortArray(bean.data.size)
                    for (j in 0 until bean.data.size) {
                        aa[j] = ByteTools.toShort(bean.data[j])
                    }
                    bean.data2 = aa
                    list.add(bean)
                    a += b
                    Log.i("cyf88", bean.toString())
                }
                return ""
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                setData("刷写文件中（共" + num + "段）")
                writeDataWorker!!.start(list)
            }

        }.execute(response)
    }

}
