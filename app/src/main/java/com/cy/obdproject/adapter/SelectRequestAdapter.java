package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.RequestBean;

import java.util.ArrayList;

public class SelectRequestAdapter extends BaseAdapter {

    private ArrayList<RequestBean> items;
    private Context context;

    public SelectRequestAdapter(ArrayList<RequestBean> items, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_selecet, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
            holder.view_line = (View) convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.tv_name.setText(items.get(position).getRequestName());
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
    }

}
