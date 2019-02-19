package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.DynamicDataBean;

import java.util.concurrent.CopyOnWriteArrayList;

public class DynamicDataAdapter extends BaseAdapter {

    private CopyOnWriteArrayList<DynamicDataBean> items;
    private Context context;

    public DynamicDataAdapter(CopyOnWriteArrayList<DynamicDataBean> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_dynamic_data, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
            holder.btn_chart = (TextView) convertView.findViewById(R.id.btn_chart);
            holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(items.get(position).getName());

        if ("1".equals(items.get(position).getIsSelect())) {
            holder.iv_left.setImageResource(R.mipmap.ic_check);
        } else {
            holder.iv_left.setImageResource(android.R.color.transparent);
        }
//        if (position != (getCount() - 1)) {
//            holder.view_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.view_line.setVisibility(View.GONE);
//        }
        return convertView;
    }

    public class Holder {
        public TextView tv_name, tv_value, btn_chart;
        public ImageView iv_left;
    }

}
