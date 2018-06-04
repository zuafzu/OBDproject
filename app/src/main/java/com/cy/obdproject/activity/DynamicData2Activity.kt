package com.cy.obdproject.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import com.cy.obdproject.socket.SocketService
import com.cy.obdproject.worker.DynamicDataWorker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_dynamic_data2.*
import org.jetbrains.anko.toast

class DynamicData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var dynamicDataWorker: DynamicDataWorker? = null
    private var listData: ArrayList<DynamicDataBean>? = null
    private var adapter: ControlDynamicDataAdapter? = null
    private var isStart: Boolean = true
    var pageIndex = 0

    private var pageCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_data2)
        initView()
    }

    private fun initView() {

        setClickMethod(iv_back)
        setClickMethod(btn_lastPage)
        setClickMethod(btn_nextPage)
        setClickMethod(btn_start)

        if (intent.hasExtra("listData")) {
            listData = intent.getSerializableExtra("listData") as ArrayList<DynamicDataBean>?
            dynamicDataWorker = DynamicDataWorker()
            dynamicDataWorker!!.init(this, listData) { data ->
                setData(data)
                if (!isStart) {
                    Handler().postDelayed({
                        if (!isStart) {
                            dynamicDataWorker!!.start(getPageList(listData!!, Constant.pageSize)[pageIndex])
                        }
                    }, 3000)
                }
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
            toast("shujuqueshi")
        }
    }

    override fun setData(data: String?) {
        runOnUiThread {
            Log.i("cyf", "data : $data")
            dismissProgressDialog()
            try {
                val mlist = Gson().fromJson<List<DynamicDataBean>>(data, object : TypeToken<ArrayList<DynamicDataBean>>() {}.type) as ArrayList<DynamicDataBean>?
                listData!!.clear()
                listData!!.addAll(mlist!!)
                if (adapter == null) {
                    adapter = ControlDynamicDataAdapter(listData!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.i("cyf", "e : ${e.message}")
                toast(data!!)
            }
            super.setData(data)
        }
    }

    private fun initData() {
        if (isProfessionalConnected) {// 专家连接

        } else {
            showProgressDialog()
            if (SocketService.getIntance() != null && SocketService.getIntance()!!.isConnected()) {
                dynamicDataWorker!!.start(getPageList(listData!!, Constant.pageSize)[pageIndex])
            }
        }
    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
                btn_start.text = "开始"
                isStart = true
            }
            "btn_lastPage" -> {
                if (pageCount > 1 && Constant.pageSize > 0) {
                    preView()
                }
                btn_start.text = "开始"
                isStart = true
            }
            "btn_nextPage" -> {
                if (pageCount > 1) {
                    nextView()
                }
                btn_start.text = "开始"
                isStart = true
            }
            "btn_start" -> {
                Log.e("zj", "当前页 List = " + getPageList(listData!!, Constant.pageSize)[pageIndex])
                if (isStart) {
                    btn_start.text = "停止"
                    isStart = false
                    initData()
                } else {
                    btn_start.text = "开始"
                    isStart = true
                }
            }
        }
    }

    override fun onBackPressed() {
        btn_start.text = "开始"
        isStart = true
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

    inner class ControlDynamicDataAdapter(private val items: ArrayList<DynamicDataBean>, private val context: Context) : BaseAdapter() {

        override fun getCount(): Int {
            // ori表示到目前为止的前几页的总共的个数。
            val ori = Constant.pageSize * pageIndex

            // 值的总个数-前几页的个数就是这一页要显示的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
            return if (items!!.size - ori < Constant.pageSize) {
                items!!.size - ori

//                Log.e("zj","getCount 111= "+(items!!.size - ori))
            } else {
//                Log.e("zj","getCount 222 = "+Constant.pageSize)

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

            //        if (position != (getCount() - 1)) {
            //            holder.view_line.setVisibility(View.VISIBLE);
            //        } else {
            //            holder.view_line.setVisibility(View.GONE);
            //        }
            return convertView
        }

        inner class Holder {
            var tv_name: TextView? = null
            var tv_value: TextView? = null
            var view_line: View? = null
        }
    }

    private fun getPageList(targe: ArrayList<DynamicDataBean>, size: Int): ArrayList<ArrayList<DynamicDataBean>> {
        val listArr = ArrayList<ArrayList<DynamicDataBean>>()
        //获取被拆分的数组个数
        val arrSize = if (targe.size % size === 0) targe.size / size else targe.size / size + 1
        for (i in 0 until arrSize) {
            val sub = ArrayList<DynamicDataBean>()
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
