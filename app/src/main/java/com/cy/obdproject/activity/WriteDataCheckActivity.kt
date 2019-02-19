package com.cy.obdproject.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.url.Urls
import com.cyf.cyfimageselector.model.PhotoConfigure
import com.cyf.cyfimageselector.recycler.CyfRecyclerView
import com.cyf.cyfimageselector.utils.DensityUtil
import kotlinx.android.synthetic.main.activity_write_data_check.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.HashMap
import kotlin.collections.ArrayList

class WriteDataCheckActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    var dialog1: ProgressDialog? = null
    private var fileName = ""
    private var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data_check)
        initView()
        initRcv()
    }

    private fun initView() {
        if (intent.hasExtra("name")) {
            fileName = intent.getStringExtra("name")
        }
        tv_name.text = fileName
        net_getUploadImgUrl()

        setClickMethod(iv_back)
        setClickMethod(btn_ok)
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_ok" -> {
                if (et_vin.text.toString() == "") {
                    toast("vin不能为空")
                    return
                }
                showProgressDialog("正在上传...")
                recyclerView.getSelectThumbnailsList()
            }
        }
    }

    private fun initRcv() {
        val config = PhotoConfigure().setType(PhotoConfigure.EditImg).setColnum(3).setDelete(false).setCanDrag(true).setAutoDelThm(true).setOriginalShow(true)
        recyclerView.setTv_delete(textView).setOnUpdateData {
            recyclerView.post {
                val h = DensityUtil.dip2px(this@WriteDataCheckActivity, 244f) +
                        ((recyclerView.width - DensityUtil.dip2px(this@WriteDataCheckActivity, 32f)) / 3 * it)
                if (rl_view.height != h) {
                    rl_view.layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h)
                }
            }
        }.setOnCyfThumbnailsListener(object : CyfRecyclerView.OnCyfThumbnailsListener {

            override fun onStart() {
                // 开启转圈等待效果
                dialog1?.show()
            }

            override fun onError(e: Throwable?) {
                dialog1?.cancel()
                Toast.makeText(this@WriteDataCheckActivity, "获取图片异常：" + e.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onEnd(list: MutableList<String>?, thumbnailsList: MutableList<String>?, isOriginalDrawing: Boolean) {
                // 这里启动上传图片的线程，并结束转圈等待效果
                dialog1?.cancel()

                val builder2 = AlertDialog.Builder(
                        this@WriteDataCheckActivity)
                builder2.setTitle("提示")
                builder2.setCancelable(true)
                val dialog2 = builder2.create()
                if (isOriginalDrawing) {
                    dialog2.setMessage("原图：\n" + list.toString())
                } else {
                    dialog2.setMessage("缩略图：\n" + thumbnailsList.toString())
                }
                dialog2.setButton(AlertDialog.BUTTON_POSITIVE, "我知道了") { p0, p1 ->
                    // 上传完成之后按需求调用清除缩略图的方法（config.isAutoDelThm = true可以不调用）
                    // recyclerView.clearThumbnailsList()
                }
                // dialog2.show()
                if (isOriginalDrawing) {
                    net_UploadFile(list!!)
                } else {
                    net_UploadFile(thumbnailsList!!)
                }
            }
        }).show(config)
    }

    /**
     * 获取上传图片地址
     */
    private fun net_getUploadImgUrl() {
        val map = hashMapOf<String, String>()
        NetTools.net(map, Urls().getUploadImgUrl, this) { response ->
            val json = JSONObject(response.data)
            Urls.uploadImg = json.optString("url")
        }
    }

    /**
     * 上传文件
     */
    private fun net_UploadFile(paths: MutableList<String>) {
        if (Urls.uploadImg == "") {
            toast("上传图片地址获取失败，请重试")
            net_getUploadImgUrl()
            return
        }
        val map = HashMap<String, File>()
        for (i in 0 until paths.size) {
            map["file$i"] = File(paths[i])
        }
        NetTools.netFile(map, this) { response ->
            val jsonArray = JSONArray(response.data)
            list.clear()
            for (i in 0 until jsonArray.length()) {
                val json = jsonArray.getJSONObject(i)
                list.add(json.optString("id"))
            }
            net_uploadCheck()
        }
    }

    /**
     * 上传vin
     */
    private fun net_uploadCheck() {
        // {"file":"生产文件名", "vin":"","remark":"","imgs": ["123","456","789"]}
        val mListString = list.toString().replace("[", "[\"").replace("]", "\"]").replace(",", "\",\"").replace(" ", "")
        val data = "{\"file\":\"$fileName\", \"vin\":\"${et_vin.text}\", \"remark\":\"${et_remark.text}\",\"imgs\": $mListString}"
        NetTools.net(data, Urls().uploadCheck, this) { response ->
            toast(response.msg!!)
            finish()
        }
    }

}
