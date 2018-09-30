package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.tools.LogTools
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.url.Urls
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.activity_setting.*
import okhttp3.Call
import okhttp3.Request
import org.json.JSONObject
import java.io.File


class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        isshowDissWait = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        tv_update.text = "检查更新（当前版本：${getLocalVersionName(this)}）"
        iv_back.setOnClickListener {
            finish()
        }
        ll_cp.setOnClickListener {
            // 修改密码

        }
        ll_update.setOnClickListener {
            // 检查更新app
            showProgressDialog()
            getAppFile(getVersionCode(this))
        }
    }

    /**
     * 获取本地软件版本号名称
     */
    fun getLocalVersionName(ctx: Context): String {
        var localVersion = ""
        try {
            val packageInfo = ctx.applicationContext.packageManager.getPackageInfo(ctx.packageName, 0)
            localVersion = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            LogTools.errLog(e)
        }
        return localVersion
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
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
    private fun getAppFile(ver: Int) {
        val map = HashMap<String, String>()
        map["ver"] = "" + ver
        NetTools.net(map, Urls().getAppFile, this, {
            dismissProgressDialog()
            val jsonObject = JSONObject(it.data)
            val path = jsonObject.optString("path")
            val mVer = jsonObject.optString("ver")
            if (path == "") {
                // 无更新
                val dialog = AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("当前已经是最新版本")
                        .setPositiveButton("确认") { dialog, _ ->
                            dialog.dismiss()
                        }.create()
                dialog.show()
            } else {
                // 下载更新
                download(path, resources.getString(R.string.app_name) + ver + ".apk")
            }
        }, "", false, false)
    }

    private fun download(url: String, name: String) {
        try {
            OkHttpUtils.get().url(url).addHeader("Accept-Encoding", "identity").build().execute(
                    object : FileCallBack(com.qiming.eol_public.InitClass.pathApk, name) {

                        var progressBar: ProgressBar? = null
                        var dialog: AlertDialog? = null
                        var view: View? = null

                        override fun onBefore(request: Request?, id: Int) {
                            super.onBefore(request, id)
                            view = layoutInflater.inflate(R.layout.dialog_progress, null)
                            progressBar = view!!.findViewById(R.id.progressBar)
                            progressBar!!.progress = 0
                            dialog = AlertDialog.Builder(this@SettingActivity)
                                    .setTitle("提示")
                                    .setMessage("下载新版本...")
                                    .setView(view)
                                    .create()
                            dialog!!.show()
                        }

                        override fun inProgress(progress: Float, total: Long, id: Int) {
                            super.inProgress(progress, total, id)
                            // progressBar!!.progress = (progress / total * 100).toInt()
                            progressBar!!.progress = (progress * 100).toInt()
                        }

                        override fun onError(call: Call, e: Exception, id: Int) {
                            dialog!!.dismiss()
                        }

                        override fun onResponse(response: File, id: Int) {
                            dialog!!.dismiss()
                            val intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.action = Intent.ACTION_VIEW
                            val type = getMIMEType(response)
                            intent.setDataAndType(Uri.fromFile(response), type)
                            startActivity(intent)
                            // android.os.Process.killProcess(android.os.Process.myPid())
                        }
                    })
        } catch (e: java.lang.Exception) {
            LogTools.errLog(e)
        }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
