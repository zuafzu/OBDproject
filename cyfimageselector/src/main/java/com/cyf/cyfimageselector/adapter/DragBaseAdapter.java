//package com.cyf.cyfimageselector.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by caoyingfu on 2018/2/24.
// */
//
//public abstract class DragBaseAdapter extends BaseAdapter {
//
//    private List<String> list;
//    private Context context;
//    private int num = -1;
//
//    protected abstract int getLayoutId();
//
//    protected abstract void initView(MyNewAdapter.ViewHolder holder);
//
//    protected abstract void setViewValue(MyNewAdapter.ViewHolder holder, int position);
//
//
//    @Override
//    public int getCount() {
//        if (num != -1) {
//            if (list.size() > 0) {
//                for (int i = 0; i < list.size(); i++) {
//                    if (list.get(i).equals("add")) {
//                        list.remove(i);
//                        break;
//                    }
//                }
//            }
//            if (this.list.size() < num) {
//                this.list.add("add");
//            }
//        }
//        return list.size();
//    }
//
//    @Override
//    public String getItem(int i) {
//        return list.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//
//    public DragBaseAdapter(Context context, List<String> list, int num) {
//        this.context = context;
//        this.list = list;
//        this.num = num;
//    }
//
//    public DragBaseAdapter(Context context, List<String> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        MyNewAdapter.ViewHolder viewHolder = null;
//        if (view == null) {
//            view = LayoutInflater.from(context).inflate(getLayoutId(), null);
//            viewHolder = new MyNewAdapter.ViewHolder(view);
//            initView(viewHolder);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (MyNewAdapter.ViewHolder) view.getTag();
//        }
//        // 解决图片错位问题，GridView重复调用poistion=0的getView方法，ListView会全部重复调用
//        if (viewGroup.getChildCount() == i) {
//            //if (!list.get(i).equals(view.getTag(R.string.app_name))) {
//                setViewValue(viewHolder, i);
//            //}
//        }
//        return view;
//    }
//
//
//    public void moveItem(int start, int end) {
//        List<String> tmpList = new ArrayList<>();
//        if (start < end) {
//            tmpList.clear();
//            for (String s : list) tmpList.add(s);
//            String endMirror = tmpList.get(end);
//
//            tmpList.remove(end);
//            tmpList.add(end, getItem(start));
//
//            for (int i = start + 1; i <= end; i++) {
//                tmpList.remove(i - 1);
//                if (i != end) {
//                    tmpList.add(i - 1, getItem(i));
//                } else {
//                    tmpList.add(i - 1, endMirror);
//                }
//            }
//        } else {
//            tmpList.clear();
//            for (String s : list) tmpList.add(s);
//            String startMirror = tmpList.get(end);
//            tmpList.remove(end);
//            tmpList.add(end, getItem(start));
//
//            for (int i = start - 1; i >= end; i--) {
//                tmpList.remove(i + 1);
//                if (i != start) {
//                    tmpList.add(i + 1, getItem(i));
//                } else {
//                    tmpList.add(i + 1, startMirror);
//                }
//            }
//
//        }
//        list.clear();
//        list.addAll(tmpList);
//
//        notifyDataSetChanged();
//    }
//}