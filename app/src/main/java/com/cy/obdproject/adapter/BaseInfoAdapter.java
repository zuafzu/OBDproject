package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.BaseInfoBean;

import java.util.ArrayList;

public class BaseInfoAdapter extends BaseAdapter {

    private ArrayList<BaseInfoBean> items;
    private Context context;
    private int flag;

    public BaseInfoAdapter(ArrayList<BaseInfoBean> items, Context context, int flag) {
        this.items = items;
        this.context = context;
        this.flag = flag;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_base_info, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
            holder.view_line = (View) convertView.findViewById(R.id.view_line);
            holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(items.get(position).getName());
        holder.tv_value.setText(items.get(position).getValue());
        if (flag == 1) {
            holder.iv_left.setVisibility(View.GONE);
        } else {
            holder.iv_left.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public class Holder {
        public TextView tv_name, tv_value;
        public View view_line;
        public ImageView iv_left;
    }

}
