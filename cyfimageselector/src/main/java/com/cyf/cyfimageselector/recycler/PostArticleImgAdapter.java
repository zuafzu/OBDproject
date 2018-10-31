package com.cyf.cyfimageselector.recycler;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cyf.cyfimageselector.R;
import com.cyf.cyfimageselector.model.PhotoConfigure;
import com.cyf.cyfimageselector.ui.PhotoPreviewActivity;
import com.cyf.cyfimageselector.ui.PhotoWallActivity2;
import com.cyf.cyfimageselector.utils.SDCardImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoyingfu on 2018/2/26.
 */

public class PostArticleImgAdapter extends RecyclerView.Adapter<PostArticleImgAdapter.MyViewHolder> {

    private Context mContext;
    private CyfRecyclerView cyfRecyclerView;
    private PhotoConfigure photoConfigure;
    private LayoutInflater mLayoutInflater;

    private int type = 0;// 0带保存按钮，3不带保存按钮
    private boolean isShow = true;// 是否加载无缓存的图片

    public void setClick(boolean isClick) {
        if (photoConfigure != null) {
            photoConfigure.setClick(isClick);
        }
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public Context getmContext() {
        return mContext;
    }

    public PostArticleImgAdapter(Context context, CyfRecyclerView cyfRecyclerView, PhotoConfigure photoConfigure) {
        this.mContext = context;
        this.cyfRecyclerView = cyfRecyclerView;
        this.photoConfigure = photoConfigure;

        if (photoConfigure.isSave()) {
            this.type = 0;
        } else {
            this.type = 3;
        }
        this.mLayoutInflater = LayoutInflater.from(context);
        if (photoConfigure.getType() == PhotoConfigure.EditImg) {
            if (photoConfigure.getList().size() > 0) {
                for (int i = 0; i < photoConfigure.getList().size(); i++) {
                    if (photoConfigure.getList().get(i).equals("add")) {
                        photoConfigure.getList().remove(i);
                        break;
                    }
                }
            }
            if (photoConfigure.getList().size() < photoConfigure.getNum()) {
                photoConfigure.getList().add("add");
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.photo_wall_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.view_top.setVisibility(View.GONE);
        holder.photo_wall_item_cb.setVisibility(View.GONE);
        if (photoConfigure.getType() == PhotoConfigure.WatchImg) {
            SDCardImageLoader.setImgThumbnail(mContext, photoConfigure.getList().get(holder.getAdapterPosition()), ((ImageView) holder.photo_wall_item_photo));
            holder.photo_wall_item_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (photoConfigure.isClick()) {
                        if (cyfRecyclerView.getListener() != null) {
                            cyfRecyclerView.getListener().onClick(holder.getAdapterPosition());
                        } else {
                            PhotoPreviewActivity.openPhotoPreview((Activity) mContext, holder.getAdapterPosition(), photoConfigure.getList().size(), type, (ArrayList<String>) photoConfigure.getList(), new PhotoPreviewActivity.OnHanlderResultCallback() {
                                @Override
                                public void onHanlderSuccess(List<String> resultList) {

                                }
                            });
                        }
                    }
                }
            });
        } else {
            if (photoConfigure.getList().get(holder.getAdapterPosition()).equals("add")) {
                ((View) holder.photo_wall_item_photo.getParent()).setTag(R.string.app_name, "add");
                holder.btn_delete.setVisibility(View.GONE);
                ((ImageView) holder.photo_wall_item_photo).setImageResource(R.mipmap.ic_add_image);
            } else {
                if (photoConfigure.isDelete()) {
                    holder.btn_delete.setVisibility(View.VISIBLE);
                    holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            photoConfigure.getList().remove(holder.getAdapterPosition());
                            setmList();
                            notifyItemRemoved(holder.getAdapterPosition());
                        }
                    });
                } else {
                    holder.btn_delete.setVisibility(View.GONE);
                }
                SDCardImageLoader.setImgThumbnail(mContext, photoConfigure.getList().get(holder.getAdapterPosition()), ((ImageView) holder.photo_wall_item_photo));
            }
            holder.photo_wall_item_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (photoConfigure.getList().size() == 0 ||
                            photoConfigure.getList().size() == holder.getAdapterPosition() ||
                            photoConfigure.getList().get(holder.getAdapterPosition()).equals("add")) {
                        for (int j = 0; j < photoConfigure.getList().size(); j++) {
                            if (photoConfigure.getList().get(j).equals("add")) {
                                photoConfigure.getList().remove(j);
                                break;
                            }
                        }
                        PhotoWallActivity2.openImageSelecter((Activity) mContext, photoConfigure, new PhotoWallActivity2.OnHanlderResultCallback() {
                            @Override
                            public void onHanlderSuccess(List<String> resultList) {
                                photoConfigure.getList().clear();
                                photoConfigure.getList().addAll(resultList);
                                setmList();
                                notifyDataSetChanged();
                            }
                        });
                    } else {
                        for (int j = 0; j < photoConfigure.getList().size(); j++) {
                            if (photoConfigure.getList().get(j).equals("add")) {
                                photoConfigure.getList().remove(j);
                                break;
                            }
                        }
                        PhotoPreviewActivity.openPhotoPreview((Activity) mContext, holder.getAdapterPosition(), photoConfigure.getNum(), 2, (ArrayList<String>) photoConfigure.getList(), new PhotoPreviewActivity.OnHanlderResultCallback() {
                            @Override
                            public void onHanlderSuccess(List<String> resultList) {
                                photoConfigure.getList().clear();
                                photoConfigure.getList().addAll(resultList);
                                setmList();
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
            if (cyfRecyclerView.getOnUpdateData() != null) {
                int line = photoConfigure.getList().size() / photoConfigure.getColnum();
                if (photoConfigure.getList().size() % photoConfigure.getColnum() != 0) {
                    line++;
                }
                cyfRecyclerView.getOnUpdateData().reflush(line);
            }
        }
    }

    @Override
    public int getItemCount() {
        return photoConfigure.getList() == null ? 0 : photoConfigure.getList().size();
    }


    public void setmList() {
        if (photoConfigure.getList().size() > 0) {
            for (int i = 0; i < photoConfigure.getList().size(); i++) {
                if (photoConfigure.getList().get(i).equals("add")) {
                    photoConfigure.getList().remove(i);
                    break;
                }
            }
        }
        if (photoConfigure.getList().size() < photoConfigure.getNum()) {
            photoConfigure.getList().add("add");
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView photo_wall_item_photo;
        View view_top;
        AppCompatCheckBox photo_wall_item_cb;
        ImageView btn_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            photo_wall_item_photo = itemView.findViewById(R.id.photo_wall_item_photo);
            view_top = itemView.findViewById(R.id.view_top);
            photo_wall_item_cb = itemView.findViewById(R.id.photo_wall_item_cb);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface OnUpdateData {
        void reflush(int line);
    }

}