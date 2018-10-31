package com.cyf.cyfimageselector.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 相册选择器配置器
 * Created by Administrator on 2016/8/24.
 */
public class PhotoConfigure implements Serializable {

    public final static int WatchImg = 0;// 查看模式
    public final static int EditImg = 1;// 编辑模式
    public static final int autoColNum = -1;//根据图片数量判断一行显示几个图片

    private int type = 0;//查看模式or编辑模式

    private ArrayList<String> list = new ArrayList<>();// 已选图片

    private int colnum = 3;// 每行显示数量，针对CyfRecyclerView有效
    private boolean isSave = false;// 是否有保存按钮，仅限查看时有效

    private boolean isClick = true;// 缩略图是否可以点击，针对编辑添加有效
    private boolean isSingle = false;// 是否单选，针对编辑添加有效
    private boolean isCamera = true;// 是否有相机，针对编辑添加有效
    private int num = 9;// 多选最大张数，针对编辑添加有效
    private boolean isStartWithCamera = false;// 是否直接打开相机，针对编辑添加有效
    private boolean isDelete = true;// 是否带右上角删除按钮，针对编辑添加有效
    private boolean isCanDrag = false;// 是否可以拖拽，针对编辑添加有效
    private boolean isAutoDelThm = true;// 是否自动删除生成的缩略图，针对编辑添加有效
    private boolean isOriginalShow = false;// 判断原始图开关是否显示，针对编辑添加有效

    public PhotoConfigure() {
        super();
    }

    public static int getWatchImg() {
        return WatchImg;
    }

    public static int getEditImg() {
        return EditImg;
    }

    public int getType() {
        return type;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public int getColnum() {
        return colnum;
    }

    public boolean isSave() {
        return isSave;
    }

    public boolean isClick() {
        return isClick;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public int getNum() {
        return num;
    }

    public boolean isStartWithCamera() {
        return isStartWithCamera;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public boolean isCanDrag() {
        return isCanDrag;
    }

    public boolean isAutoDelThm() {
        return isAutoDelThm;
    }

    public boolean isOriginalShow() {
        return isOriginalShow;
    }

    public PhotoConfigure setType(int type) {
        this.type = type;
        return this;
    }

    public PhotoConfigure setList(ArrayList<String> list) {
        this.list = list;
        return this;
    }

    public PhotoConfigure setColnum(int colnum) {
        this.colnum = colnum;
        return this;
    }

    public PhotoConfigure setSave(boolean save) {
        isSave = save;
        return this;
    }

    public PhotoConfigure setClick(boolean click) {
        isClick = click;
        return this;
    }

    public PhotoConfigure setSingle(boolean single) {
        isSingle = single;
        return this;
    }

    public PhotoConfigure setCamera(boolean camera) {
        isCamera = camera;
        return this;
    }

    public PhotoConfigure setNum(int num) {
        this.num = num;
        return this;
    }

    public PhotoConfigure setStartWithCamera(boolean startWithCamera) {
        isStartWithCamera = startWithCamera;
        return this;
    }

    public PhotoConfigure setDelete(boolean delete) {
        isDelete = delete;
        return this;
    }

    public PhotoConfigure setCanDrag(boolean canDrag) {
        isCanDrag = canDrag;
        return this;
    }

    public PhotoConfigure setAutoDelThm(boolean autoDelThm) {
        isAutoDelThm = autoDelThm;
        return this;
    }

    public PhotoConfigure setOriginalShow(boolean originalShow) {
        isOriginalShow = originalShow;
        return this;
    }

    @Override
    public String toString() {
        return "PhotoConfigure{" +
                "type=" + type +
                ", list=" + list +
                ", colnum=" + colnum +
                ", isSave=" + isSave +
                ", isClick=" + isClick +
                ", isSingle=" + isSingle +
                ", isCamera=" + isCamera +
                ", num=" + num +
                ", isStartWithCamera=" + isStartWithCamera +
                ", isDelete=" + isDelete +
                ", isCanDrag=" + isCanDrag +
                ", isAutoDelThm=" + isAutoDelThm +
                ", isOriginalShow=" + isOriginalShow +
                '}';
    }
}