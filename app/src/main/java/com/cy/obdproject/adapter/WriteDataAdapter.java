package com.cy.obdproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.WriteDataBean;

import java.util.ArrayList;

public class WriteDataAdapter extends BaseAdapter {

    private ArrayList<WriteDataBean> items;
    private Context context;

    public WriteDataAdapter(ArrayList<WriteDataBean> items, Context context) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_base_info, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
            holder.view_line = (View) convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_value.setVisibility(View.VISIBLE);
        String[] str = items.get(position).getFileName().split("\\.");
        String name2 = str[str.length - 1];
        if (items.get(position).getFileName().contains("_")) {
            String name1 = items.get(position).getFileName().split("_")[0];
            holder.tv_value.setText(name2.toUpperCase() + "  " + name1);
            if ("1".equals(items.get(position).getLocalHas())) {
                holder.tv_name.setText(items.get(position).getFileName().
                        replace(name1 + "_", "").
                        replace("." + name2, "") + "(本地)");
            } else {
                holder.tv_name.setText(items.get(position).getFileName().
                        replace(name1 + "_", "").
                        replace("." + name2, ""));
            }
        } else {
            holder.tv_value.setText(name2.toUpperCase());
            if ("1".equals(items.get(position).getLocalHas())) {
                holder.tv_name.setText(items.get(position).getFileName().
                        replace("." + name2, "") + "(本地)");
            } else {
                holder.tv_name.setText(items.get(position).getFileName().
                        replace("." + name2, ""));
            }
        }
        return convertView;
    }

    public class Holder {
        public TextView tv_name, tv_value;
        public View view_line;
    }

}
