package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.ScrollView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.WriteFileBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.tools.FileUtil
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.activity_write_data2.*
import okhttp3.Call
import okhttp3.Request
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WriteData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var code = ""
    private var url = ""
    private var name = ""
    private var isLocal = ""
    private var list = ArrayList<WriteFileBean>()
    private var mFile: File? = null
    private var handler: Handler? = null
    private var jsonArray = JSONArray()
    var isStart = false// 是否开始刷写

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
        code = intent.getStringExtra("code")
        url = intent.getStringExtra("url")
        name = intent.getStringExtra("name")
        isLocal = intent.getStringExtra("isLocal")
        jsonArray = JSONArray()
        tv_msg.movementMethod = ScrollingMovementMethod.getInstance()
        setClickMethod(btn_start)
        setClickMethod(iv_back)
        progressBar.visibility = View.GONE
        progressBar.progress = 0
    }

    override fun doMethod(string: String) {
        when (string) {
            "btn_start" -> {
                isStart = true
                progressBar.progress = 0
                progressBar.visibility = View.VISIBLE
                btn_start.text = "刷写中......"
                iv_back.isClickable = false
                btn_start.isClickable = false
                btn_start.setBackgroundResource(R.drawable.shape_btn_gary)
                list.clear()
                jsonArray = JSONArray()
                if (SPTools[this, Constant.USERTYPE, Constant.userNormal] == Constant.userNormal) {
                    if (url.toLowerCase().startsWith("http")) {
                        if (isLocal == "1") {
                            // 下载到本地
                            download()
                        } else {
                            // 读网络流
                            download2()
                        }
                    } else {
                        // 本地已经有文件了
                        unloadFileInfo()
                    }
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
        var isD = true
        runOnUiThread {
            when {
                data!!.startsWith("-") -> {
                    val str = data!!.replace("-", "")
                    if ((str.toInt() - progressBar.progress) < 5 && (str.toInt() - progressBar.progress) >= 0) {
                        isD = false
                    } else {
                        progressBar.visibility = View.VISIBLE
                        progressBar.progress = str.toInt()
                    }
                }
                data!!.startsWith("toast") -> {
                    toast(data!!.replace("toast", ""))
                }
                else -> {
                    if (data == "刷写结束") {
                        progressBar.visibility = View.GONE
                        toast("刷写结束")
                        isStart = false
                        iv_back.isClickable = true
                        btn_start.text = getString(R.string.write)
                        btn_start.isClickable = true
                        btn_start.setBackgroundResource(R.drawable.shape_btn_colorprimary)
                        // 删除文件
//                        if (mFile != null) {
//                            mFile!!.deleteOnExit()
//                        }
                    } else {
                        var mData = ""
                        val mJsonArray = JSONArray(data)
                        for (i in 0 until mJsonArray.length()) {
                            mData = mData + mJsonArray.get(i) + "<br/>"
                        }
                        tv_msg.text = Html.fromHtml(mData)
                    }
                    // 设置默认滚动到底部
                    scrollView.post {
                        // TODO Auto-generated method stub
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                }
            }
        }
        if (isD) {
            super.setData(data)
        }
    }

    private fun download() {
        OkHttpUtils.get().url(url).addHeader("Accept-Encoding", "identity").build().execute(object : FileCallBack(com.qiming.eol_public.InitClass.pathShuaxie, name) {

            var mProgress = 0F

            override fun onBefore(request: Request?, id: Int) {
                super.onBefore(request, id)
                val currentTime = Date()
                val formatter = SimpleDateFormat("HH:mm:ss")
                val dateString = formatter.format(currentTime)
                jsonArray.put("$dateString        生产文件开始下载")
                setData(jsonArray.toString())
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
                val currentTime = Date()
                val formatter = SimpleDateFormat("HH:mm:ss")
                val dateString = formatter.format(currentTime)
                jsonArray.put("<font color='#FF0000'>$dateString        生产文件下载失败，请重试</font>")
                setData(jsonArray.toString())
            }

            override fun onResponse(response: File, id: Int) {
                if (mProgress == 1f) {
                    mFile = response
                    val buffer = FileUtil.readFile(mFile)
                    myApp.publicUnit.setAllBytesData(buffer)

                    val currentTime = Date()
                    val formatter = SimpleDateFormat("HH:mm:ss")
                    val dateString = formatter.format(currentTime)
                    jsonArray.put("$dateString        生产文件下载完成")
                    unloadFileInfo()
                    setData(jsonArray.toString())

                    if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                        initScript()
                    } else {
                        if (isUserConnected) {
                            val currentTime = Date()
                            val formatter = SimpleDateFormat("HH:mm:ss")
                            val dateString = formatter.format(currentTime)
                            jsonArray.put("<font color='#FF0000'>$dateString        OBD未连接</font>")
                            setData(jsonArray.toString())
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("StaticFieldLeak")
    private fun download2() {
        object : AsyncTask<String, Void, ByteArray>() {

            override fun onPreExecute() {
                super.onPreExecute()
                val currentTime = Date()
                val formatter = SimpleDateFormat("HH:mm:ss")
                val dateString = formatter.format(currentTime)
                jsonArray.put("$dateString        生产文件开始读取")
                setData(jsonArray.toString())
            }

            override fun doInBackground(vararg strings: String): ByteArray? {
                SPTools.put(this@WriteData2Activity, "ControlFile", "")
                val mUrl = URL(strings[0])
                val connection = mUrl.openConnection()
                //获取内容长度
                val contentLength = connection.contentLength
                val scriptInputStream = connection.getInputStream()
                var totalReaded = 0L
                val baos = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len = 0
                do {
                    len = scriptInputStream!!.read(buffer)
                    if (len == -1) {
                        break
                    }
                    baos.write(buffer, 0, len)
                    totalReaded += len
                    val progress = totalReaded * 100 / contentLength
                    setData("-" + progress.toInt())
                } while (true)
                scriptInputStream!!.close()
                val result = baos.toByteArray()
                baos.close()
                return result
            }

            override fun onPostExecute(result: ByteArray?) {
                super.onPostExecute(result)
                myApp.publicUnit.setAllBytesData(result)

                val currentTime = Date()
                val formatter = SimpleDateFormat("HH:mm:ss")
                val dateString = formatter.format(currentTime)
                jsonArray.put("$dateString        生产文件读取完成")
                unloadFileInfo()
                setData(jsonArray.toString())

                if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                    initScript()
                } else {
                    if (isUserConnected) {
                        val currentTime = Date()
                        val formatter = SimpleDateFormat("HH:mm:ss")
                        val dateString = formatter.format(currentTime)
                        jsonArray.put("<font color='#FF0000'>$dateString        OBD未连接</font>")
                        setData(jsonArray.toString())
                    }
                }

            }

        }.execute(url)
    }

    // 下载文件成功调用通知接口(判断本地文件是否可以刷写)
    private fun unloadFileInfo() {
        val map = HashMap<String, String>()
        map["file"] = name
        if (url.toLowerCase().startsWith("http")) {
            map["isLocal"] = "0"//使用的网络下载文件
        } else {
            map["isLocal"] = "1"//使用的本地文件
        }
        NetTools.net(map, Urls().setFileDownOK, this, {
            if (it.code == "0") {
                if (!url.toLowerCase().startsWith("http")) {
                    val buffer = FileUtil.readFile(File(url))
                    myApp.publicUnit.setAllBytesData(buffer)

                    if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                        initScript()
                    } else {
                        if (isUserConnected) {
                            val currentTime = Date()
                            val formatter = SimpleDateFormat("HH:mm:ss")
                            val dateString = formatter.format(currentTime)
                            jsonArray.put("<font color='#FF0000'>$dateString        OBD未连接</font>")
                            setData(jsonArray.toString())
                        }
                    }
                }
            } else {
                File(url).deleteOnExit()
                setData("toast" + it.msg)
                setData("刷写结束")
            }
        }, "", false, false)
    }

    @SuppressLint("HandlerLeak")
    private fun initScript() {
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                myApp.publicUnit.setBreak(true)
                setData("toast" + msg.obj.toString())
            }
        }
        try {
            myApp.publicUnit.setMessageHandler(@SuppressLint("HandlerLeak")
            object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        -1// 清空显示文本
                        -> setData("")
                        0// 显示文本
                        -> {
                            val currentTime = Date()
                            val formatter = SimpleDateFormat("HH:mm:ss")
                            val dateString = formatter.format(currentTime)
                            if (msg.arg1 == 1) {
                                jsonArray.put("<font color='#FF0000'>" + dateString + "        " + msg.obj.toString() + "</font>")
                            } else {
                                jsonArray.put(dateString + "        " + msg.obj.toString())
                            }
                            setData(jsonArray.toString())
                        }
                        1// 显示进度条
                        -> {
                            Log.e("cyf", "进度条：" + msg.obj as Int)
                            setData("-" + msg.obj as Int)
                        }
                    }
                    super.handleMessage(msg)
                }
            })
            myApp.publicUnit.SetEvent(handler, code)
            Thread {
                while (true) {
                    val isRun = myApp.publicUnit.GetScriptIsRun(code)
                    if (!isRun) {
                        runOnUiThread {
                            // 恢复按钮可以点击
                            setData("刷写结束")
                        }
                        break
                    }
                }
            }.start()
        } catch (e: Exception) {
            LogTools.errLog(e)
        }
    }

}
