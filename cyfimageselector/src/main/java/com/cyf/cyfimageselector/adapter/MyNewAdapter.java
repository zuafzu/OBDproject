//package com.cyf.cyfimageselector.adapter;
//
//import android.content.Context;
//import android.util.SparseArray;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.cyf.cyfimageselector.R;
//import com.cyf.cyfimageselector.utils.SDCardImageLoader;
//
//import java.util.List;
//
///**
// * Created by caoyingfu on 2018/2/24.
// */
//
//public class MyNewAdapter extends DragBaseAdapter {
//
//    private Context context;
//    private List<String> list;
//    private boolean isdelete;
//
//    private int type = 0;//0查看，1编辑
//
//    public MyNewAdapter(Context context, List<String> list, boolean isdelete, int num) {
//        super(context, list, num);
//        this.context = context;
//        this.list = list;
//        this.isdelete = isdelete;
//
//        if (this.list.size() > 0) {
//            for (int i = 0; i < this.list.size(); i++) {
//                if (this.list.get(i).equals("add")) {
//                    this.list.remove(i);
//                    break;
//                }
//            }
//        }
//        if (this.list.size() < num) {
//            this.list.add("add");
//        }
//        type = 1;
//    }
//
//    public MyNewAdapter(Context context, List<String> list) {
//        super(context, list);
//        this.context = context;
//        this.list = list;
//        type = 0;
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.photo_wall_item;
//    }
//
//    @Override
//    protected void initView(ViewHolder holder) {
//        holder.addView(R.id.photo_wall_item_photo);
//        holder.addView(R.id.view_top);
//        holder.addView(R.id.photo_wall_item_cb);
//        holder.addView(R.id.btn_delete);
//    }
//
//    @Override
//    protected void setViewValue(final ViewHolder holder, final int position) {
//        holder.getView(R.id.view_top).setVisibility(View.GONE);
//        holder.getView(R.id.photo_wall_item_cb).setVisibility(View.GONE);
//        if (type == 0) {
//            setImg(holder, position);
//        } else {
//            if (list.get(position).equals("add")) {
//                ((View) holder.getView(R.id.photo_wall_item_photo).getParent()).setTag(R.string.app_name, "add");
//                holder.getView(R.id.btn_delete).setVisibility(View.GONE);
//                ((ImageView) holder.getView(R.id.photo_wall_item_photo)).setImageResource(R.mipmap.ic_add_image);
//            } else {
//                if (isdelete) {
//                    holder.getView(R.id.btn_delete).setVisibility(View.VISIBLE);
//                    holder.getView(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            list.remove(position);
//                            // notifyDataSetInvalidated();
//                            notifyDataSetChanged();
//                        }
//                    });
//                } else {
//                    holder.getView(R.id.btn_delete).setVisibility(View.GONE);
//                }
//                setImg(holder, position);
//            }
//        }
//    }
//
//    private void setImg(final ViewHolder holder, final int position) {
//        ((View) holder.getView(R.id.photo_wall_item_photo).getParent()).setTag(R.string.app_name, list.get(position));
//        SDCardImageLoader.setImgThumbnail(context, list.get(position), ((ImageView) holder.getView(R.id.photo_wall_item_photo)));
//    }
//
//    public static class ViewHolder {
//
//        private SparseArray<View> array;
//        private View view;
//
//        public ViewHolder(View view) {
//            this.view = view;
//            this.array = new SparseArray<>();
//        }
//
//        public void addView(int id) {
//            array.append(id, view.findViewById(id));
//        }
//
//        public View getView(int id) {
//            return (View) array.get(id);
//        }
//
//    }
//
//}