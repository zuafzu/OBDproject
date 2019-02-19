package com.cy.obdproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.base.LineDataSetList
import com.cy.obdproject.bean.DynamicDataBean
import com.cy.obdproject.tools.ChartUtils
import com.cy.obdproject.tools.LogTools
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_dynamic_data2.*
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class DynamicData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var pageSize = 9
    private var pageCount = 0

    private var code = ""

    // private var dynamicDataWorker: DynamicDataWorker? = null
    private var listData = CopyOnWriteArrayList<DynamicDataBean>()
    private var adapter: ControlDynamicDataAdapter? = null
    var pageIndex = 0

    private var time = 500L

    private var handler1: Handler? = null
    private var handler2: Handler? = null

    private var selectIndex = -1// 当前选中条目，-1表示没选中
    private var selectName = ""
    private val values = ArrayList<Entry>()
    private val labels = ArrayList<String>()
    private val dataSets = LineDataSetList()

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
        if (intent.hasExtra("code")) {
            code = intent.getStringExtra("code")
        }
        pageSize = intent.getIntExtra("pageSize", 0)

        setClickMethod(iv_back)
        setClickMethod(btn_lastPage)
        setClickMethod(btn_nextPage)
        setClickMethod(btn_start)
        setClickMethod(iv_close)

        rl_Chart.visibility = View.GONE

        if (intent.hasExtra("listData")) {
            listData.clear()
            val list = intent.getSerializableExtra("listData") as ArrayList<DynamicDataBean>?
            for (i in 0 until list!!.size) {
                listData.add(list[i])
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
                    adapter = ControlDynamicDataAdapter(listData!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        } else {
            toast("数据缺失")
        }
        // 直接开始读取
        // doMethod("btn_start")
    }

    fun onMyItemClick(p2: Int) {
        sendClick(this@DynamicData2Activity.localClassName, "" + p2)
        if ("1" == listData[p2 + pageIndex * pageSize].isSelect) {
            // 防止反复点击一个
            if (selectIndex != p2 + pageIndex * pageSize) {
                rl_Chart.visibility = View.VISIBLE
                selectIndex = p2 + pageIndex * pageSize
                labels.clear()
                values.clear()
            }
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
                    setXYLable(1)
                }
            } catch (e: Exception) {
                LogTools.errLog(e)
            }
            super.setData(data)
        }
    }

    // 显示更新图表，时间间隔time秒,time=0既真实获取数据速度
    private fun setXYLable(time: Int) {
        try {
            if (selectIndex >= 0 && selectIndex < listData.size) {
                rl_Chart.visibility = View.VISIBLE
                val sd = SimpleDateFormat("HH:mm:ss")
                var isShow = false
                if (labels.size != 0) {
                    val a = sd.format(Date(System.currentTimeMillis())).split(":")//当前时间
                    val b = labels[labels.size - 1].split(":")// 上次时间
                    if ((a[0].toInt() == 0 && b[0].toInt() == 23) || a[0].toInt() > b[0].toInt() || a[1].toInt() > b[1].toInt() || a[2].toInt() - b[2].toInt() >= time) {
                        isShow = true
                    }
                } else {
                    lineChart.clear()
                    isShow = true
                }
                if (isShow) {
                    selectName = listData[selectIndex].name
                    // 把listData[selectIndex].value分成单位和值
                    var unit = listData[selectIndex].unit
                    var value = listData[selectIndex].value.replace(listData[selectIndex].unit, "").toFloat()
                    // 赋值X,Y轴
                    values.add(Entry(labels.size.toFloat(), value))
                    // 赋值X轴
                    labels.add(sd.format(Date(System.currentTimeMillis())))
                    getData()
                    val lineDataSets = arrayOfNulls<LineDataSet>(dataSets.size())
                    for (i in 0 until dataSets.size()) {
                        lineDataSets[i] = dataSets.get(i)
                    }
                    val data = LineData(*lineDataSets)
                    lineChart.data = data
                    ChartUtils.showLineChart(this, labels, lineChart, unit)
                }
                val drawable = resources.getDrawable(R.drawable.fade_red)
                if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
                    val lineDataSet = lineChart!!.data.getDataSetByIndex(0) as LineDataSet
                    //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.fillDrawable = drawable
                    lineChart.invalidate()
                }
            }
        } catch (e: Exception) {
            Log.e("cyf123", "setXYLable err : " + e.message)
        }
    }

    private fun getData(): LineDataSetList {
        dataSets.clear()
        try {
            val d = LineDataSet(values, selectName)
            d.circleRadius = 2f
            //设置曲线值的圆点是实心还是空心
            d.setDrawCircleHole(false)
            d.setDrawValues(false)
            d.setDrawFilled(true)
            d.fillColor = ContextCompat.getColor(this, R.color.colorAccent)
            d.lineWidth = 2.5f
            d.color = ContextCompat.getColor(this, R.color.colorAccent)
            d.setCircleColor(ContextCompat.getColor(this, R.color.colorAccent))
            // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
            d.valueFormatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler -> value.toString() + "%" }
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            // d.mode = LineDataSet.Mode.CUBIC_BEZIER
            dataSets.add(d)
        } catch (e: Exception) {
            Log.e("cyf123", "getData err : " + e.message)
        }
        return dataSets
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
        // 处理只被选中的数据
        val mmlist = getPageList(listData!!, pageSize)[pageIndex]
        val mmmlist = CopyOnWriteArrayList<DynamicDataBean>()
        for (i in 0 until mmlist.size) {
            if (mmlist[i].isSelect == "1") {
                mmmlist.add(mmlist[i])
            }
        }
        // 调用jar包传递数据
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
        }, Gson().toJson(mmmlist))
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
                    // btn_start.text = "开始"
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
                    if (pageCount > 1 && pageSize > 0) {
                        preView()
                    }
                    labels.clear()
                    values.clear()
                    selectIndex = -1
                    selectName = ""
                    adapter!!.notifyDataSetChanged()
                    rl_Chart.visibility = View.GONE
                    // btn_start.text = "开始"
                    dismissProgressDialog()
                    // 继续开始
                    doMethod("btn_start")
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
                    labels.clear()
                    values.clear()
                    selectIndex = -1
                    selectName = ""
                    adapter!!.notifyDataSetChanged()
                    rl_Chart.visibility = View.GONE
                    // btn_start.text = "开始"
                    dismissProgressDialog()
                    // 继续开始
                    doMethod("btn_start")
                }, time)
            }
            "btn_start" -> {
                Log.e("zj", "当前页 List = " + getPageList(listData!!, pageSize)[pageIndex])
                if (isStart) {
                    toast("开始读取动态数据")
                    btn_start.text = "停止"
                    isStart = false

                    labels.clear()
                    values.clear()

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

                    val mIntent = Intent(this@DynamicData2Activity, DynamicDataActivity::class.java)
                    mIntent.putExtra("code", code)
                    mIntent.putExtra("listData", listData)
                    startActivity(mIntent)
                    finish()
                    overridePendingTransition(0, 0)

//                    showProgressDialog()
//                    Handler().postDelayed({
//                        dismissProgressDialog()
//                        btn_start.text = "开始"
//                    }, time)
                }
            }
            "iv_close" -> {
                rl_Chart.visibility = View.GONE
                labels.clear()
                values.clear()
                selectIndex = -1
                selectName = ""
            }
            else -> {
                onMyItemClick(string!!.toInt())
            }
        }
    }

    override fun onBackPressed() {
        if (!isUserConnected && !isProfessionalConnected) {
            // btn_start.text = "开始"
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
                holder.btn_chart!!.visibility = View.VISIBLE
                holder.btn_chart!!.setOnClickListener {
                    onMyItemClick(position)
                }
                if (items[position + pageIndex * pageSize].value == "") {
                    holder.tv_value!!.text = "暂无数据"
                } else {
                    holder.tv_value!!.text = items[position + pageIndex * pageSize].value
                }
            } else {
                holder.iv_left!!.setImageResource(android.R.color.transparent)
                holder.btn_chart!!.visibility = View.GONE
                holder.tv_value!!.text = ""
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
