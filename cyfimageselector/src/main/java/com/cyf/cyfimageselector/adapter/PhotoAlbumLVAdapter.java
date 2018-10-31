package com.cyf.cyfimageselector.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyf.cyfimageselector.R;
import com.cyf.cyfimageselector.model.PhotoAlbumLVItem;
import com.cyf.cyfimageselector.utils.SDCardImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * 选择相册页面,ListView的adapter
 * Created by hanj on 14-10-14.
 */
public class PhotoAlbumLVAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PhotoAlbumLVItem> list;

    private int filesIndex = 0;//选中文件夹位置

    public void setFilesIndex(int filesIndex) {
        this.filesIndex = filesIndex;
    }

    public PhotoAlbumLVAdapter(Context context, ArrayList<PhotoAlbumLVItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.photo_album_lv_item, null);
            holder = new ViewHolder();

            holder.firstImageIV = (ImageView) convertView.findViewById(R.id.select_img_gridView_img);
            holder.pathNameTV = (TextView) convertView.findViewById(R.id.select_img_gridView_path);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == filesIndex) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.shape_circle);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.pathNameTV.setCompoundDrawables(null, null, drawable, null);
        } else {
            holder.pathNameTV.setCompoundDrawables(null, null, null, null);
        }

        //图片（缩略图）
        String filePath = list.get(position).getFirstImagePath();
        SDCardImageLoader.setImgThumbnail(context,filePath,holder.firstImageIV);
        //文字
        holder.pathNameTV.setText(getPathNameToShow(list.get(position)));

        return convertView;
    }

    private class ViewHolder {
        ImageView firstImageIV;
        TextView pathNameTV;
    }

    /**
     * 根据完整路径，获取最后一级路径，并拼上文件数用以显示。
     */
    private String getPathNameToShow(PhotoAlbumLVItem item) {
        String absolutePath = item.getPathName();
        int lastSeparator = absolutePath.lastIndexOf(File.separator);
        return absolutePath.substring(lastSeparator + 1) + "(" + item.getFileCount() + ")";
    }

}
