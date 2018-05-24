package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.cy.obdproject.R
import com.cy.obdproject.adapter.SelectRequestAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.ErrorCodeBean
import com.cy.obdproject.bean.RequestBean
import com.cy.obdproject.bean.WebSocketBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.socket.WebSocketService
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.tools.SPTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_response_list.*
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.set
import kotlin.math.log


// 专家列表
class ResponseListActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var mAlertDialog: AlertDialog? = null
    private var mIntent1: Intent? = null
    private var requestList: ArrayList<RequestBean>? = null
    private var adapter: SelectRequestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response_list)
        initView()
        net_requestList()
    }

    private fun initView() {
        setClickMethod(iv_back)
        mIntent1 = Intent(this, WebSocketService::class.java)
        requestList = ArrayList()
        listView!!.setOnItemClickListener { _, _, position, _ ->
            SPTools.put(this, Constant.ZFORUID, "" + requestList!![position].requestId)
            startService(mIntent1)
            showProgressDialog()
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
        }
    }

    fun showWaitDialog(isShow: Boolean) {
        dismissProgressDialog()
        if (isShow) {
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
            }
            mAlertDialog = AlertDialog.Builder(this).setTitle("提示").setMessage("等待专家应答......").setCancelable(false).setPositiveButton("取消等待") { _, _ ->
                stopService(mIntent1)
                mAlertDialog!!.dismiss()
            }.show()
        } else {
            // 给专家端发送首页信息  用户连接
            var data = ""
            var bean: ErrorCodeBean = ErrorCodeBean()
            bean.code = SPTools.get(this, Constant.CARTYPE, "1").toString()//车型
            bean.msg = SPTools.get(this, Constant.CARNAME, "").toString()// 车名
            data = Gson().toJson(bean)
            val str = "{\"activity\":\"" + "MainActivity" + "\",\"method\":\"" + "setData" + "\",\"data\":\"" + data.replace("\"", "\\\"") + "\"}"
            val map = HashMap<String, String>()
            map["data"] = str
            NetTools.net(map, Urls().updateMsg, this, { response ->
                if (response != null && "0" == response.code) {
                    try {
                        val jsonObject = JSONObject(response.data)
                        val webSocketBean = WebSocketBean()
                        webSocketBean.s = "" + SPTools[this@ResponseListActivity, Constant.USERID, ""]!!
                        // ---------------------------- cyf 需要修改-----------------------------
                        webSocketBean.r = "" + SPTools[this@ResponseListActivity, Constant.ZFORUID, ""]!!
                        webSocketBean.c = "D"
                        webSocketBean.d = jsonObject.optString("id")
                        webSocketBean.e = ""
                        WebSocketService.getIntance()!!.sendMsg(Gson().toJson(webSocketBean))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }, "正在加载...", false, false)

            // 返回主页面
            if (mAlertDialog != null && mAlertDialog!!.isShowing) {
                mAlertDialog!!.dismiss()
                finish()
            }
        }
    }

    private fun net_requestList() {
        val map = hashMapOf<String, String>()
        map["requestUserType"] = "z"
        NetTools.net(map, Urls().requestList, this, { response ->
            if (response.code == "0") {
                val beans = Gson().fromJson<List<RequestBean>>(response.data, object : TypeToken<ArrayList<RequestBean>>() {}.type) as ArrayList<RequestBean>?
                requestList!!.clear()
                requestList!!.addAll(beans!!)
                if (adapter == null) {
                    adapter = SelectRequestAdapter(requestList!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                toast(response.msg!!)
            }
        }, "正在加载...", true, true)
    }

}
