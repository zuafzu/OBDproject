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
import com.cy.obdproject.socket.SocketService;

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
//        if (position != (getCount() - 1)) {
//            holder.view_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.view_line.setVisibility(View.GONE);
//        }
        if (SocketService.Companion.getIntance() != null && SocketService.Companion.getIntance().isConnected()) {
            if (items.get(position).getName().contains("车辆运输模式")) {
                int value = -1;
                try {
                    value = Integer.valueOf(items.get(position).getValue());
                } catch (Exception e) {

                }
                if (value == 0) {
                    holder.tv_value.setText("退出运输模式");
                } else if (value == 1) {
                    holder.tv_value.setText("激活运输模式");
                } else {
                    holder.tv_value.setText("解析异常");
                }
            }
            if (items.get(position).getName().contains("车辆售后服务模式")) {
                int value = -1;
                try {
                    value = Integer.valueOf(items.get(position).getValue());
                } catch (Exception e) {

                }
                if (value == 0) {
                    holder.tv_value.setText("退出售后服务模式");
                } else if (value == 1) {
                    holder.tv_value.setText("激活售后服务模式");
                } else {
                    holder.tv_value.setText("解析异常");
                }
            }
            if (items.get(position).getName().contains("a2l文件ID")) {
                int value = -1;
                try {
                    value = Integer.valueOf(items.get(position).getValue());
                } catch (Exception e) {

                }
                if (value == 0) {
                    holder.tv_value.setText("退出售后服务模式");
                } else if (value == 1) {
                    holder.tv_value.setText("激活售后服务模式");
                } else {
                    holder.tv_value.setText("解析异常");
                }
            }
            if (items.get(position).getName().contains("噪声Simu语音配置")) {
                int value = -1;
                try {
                    value = Integer.valueOf(items.get(position).getValue());
                } catch (Exception e) {

                }
                if (value == 0) {
                    holder.tv_value.setText("未配置");
                } else if (value == 1) {
                    holder.tv_value.setText("声音1");
                } else if (value == 2) {
                    holder.tv_value.setText("声音2");
                } else {
                    holder.tv_value.setText("解析异常");
                }
            }
        }

        return convertView;
    }

    public class Holder {
        public TextView tv_name, tv_value;
        public View view_line;
        public ImageView iv_left;
    }

}
