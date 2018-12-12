package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.tools.ZipUtils
import com.cy.obdproject.url.Urls
import com.qiming.eol_public.InitClass
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import dalvik.system.DexClassLoader
import kotlinx.android.synthetic.main.activity_welcome.*
import okhttp3.Call
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.*


class WelcomeActivity : BaseActivity() {

    private var scriptName = ""
    private var scriptInputStream: InputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        checkFile()
        // 测试https
//        NetTools.net(HashMap<String, String>(), "https://192.168.1.81:4050", this) {
//
//        }
    }

    // 创建文件夹，删除旧版本的apk和3天以前的日志
    @SuppressLint("SetTextI18n")
    private fun checkFile() {
        tv_msg.text = "初始化..."
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathApk)
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathShuaxie)
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathLiucheng)
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathMokuai)
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathCuowu)
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathShunxu)
        checkFileMkdirs(com.qiming.eol_public.InitClass.pathTongxun)
        // 检查旧版本的apk,有则删除
        val apkList = File(com.qiming.eol_public.InitClass.pathApk).listFiles()
        if (apkList.isNotEmpty()) {
            for (i in 0 until apkList.size) {
                apkList[i].delete()
            }
        }
        // 检查3天以前的日志
        checkLogFile(com.qiming.eol_public.InitClass.pathCuowu)
        checkLogFile(com.qiming.eol_public.InitClass.pathShunxu)
        checkLogFile(com.qiming.eol_public.InitClass.pathTongxun)
        // 检查更新app
        getAppFile(getVersionCode(this))
    }

    // 检查文件夹是否存在，不存在创建
    private fun checkFileMkdirs(path: String) {
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    // 检查是否有3天以前的日志，有删除
    private fun checkLogFile(path: String) {
        val list = File(path).listFiles()
        if (list.isNotEmpty()) {
            for (i in 0 until list.size) {
                if (System.currentTimeMillis() - list[i].lastModified() > 3 * 24 * 60 * 60 * 1000) {
                    list[i].delete()
                }
            }
        }
    }

    // 获取当前本地apk的版本
    private fun getVersionCode(mContext: Context): Int {
        var versionCode = 0
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            LogTools.errLog(e)
        }
        return versionCode
    }

    // 最新应用文件
    @SuppressLint("SetTextI18n")
    private fun getAppFile(ver: Int) {
        tv_msg.text = "检查app版本..."
        val map = HashMap<String, String>()
        map["ver"] = "" + ver
        NetTools.net(map, Urls().getAppFile, this, {
            val jsonObject = JSONObject(it.data)
            val path = jsonObject.optString("path")
            val mVer = jsonObject.optString("ver")
            if (path == "") {
                // 无更新
                // 检查更新jar包
                getModuleFile()
            } else {
                // 下载更新
                download(path, resources.getString(R.string.app_name) + ver + ".apk", 1)
            }
        }, "", false, false)
    }

    // 最新模块文件
    private fun getModuleFile() {
        tv_msg.text = "检查模块文件..."
        var file = SPTools[this, "ModuleFile", ""] as String
        if (!File(com.qiming.eol_public.InitClass.pathMokuai + "/" + file).exists()) {
            // 防止用户主动删除文件，导致有记录但是没有文件
            file = ""
        }
        val map = HashMap<String, String>()
        map["file"] = "" + file
        NetTools.net(map, Urls().getModuleFile, this, {
            val jsonObject = JSONObject(it.data)
            val path = jsonObject.optString("path")
            val file = jsonObject.optString("file")
            if (path == "") {
                // 无更新
                // 检查更新脚本文件
                getControlFile()
            } else {
                // 下载更新
                download(path, file, 2)
            }
        }, "", false, false)
    }

    // 最新流程文件
    private fun getControlFile() {
        tv_msg.text = "检查流程文件..."
        var file = SPTools[this, "ControlFile", ""] as String
        if (!File(com.qiming.eol_public.InitClass.pathLiucheng + "/" + file).exists()) {
            // 防止用户主动删除文件，导致有记录但是没有文件
            file = ""
        }
        scriptName = file
        val map = HashMap<String, String>()
        map["file"] = "" + file
        NetTools.net(map, Urls().getControlFile, this, {
            val jsonObject = JSONObject(it.data)
            val path = jsonObject.optString("path")
            val file = jsonObject.optString("file")
            val isLocal = jsonObject.optString("isLocal")
            if (path == "") {
                // 无更新
                startApp()
            } else {
                // 删除旧文件
                File(com.qiming.eol_public.InitClass.pathLiucheng + "/" + scriptName).delete()
                // 下载更新
                if (isLocal == "1") {
                    // 文件下载
                    download(path, file, 3)
                } else {
                    // 流下载
                    download2(path)
                }
            }
        }, "", false, false)
    }

    private fun download(url: String, name: String, type: Int) {
        try {
            var path = ""
            when (type) {
                1 -> {
                    path = com.qiming.eol_public.InitClass.pathApk
                }
                2 -> {
                    path = com.qiming.eol_public.InitClass.pathMokuai
                }
                3 -> {
                    path = com.qiming.eol_public.InitClass.pathLiucheng
                }
            }
            OkHttpUtils.get().url(url).addHeader("Accept-Encoding", "identity").build().execute(
                    object : FileCallBack(path, name) {

                        override fun onBefore(request: Request?, id: Int) {
                            super.onBefore(request, id)
                            progressBar.progress = 0
                            progressBar.visibility = View.VISIBLE
                            when (type) {
                                1 -> {
                                    tv_msg.text = "下载新版本..."
                                }
                                2 -> {
                                    tv_msg.text = "下载新的模块文件..."
                                }
                                3 -> {
                                    tv_msg.text = "下载新的流程文件..."
                                }
                            }
                        }

                        override fun inProgress(progress: Float, total: Long, id: Int) {
                            super.inProgress(progress, total, id)
                            progressBar.progress = (progress * 100).toInt()
                        }

                        override fun onError(call: Call, e: Exception, id: Int) {
                            progressBar.visibility = View.INVISIBLE
                            showErrDialog("初始化下载异常")
                        }

                        override fun onResponse(response: File, id: Int) {
                            progressBar.visibility = View.INVISIBLE
                            when (type) {
                                1 -> {
                                    val intent = Intent()
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.action = Intent.ACTION_VIEW
                                    val type = getMIMEType(response)
                                    intent.setDataAndType(Uri.fromFile(response), type)
                                    startActivity(intent)
                                    // android.os.Process.killProcess(android.os.Process.myPid())
                                }
                                2 -> {
                                    // 解压文件
                                    tv_msg.text = "解压缩模块文件..."
                                    ZipUtils.UnZipFolder(response.absolutePath, com.qiming.eol_public.InitClass.pathMokuai)
                                    // 下一步
                                    SPTools.put(this@WelcomeActivity, "ModuleFile", name)
                                    getControlFile()
                                }
                                3 -> {
                                    SPTools.put(this@WelcomeActivity, "ControlFile", name)
                                    scriptName = name
                                    startApp()
                                }
                            }
                        }
                    })
        } catch (e: java.lang.Exception) {
            showErrDialog("初始化下载异常")
            LogTools.errLog(e)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun download2(url: String) {
        object : AsyncTask<String, Void, String>() {

            override fun onPreExecute() {
                super.onPreExecute()
                progressBar.progress = 0
                progressBar.visibility = View.VISIBLE
                tv_msg.text = "下载新的流程文件..."
            }

            override fun doInBackground(vararg strings: String): String? {
                SPTools.put(this@WelcomeActivity, "ControlFile", "")
                val mUrl = URL(strings[0])
                val connection = mUrl.openConnection()
                //获取内容长度
                val contentLength = connection.contentLength
                scriptInputStream = connection.getInputStream()
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
                    progressBar.progress = progress.toInt()
                } while (true)
                scriptInputStream!!.close()
                val result = baos.toString()
                baos.close()
                return result
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                scriptInputStream = ByteArrayInputStream(result!!.toByteArray())
                startApp()
            }

        }.execute(url)
    }

    // 弹出错误提示对话框
    private fun showErrDialog(msg: String) {
        val dialog = AlertDialog.Builder(this@WelcomeActivity)
                .setTitle("提示")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("退出") { dialog, _ ->
                    this@WelcomeActivity.finish()
                    dialog.dismiss()
                }.create()
        dialog.show()
    }

    private fun getMIMEType(f: File): String {
        var type: String
        val fName = f.name
        val end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length)
                .toLowerCase()
        if (end == "m4a" || end == "mp3" || end == "mid"
                || end == "xmf" || end == "ogg" || end == "wav") {
            type = "audio"
        } else if (end == "3gp" || end == "mp4") {
            type = "video"
        } else if (end == "jpg" || end == "gif" || end == "png"
                || end == "jpeg" || end == "bmp") {
            type = "image"
        } else if (end == "apk") {
            type = "application/vnd.android.package-archive"
        } else {
            type = "*"
        }
        if (end == "apk") {
        } else {
            type += "/*"
        }
        return type
    }

    private fun startApp() {
        // 启动脚本
        var isOk = true
        try {
            val libPath = InitClass.pathMokuai + "/" + "eol_scriptrunner.jar" // 要动态加载的jar
            val dexDir = getDir("dex", MODE_PRIVATE) // 优化后dex的路径
            val classLoader = DexClassLoader(libPath, dexDir.absolutePath, null, classLoader)
            val cls = classLoader.loadClass("com.qiming.eol_scriptrunner.ScriptManager")
            val mobject = cls.newInstance()

            val param = InitClass.ClassUnit()
            param.Instance = mobject
            param.clazz = cls
            myApp.publicUnit.SetMap("eol_scriptrunner", param)
            myApp.publicUnit.SetPublicUnit("eol_scriptrunner", myApp.publicUnit)
            isOk = if (scriptInputStream == null) {
                myApp.publicUnit.ScriptManagerLoadScript(scriptName)
            } else {
                myApp.publicUnit.ScriptManagerLoadScript(scriptInputStream)
            }
        } catch (e: Exception) {
            isOk = false
            LogTools.errLog(e)
        }
        if (isOk) {
            // 跳转登录界面
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
            overridePendingTransition(0, 0)
        } else {
            showErrDialog("模块文件解析失败")
        }
    }

}
