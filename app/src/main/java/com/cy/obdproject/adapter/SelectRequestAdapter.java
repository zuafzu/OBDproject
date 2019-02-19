package com.cy.obdproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_selecet2, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(items.get(position).getRequestName());
        String strUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1545047587086&di=ed48ea529b1cef3a981a8ea02b70ed61&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F010ae45a3626b7a80121db80f25c77.jpg%401280w_1l_2o_100sh.jpg";
        Glide.with(context).load(strUrl).apply(RequestOptions.bitmapTransform(new CircleCrop()).error(R.mipmap.ic_head)).into(holder.iv_head);
        return convertView;
    }

    public class Holder {
        public ImageView iv_head;
        public TextView tv_name, tv_phone, tv_address;
    }

}
