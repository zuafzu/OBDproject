package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cy.obdproject.R;
import com.cy.obdproject.bean.ErrorCodeBean;

import java.util.ArrayList;

public class ErrorCodeAdapter extends BaseAdapter {

    private ArrayList<ErrorCodeBean> items;
    private Context context;

    public ErrorCodeAdapter(ArrayList<ErrorCodeBean> items, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_error_code, parent, false);
            holder.tv_code = (TextView) convertView.findViewById(R.id.tv_code);
            holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
            holder.tv_freeze = (TextView) convertView.findViewById(R.id.tv_freeze);

            holder.view_line = (View) convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_code.setText(items.get(position).getCode());
        holder.tv_msg.setText(items.get(position).getMsg());

        holder.tv_freeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorCodeClick.setOnErrorCodeClick("", position);

            }
        });

//        if (position != (getCount() - 1)) {
//            holder.view_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.view_line.setVisibility(View.GONE);
//        }
        return convertView;
    }

    OnErrorCodeClick onErrorCodeClick;

    public interface OnErrorCodeClick {
        void setOnErrorCodeClick(String id, int position);
    }

    public void setOnErrorCodeClick(OnErrorCodeClick onErrorCodeClick) {
        this.onErrorCodeClick = onErrorCodeClick;
    }

    public class Holder {
        public TextView tv_code, tv_msg,tv_freeze;
        public View view_line;
    }

}
