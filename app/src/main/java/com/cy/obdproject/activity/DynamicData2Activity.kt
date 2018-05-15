package com.cy.obdproject.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cy.obdproject.R
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.DynamicDataBean
import kotlinx.android.synthetic.main.activity_dynamic_data2.*

class DynamicData2Activity : BaseActivity(), BaseActivity.ClickMethoListener {
    private var listData: ArrayList<DynamicDataBean>? = null
    private var adapter: ControlDynamicDataAdapter? = null

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
            if (listData!!.size > 0) {
                pageCount = (listData!!.size - 1) / 10 + 1;

                if (pageCount == 1) {
                    btn_lastPage.isEnabled = false
                    btn_nextPage.isEnabled = false
                    btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
                    btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
                } else {

                }
                if (adapter == null) {
                    adapter = ControlDynamicDataAdapter(listData!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        }

    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
            "btn_lastPage" -> {
                if (pageCount > 1 && index > 0) {
                    preView()
                }
            }
            "btn_nextPage" -> {
                if (pageCount > 1) {
                    nextView()
                }

            }
            "btn_start" -> {

            }
        }
    }

    fun preView() {
        index--
        // 检查Button是否可用。
        checkButton()
    }

    // 点击右边的Button，表示向后翻页，索引值要加1.
    fun nextView() {
        index++
        // 检查Button是否可用。
        checkButton()
    }

    fun checkButton() {
        // 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
        // 将向前翻页的按钮设为不可用。
        if (index <= 0) {
            btn_lastPage.isEnabled = false
            btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
        } else {
            btn_lastPage.isEnabled = true
            btn_lastPage.setBackgroundResource(R.drawable.shape_btn_colorprimary)

        }
        // 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        // 将向后翻页的按钮设为不可用。
        if (listData!!.size - index * VIEW_COUNT <= VIEW_COUNT) {
            btn_nextPage.isEnabled = false
            btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorhint)
        } else {
            btn_nextPage.isEnabled = true
            btn_nextPage.setBackgroundResource(R.drawable.shape_btn_colorprimary)
        }// 否则将2个按钮都设为可用的。
        // 刷新ListView里面的数值。
        if (listData!!.size - index * VIEW_COUNT > 0) {
            adapter!!.notifyDataSetChanged()
        }
    }

    var VIEW_COUNT = 10
    // 用于显示页号的索引
    var index = 0

    inner class ControlDynamicDataAdapter(private val items: ArrayList<DynamicDataBean>, private val context: Context) : BaseAdapter() {

        override fun getCount(): Int {
            // ori表示到目前为止的前几页的总共的个数。
            val ori = VIEW_COUNT * index

            // 值的总个数-前几页的个数就是这一页要显示的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
            return if (items!!.size - ori < VIEW_COUNT) {
                items!!.size - ori
            } else {
                VIEW_COUNT
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

            holder.tv_name!!.text = items.get(position + index * VIEW_COUNT).name
            holder.tv_value!!.text = items.get(position + index * VIEW_COUNT).value

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
}
