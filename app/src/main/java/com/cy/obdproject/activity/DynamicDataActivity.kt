package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.DynamicDataBean
import com.cy.obdproject.tools.LogTools
import kotlinx.android.synthetic.main.activity_dynamic_data.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class DynamicDataActivity : BaseActivity(), BaseActivity.ClickMethoListener, AdapterView.OnItemClickListener {

    private var pageSize = 9
    private var pageCount = 0

    private var code = ""

    private var listData = CopyOnWriteArrayList<DynamicDataBean>()
    private var adapter: ControlDynamicDataAdapter? = null
    var pageIndex = 0

    // private var adapter: DynamicDataAdapter? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_data)
        initView()
    }

    private fun initView() {
        // ------------------计算每页显示最大条目数-------------
        val outMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(outMetrics)
        val height = px2dip(this,
                (outMetrics.heightPixels - getStatusBarHeight(this)).toFloat())
        pageSize = (height - 44 - 20 - 8 - 26 - 16) / 73
        // ---------------------------------------------------

        code = intent.getStringExtra("code")

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
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
                        4 -> {
                            try {
                                val data = msg.obj.toString()
                                val jsonObject = JSONObject(data)
                                val jsonArray = jsonObject.optJSONArray("List")
                                listData.clear()
                                for (i in 0 until jsonArray.length()) {
                                    val jsonObject1 = jsonArray.getJSONObject(i)
                                    val bean = DynamicDataBean()
                                    bean.sid = jsonObject1!!.optString("SID")
                                    bean.did = jsonObject1!!.optString("DID")
                                    bean.name = jsonObject1!!.optString("Name")
                                    bean.name_ENG = jsonObject1!!.optString("Name_ENG")
                                    bean.byte_Start = jsonObject1!!.optString("Byte_Start")
                                    bean.byte_Length = jsonObject1!!.optString("Byte_Length")
                                    bean.bit_Start = jsonObject1!!.optString("Bit_Start")
                                    bean.bit_Length = jsonObject1!!.optString("Bit_Length")
                                    bean.coefficient = jsonObject1!!.optString("Coefficient")
                                    bean.offset = jsonObject1!!.optString("Offset")
                                    bean.type = jsonObject1!!.optString("Type")
                                    bean.enum = jsonObject1!!.optString("Enum")
                                    bean.unit = jsonObject1!!.optString("Unit")
                                    bean.unit_ENG = jsonObject1!!.optString("Unit_ENG")
                                    bean.value_Min = jsonObject1!!.optString("Value_Min")
                                    bean.value_Max = jsonObject1!!.optString("Value_Max")
                                    listData.add(bean)
                                }
//                            if (adapter == null) {
//                                adapter = DynamicDataAdapter(listData!!, this@DynamicDataActivity)
//                                listView!!.adapter = adapter
//                            } else {
//                                adapter!!.notifyDataSetChanged()
//                            }
                                if (intent.hasExtra("listData")) {
                                    val list = intent.getSerializableExtra("listData") as ArrayList<DynamicDataBean>?
                                    for (i in 0 until list!!.size) {
                                        if (list!![i].isSelect == "1") {
                                            for (j in 0 until listData.size) {
                                                if (list!![i].name == listData[j].name) {
                                                    listData[j].isSelect = "1"
                                                }
                                            }
                                        }
                                    }
                                }
                                if (listData!!.size > 0) {
                                    pageCount = (listData!!.size - 1) / pageSize + 1

                                    if (pageCount == 1) {
                                        btn_lastPage.isEnabled = false
                                        btn_nextPage.isEnabled = false
                                        btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
                                        btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
                                    }
                                    if (adapter == null) {
                                        adapter = ControlDynamicDataAdapter(listData!!, this@DynamicDataActivity)
                                        listView!!.adapter = adapter
                                    } else {
                                        adapter!!.notifyDataSetChanged()
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                LogTools.myLog("DynamicDataActivity initView 134行异常： ${e.message}")
                            }
                        }
                    }
                    super.handleMessage(msg)
                }
            })
            myApp.publicUnit.SetEvent(handler, code.split(",")[0])
        } catch (e: Exception) {
            LogTools.errLog(e)
        }
        setClickMethod(iv_back)
        setClickMethod(btn_start)
        setClickMethod(btn_lastPage)
        setClickMethod(btn_nextPage)
        setClickMethod(ll_check)
        listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        sendClick(this@DynamicDataActivity.localClassName, "" + position)
        if (listData!![position + pageIndex * pageSize].isSelect == "1") {
            listData!![position + pageIndex * pageSize].isSelect = "0"
        } else {
            listData!![position + pageIndex * pageSize].isSelect = "1"
        }
        checkBtn()
    }


    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_start" -> {
                var mListData = CopyOnWriteArrayList<DynamicDataBean>()
                if (listData!!.size > 0) {
                    for (i in 0 until listData!!.size) {
                        if (listData!![i].isSelect == "1") {
                            mListData.add(listData!![i])
                        }
                    }
                    if (mListData!!.size > 0) {
                        for (i in 0 until listData!!.size) {
                            if (listData!![i].isSelect != "1") {
                                mListData.add(listData!![i])
                            }
                        }
                        var intent = Intent(this@DynamicDataActivity, DynamicData2Activity::class.java)
                        intent.putExtra("listData", mListData)
                        intent.putExtra("code", code)
                        intent.putExtra("pageSize", pageSize)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(0, 0)
                    } else {
                        toast("请选择要监控数据")
                    }

                }
            }
            "btn_lastPage" -> {
                if (pageCount > 1 && pageSize > 0) {
                    preView()
                }
            }
            "btn_nextPage" -> {
                if (pageCount > 1) {
                    nextView()
                }
            }
            "ll_check" -> {
                var isAllCheck = true
                for (i in 0 until listData.size) {
                    if (listData[i].isSelect != "1") {
                        isAllCheck = false
                        break
                    }
                }
                if (isAllCheck) {
                    for (i in 0 until listData.size) {
                        listData[i].isSelect = "0"
                    }
                } else {
                    for (i in 0 until listData.size) {
                        listData[i].isSelect = "1"
                    }

                }
                checkBtn()
            }
            else -> {
                onItemClick(null, null, string!!.toInt(), string.toLong())
            }
        }
    }

    private fun checkBtn() {
        adapter!!.notifyDataSetChanged()
        var isAllCheck = true
        for (i in 0 until listData.size) {
            if (listData[i].isSelect != "1") {
                isAllCheck = false
                break
            }
        }
        if (isAllCheck) {
            tv_check.text = "取消全选"
            iv_left.setImageResource(R.mipmap.ic_check)
        } else {
            tv_check.text = "全选"
            iv_left.setImageResource(android.R.color.transparent)
        }
    }

    private fun preView() {
        pageIndex--
        // 检查Button是否可用。
        checkButton()
    }

    // 点击右边的Button，表示向后翻页，索引值要加1.
    private fun nextView() {
        pageIndex++
        // 检查Button是否可用。
        checkButton()
    }

    private fun checkButton() {
        // 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
        // 将向前翻页的按钮设为不可用。
        if (pageIndex <= 0) {
            btn_lastPage.isEnabled = false
            btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
        } else {
            btn_lastPage.isEnabled = true
            btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorprimary)

        }
        // 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        // 将向后翻页的按钮设为不可用。
        // 否则将2个按钮都设为可用的。
        if (listData!!.size - pageIndex * pageSize <= pageSize) {
            btn_nextPage.isEnabled = false
            btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
        } else {
            btn_nextPage.isEnabled = true
            btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorprimary)
        }
        // 刷新ListView里面的数值。
        if (listData!!.size - pageIndex * pageSize > 0) {
            adapter!!.notifyDataSetChanged()
        }
    }

    inner class ControlDynamicDataAdapter(private val items: CopyOnWriteArrayList<DynamicDataBean>, private val context: Context) : BaseAdapter() {

        override fun getCount(): Int {
            // ori表示到目前为止的前几页的总共的个数。
            val ori = pageSize * pageIndex

            // 值的总个数-前几页的个数就是这一页要显示的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
            return if (items!!.size - ori < pageSize) {
                items!!.size - ori
            } else {
                pageSize
            }// 如果比默认的值还要大，说明一页显示不完，还要用换一页显示，这一页用默认的值显示满就可以了。
        }

        override fun getItem(position: Int): Any {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            var holder: Holder? = null
            if (convertView == null) {
                holder = Holder()
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_dynamic_data, parent, false)
                holder.tv_name = convertView!!.findViewById<View>(R.id.tv_name) as TextView
                holder.tv_value = convertView.findViewById<View>(R.id.tv_value) as TextView
                holder.btn_chart = convertView.findViewById<View>(R.id.btn_chart) as TextView
                holder.iv_left = convertView.findViewById(R.id.iv_left) as ImageView

                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            holder.tv_name!!.text = items[position + pageIndex * pageSize].name
            if ("1" == items[position + pageIndex * pageSize].isSelect) {
                holder.iv_left!!.setImageResource(R.mipmap.ic_check)
            } else {
                holder.iv_left!!.setImageResource(android.R.color.transparent)
            }
            return convertView
        }

        inner class Holder {
            var tv_name: TextView? = null
            var tv_value: TextView? = null
            var btn_chart: TextView? = null
            var iv_left: ImageView? = null
        }
    }

}
