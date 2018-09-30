package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.DynamicDataBean
import com.cy.obdproject.constant.Constant
import com.cy.obdproject.tools.LogTools
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dynamic_data2.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.util.concurrent.CopyOnWriteArrayList

class DynamicData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var code = ""

    // private var dynamicDataWorker: DynamicDataWorker? = null
    private var listData = CopyOnWriteArrayList<DynamicDataBean>()
    private var adapter: ControlDynamicDataAdapter? = null
    var pageIndex = 0

    private var time = 500L

    private var pageCount = 0

    private var handler1: Handler? = null
    private var handler2: Handler? = null

    companion object {
        var isStart: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_data2)
        initView()
    }

    override fun onDestroy() {
        isStart = true
        super.onDestroy()
    }

    private fun initView() {
        code = intent.getStringExtra("code")

        setClickMethod(iv_back)
        setClickMethod(btn_lastPage)
        setClickMethod(btn_nextPage)
        setClickMethod(btn_start)

        if (intent.hasExtra("listData")) {
            listData.clear()
            val list = intent.getSerializableExtra("listData") as ArrayList<DynamicDataBean>?
            for (i in 0 until list!!.size) {
                listData.add(list[i])
            }
            Log.e("zj", "listData = " + listData.toString())
            if (listData!!.size > 0) {
                pageCount = (listData!!.size - 1) / 10 + 1

                if (pageCount == 1) {
                    btn_lastPage.isEnabled = false
                    btn_nextPage.isEnabled = false
                    btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
                    btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
                }
                if (adapter == null) {

                    Log.e("zj", "11111")

                    adapter = ControlDynamicDataAdapter(listData!!, this)
                    listView!!.adapter = adapter
                } else {
                    Log.e("zj", "2222")

                    adapter!!.notifyDataSetChanged()
                }

                Log.e("zj", "count = " + adapter!!.count)
            }
        } else {
            toast("数据缺失")
        }
    }

    override fun setData(data: String?) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            dismissProgressDialog()
            try {
                if (data!!.startsWith("toast")) {
                    toast(data!!.replace("toast", ""))
                } else {
                    val jo = JSONArray(data)
                    //val mlist = Gson().fromJson<List<DynamicDataBean>>(data, object : TypeToken<ArrayList<DynamicDataBean>>() {}.type) as ArrayList<DynamicDataBean>?
                    for (i in 0 until jo!!.length()) {
                        for (j in 0 until listData!!.size) {
                            if (listData[j].name == jo.optJSONObject(i).optString("Name")) {
                                listData[j].value = jo.optJSONObject(i).optString("Value")
                            }
                        }
                    }
                    if (adapter == null) {
                        adapter = ControlDynamicDataAdapter(listData!!, this)
                        listView!!.adapter = adapter
                    } else {
                        adapter!!.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                LogTools.errLog(e)
            }
            super.setData(data)
        }
    }

    private fun initData() {
        if (isProfessionalConnected) {// 专家连接

        } else {
            start()
        }
    }

    private fun start() {
        handler1 = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                myApp.publicUnit.setBreak(true)
                setData("toast" + msg.obj.toString())
            }
        }
        myApp.publicUnit.InitDynamicData(@SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    4 -> {
                        val data = msg.obj.toString()
                        setData(data)
                    }
                }
                super.handleMessage(msg)
            }
        }, Gson().toJson(getPageList(listData!!, Constant.pageSize)[pageIndex]))
        myApp.publicUnit.SetEvent(handler1, code.split(",")[1])
    }

    private fun stop() {
        handler2 = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                myApp.publicUnit.setBreak(true)
                setData("toast" + msg.obj.toString())
            }
        }
        myApp.publicUnit.SetEvent(handler2, code.split(",")[2])
    }


    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                var time = time + 100L
                if (isStart) {
                    time = 0L
                } else {
                    toast("停止读取动态数据")
                }
                isStart = true
                stop()
                // dynamicDataWorker = null
                showProgressDialog()
                Handler().postDelayed({
                    btn_start.text = "开始"
                    dismissProgressDialog()
                    finish()
                }, time)
            }
            "btn_lastPage" -> {
                var time = time + 100L
                if (isStart) {
                    time = 0L
                } else {
                    toast("停止读取动态数据")
                }
                isStart = true
                stop()
                // dynamicDataWorker = null
                showProgressDialog()
                Handler().postDelayed({
                    if (pageCount > 1 && Constant.pageSize > 0) {
                        preView()
                    }
                    btn_start.text = "开始"
                    dismissProgressDialog()
                }, time)
            }
            "btn_nextPage" -> {
                var time = time + 100L
                if (isStart) {
                    time = 0L
                } else {
                    toast("停止读取动态数据")
                }
                isStart = true
                stop()
                // dynamicDataWorker = null
                showProgressDialog()
                Handler().postDelayed({
                    if (pageCount > 1) {
                        nextView()
                    }
                    btn_start.text = "开始"
                    dismissProgressDialog()
                }, time)
            }
            "btn_start" -> {
                Log.e("zj", "当前页 List = " + getPageList(listData!!, Constant.pageSize)[pageIndex])
                if (isStart) {
                    toast("开始读取动态数据")
                    btn_start.text = "停止"
                    isStart = false
                    initData()
                } else {
                    var time = time + 100L
                    if (isStart) {
                        time = 0L
                    } else {
                        toast("停止读取动态数据")
                    }
                    isStart = true
                    stop()
                    // dynamicDataWorker = null
                    showProgressDialog()
                    Handler().postDelayed({
                        dismissProgressDialog()
                        btn_start.text = "开始"
                    }, time)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!isUserConnected && !isProfessionalConnected) {
            btn_start.text = "开始"
            if (isStart) {
                time = 0L
            } else {
                toast("停止读取动态数据")
            }
            isStart = true
            stop()
        }
        super.onBackPressed()
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
        if (listData!!.size - pageIndex * Constant.pageSize <= Constant.pageSize) {
            btn_nextPage.isEnabled = false
            btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
        } else {
            btn_nextPage.isEnabled = true
            btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorprimary)
        }
        // 刷新ListView里面的数值。
        if (listData!!.size - pageIndex * Constant.pageSize > 0) {
            adapter!!.notifyDataSetChanged()
        }
    }

    inner class ControlDynamicDataAdapter(private val items: CopyOnWriteArrayList<DynamicDataBean>, private val context: Context) : BaseAdapter() {

        override fun getCount(): Int {
            // ori表示到目前为止的前几页的总共的个数。
            val ori = Constant.pageSize * pageIndex

            // 值的总个数-前几页的个数就是这一页要显示的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
            return if (items!!.size - ori < Constant.pageSize) {
                items!!.size - ori
            } else {
                Constant.pageSize
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
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_control_dynamic_data, parent, false)
                holder.tv_name = convertView!!.findViewById<View>(R.id.tv_name) as TextView
                holder.tv_value = convertView.findViewById<View>(R.id.tv_value) as TextView

                holder.view_line = convertView.findViewById(R.id.view_line) as View
                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            holder.tv_name!!.text = items.get(position + pageIndex * Constant.pageSize).name
            holder.tv_value!!.text = items.get(position + pageIndex * Constant.pageSize).value
            return convertView
        }

        inner class Holder {
            var tv_name: TextView? = null
            var tv_value: TextView? = null
            var view_line: View? = null
        }
    }

    private fun getPageList(targe: CopyOnWriteArrayList<DynamicDataBean>, size: Int): CopyOnWriteArrayList<CopyOnWriteArrayList<DynamicDataBean>> {
        val listArr = CopyOnWriteArrayList<CopyOnWriteArrayList<DynamicDataBean>>()
        //获取被拆分的数组个数
        val arrSize = if (targe.size % size === 0) targe.size / size else targe.size / size + 1
        for (i in 0 until arrSize) {
            val sub = CopyOnWriteArrayList<DynamicDataBean>()
            //把指定索引数据放入到list中
            for (j in i * size until size * (i + 1)) {
                if (j <= targe.size - 1) {
                    sub.add(targe[j])
                }
            }
            listArr.add(sub)
        }
        return listArr
    }
}
