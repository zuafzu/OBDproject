package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.BaseInfoBean;

import java.util.ArrayList;

public class ErrorCode2Adapter extends BaseAdapter {

    private ArrayList<BaseInfoBean> items;
    private Context context;
    private int flag;

    public ErrorCode2Adapter(ArrayList<BaseInfoBean> items, Context context, int flag) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_error_code2, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(items.get(position).getName());
        holder.tv_value.setText(items.get(position).getValue());
        return convertView;
    }

    public class Holder {
        public TextView tv_name, tv_value;
    }

}
