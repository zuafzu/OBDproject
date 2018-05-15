package com.cy.obdproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.BaseInfoBean;
import com.cy.obdproject.bean.DynamicDataBean;

import java.util.ArrayList;

public class DynamicDataAdapter extends BaseAdapter {

    private ArrayList<DynamicDataBean> items;
    private Context context;

    public DynamicDataAdapter(ArrayList<DynamicDataBean> items, Context context) {
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
            holder.view_line = (View) convertView.findViewById(R.id.view_line);
            holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(items.get(position).getName());

        if ("1".equals(items.get(position).getIsSelect())){
            holder.iv_left.setImageResource(R.mipmap.ic_checkbox_checked);
        }else {
            holder.iv_left.setImageResource(R.mipmap.ic_checkbox_unchecked);
        }
//        if (position != (getCount() - 1)) {
//            holder.view_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.view_line.setVisibility(View.GONE);
//        }
        return convertView;
    }

    public class Holder {
        public TextView tv_name, tv_value;
        public View view_line;
        public ImageView iv_left;
    }

}
