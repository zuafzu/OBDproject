package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.ErrorCodeBean;
import com.cy.obdproject.bean.IOTestBean;

import java.util.ArrayList;

public class IOTestAdapter extends BaseAdapter {

    private ArrayList<IOTestBean> items;
    private Context context;

    public IOTestAdapter(ArrayList<IOTestBean> items, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_io_test, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_test = (TextView) convertView.findViewById(R.id.tv_test);


            holder.view_line = (View) convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(items.get(position).getName());

        holder.tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestClick.setOnTestClick("", position);

            }
        });
//        if (position != (getCount() - 1)) {
//            holder.view_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.view_line.setVisibility(View.GONE);
//        }
        return convertView;
    }

    OnTestClick onTestClick;

    public interface OnTestClick {
        void setOnTestClick(String id, int position);
    }

    public void setOnTestClick(OnTestClick onTestClick) {
        this.onTestClick = onTestClick;
    }


    public class Holder {
        public TextView tv_name,tv_test;
        public View view_line;
    }

}
