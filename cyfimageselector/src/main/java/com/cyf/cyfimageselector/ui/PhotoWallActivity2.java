package com.cyf.cyfimageselector.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cyf.cyfimageselector.R;
import com.cyf.cyfimageselector.adapter.PhotoAlbumLVAdapter;
import com.cyf.cyfimageselector.base.PhotoBaseActivity;
import com.cyf.cyfimageselector.model.PhotoAlbumLVItem;
import com.cyf.cyfimageselector.model.PhotoConfigure;
import com.cyf.cyfimageselector.recycler.CyfRecyclerView;
import com.cyf.cyfimageselector.utils.PermissionsChecker;
import com.cyf.cyfimageselector.utils.SDCardImageLoader;
import com.cyf.cyfimageselector.utils.ScreenUtils;
import com.cyf.cyfimageselector.utils.Utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 选择照片页面
 * Created by cyf
 */
public class PhotoWallActivity2 extends PhotoBaseActivity {

    public final static String photoNameShow = "全部照片";// 默认名字
    public final static int photoNumShow = 100000;// 默认最多数量

    public static PhotoWallActivity2 activity = null;

    private ListView listView;
    private CheckBox checkbox;
    private Button btn_look;
    private LinearLayout ll_file;
    private TextView tv_file;
    private TextView btn_back, btn_sure, tv_toolsbar_title;
    private ArrayList<String> list;
    private ArrayList<String> list_selected = new ArrayList<>();
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;
    private PhotoConfigure photoConfigure;

    private PhotoAlbumLVAdapter photoAlbumLVAdapter;

    public static OnHanlderResultCallback callback;
    /**
     * 当前展示的是否为PhotoAlbum
     */
    private boolean isLatest = true;
    /**
     * 当前文件夹路径
     */
    private String currentFolder = null;

    private Uri mUri;

    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    // 跳转到该界面
    public static void openImageSelecter(final Activity activity, PhotoConfigure photoConfigure, OnHanlderResultCallback callback) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            PermissionsChecker mPermissionsChecker = new PermissionsChecker(activity);
            // 缺少权限时, 进入权限配置页面
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_CODE);
                return;
            }
        }
        PhotoWallActivity2.callback = callback;
        Intent intent = new Intent(activity, PhotoWallActivity2.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("photoConfigure", photoConfigure);
        intent.putExtras(mBundle);
        activity.startActivity(intent);
    }

    public static void setSelectedData(List<String> list) {
        if (PhotoWallActivity2.activity != null) {
            PhotoWallActivity2.activity.list_selected.clear();
            PhotoWallActivity2.activity.list_selected.addAll(list);
            if (PhotoWallActivity2.activity.list_selected.size() > 0) {
                PhotoWallActivity2.activity.btn_sure.
                        setText("完成(" +
                                PhotoWallActivity2.activity.list_selected.size() +
                                "/" +
                                PhotoWallActivity2.activity.photoConfigure.getNum() +
                                ")");
            }
            PhotoWallActivity2.activity.updata_btn_preview();
            if (PhotoWallActivity2.activity.adapter != null) {
                PhotoWallActivity2.activity.adapter.notifyDataSetChanged();
            }
        }
    }

    // 用户权限申请的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_wall2);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        ScreenUtils.initScreen(this);
        if (!com.cyf.cyfimageselector.utils.Utility.isSDcardOK()) {
            com.cyf.cyfimageselector.utils.Utility.showToast(this, "SD卡不可用");
            return;
        }
        activity = this;
        initIntent();
        initClick();
        initGridView();
        initAlbum();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (btn_sure != null) {
            if (list_selected.size() > 0) {
                btn_sure.setText("完成(" + list_selected.size() + "/" + photoConfigure.getNum() + ")");
            } else {
                btn_sure.setText("完成");
            }
        }
        if (CyfRecyclerView.isOriginalShow && CyfRecyclerView.onCyfThumbnailsListener != null) {
            checkbox.setVisibility(View.VISIBLE);
            checkbox.setChecked(CyfRecyclerView.isOriginalDrawing);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    CyfRecyclerView.isOriginalDrawing = b;
                }
            });
        } else {
            checkbox.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (photoConfigure.isStartWithCamera()) {
            finish();
        }
    }

    private void initIntent() {
        photoConfigure = (PhotoConfigure) getIntent().getSerializableExtra("photoConfigure");
        // 防止异常崩溃
        if (photoConfigure.getList() == null) {
            photoConfigure.setList(new ArrayList<String>());
        }
        if (photoConfigure.isSingle()) {
            // 单选
            photoConfigure.setNum(1);
        }
    }

    private void initClick() {
        tv_toolsbar_title = (TextView) findViewById(R.id.tv_toolsbar_title);
        tv_toolsbar_title.setText("选择图片");
        // 结束当前界面
        btn_back = (TextView) findViewById(R.id.tv_toolsbar_left);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.GONE) {
                    if (callback != null) {
                        callback.onHanlderSuccess(getSelectImagePaths());
                        PhotoWallActivity2.this.finish();
                    }
                } else {
                    listView.startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, R.anim.weight__close));
                    listView.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.ll_list).setVisibility(View.GONE);
                        }
                    }, 500);
                }
            }
        });
        findViewById(R.id.ll_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, R.anim.weight__close));
                listView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.ll_list).setVisibility(View.GONE);
                    }
                }, 500);
            }
        });
        // 点击回到选择相册页面
        tv_file = findViewById(R.id.tv_file);
        tv_file.setText(photoNameShow);
        ll_file = findViewById(R.id.ll_file);
        ll_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backAction();
            }
        });
        // 选择照片完成
        btn_sure = (TextView) findViewById(R.id.tv_toolsbar_right);
        if (photoConfigure.isSingle()) {
            btn_sure.setVisibility(View.GONE);
        } else {
            btn_sure.setVisibility(View.VISIBLE);
        }
        if (photoConfigure.getList().size() > 0) {
            btn_sure.setText("完成(" + photoConfigure.getList().size() + "/" + photoConfigure.getNum() + ")");
        } else {
            btn_sure.setText("完成");
        }
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择图片完成,回到起始页面
                if (callback != null) {
                    callback.onHanlderSuccess(getSelectImagePaths());
                    PhotoWallActivity2.this.finish();
                }
            }
        });
        // 预览
        btn_look = (Button) findViewById(R.id.btn_look);
        btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_selected = getSelectImagePaths();
                if (list_selected.size() > 0) {
                    PhotoPreviewActivity.openPhotoPreview(PhotoWallActivity2.this, 0, photoConfigure.getNum(), 1, list_selected, new PhotoPreviewActivity.OnHanlderResultCallback() {
                        @Override
                        public void onHanlderSuccess(List<String> resultList) {
                            if (callback != null) {
                                callback.onHanlderSuccess(resultList);
                                PhotoWallActivity2.this.finish();
                            }
                        }
                    });
                } else {
                    Utility.showToast(PhotoWallActivity2.this, "请先选择图片");
                }
            }
        });
        checkbox = findViewById(R.id.checkbox);
    }

    private void initGridView() {
        mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
        if (photoConfigure.isSingle()) {
            // 单选点击事件，多选点击事件在adapter里
            mPhotoWall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (list.get(i).equals("camera")) {
                        // 拍照
                        doTakePhoto();
                    } else {
                        // 相册
                        ArrayList<String> mList = new ArrayList<String>();
                        mList.clear();
                        mList.add(list.get(i));
                        if (callback != null) {
                            callback.onHanlderSuccess(mList);
                            PhotoWallActivity2.this.finish();
                        }
                    }
                }
            });
        }
        list = getLatestImagePaths(photoNumShow);
        if (photoConfigure.isCamera() && ((list != null && list.size() > 0 && !list.get(0).equals("camera")) || (list.size() == 0))) {
            list.add(0, "camera");
        }
        list_selected.clear();
        list_selected.addAll(photoConfigure.getList());
        updata_btn_preview();
        adapter = new PhotoWallAdapter(this, list, list_selected, btn_sure);
        mPhotoWall.setAdapter(adapter);
        if (photoConfigure.isStartWithCamera()) {
            doTakePhoto();
        }
    }

    private void initAlbum() {
        listView = (ListView) findViewById(R.id.select_img_listView);

        //第二种方式：使用ContentProvider。（效率更高）
        final ArrayList<PhotoAlbumLVItem> mmlist = new ArrayList<PhotoAlbumLVItem>();
        //“photoNameShow”
        if (photoConfigure.isCamera()) {
            mmlist.add(new PhotoAlbumLVItem(photoNameShow,
                    list.size() - 1, list.get(1).toString()));
        } else {
            mmlist.add(new PhotoAlbumLVItem(photoNameShow,
                    list.size(), list.get(0).toString()));
        }
        // 相册
        mmlist.addAll(getImagePathsByContentProvider());

        photoAlbumLVAdapter = new PhotoAlbumLVAdapter(this, mmlist);
        listView.setAdapter(photoAlbumLVAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, R.anim.weight__close));
                listView.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.ll_list).setVisibility(View.GONE);
                    }
                }, 500);
                //第一行为photoNameShow
                if (position == 0) {
                    if (!isLatest) {
                        updateView(200, null);
                        isLatest = true;
                    }
                } else {
                    //某个相册
                    String folderPath = mmlist.get(position).getPathName();
                    if (isLatest || (folderPath != null && !folderPath.equals(currentFolder))) {
                        currentFolder = folderPath;
                        updateView(100, currentFolder);
                        isLatest = false;
                    }
                }
                photoAlbumLVAdapter.setFilesIndex(position);
                photoAlbumLVAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 点击跳转至相册页面
     */
    private void backAction() {
        if (listView.getVisibility() == View.GONE) {
            findViewById(R.id.ll_list).startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, android.R.anim.fade_in));
            findViewById(R.id.ll_list).setVisibility(View.VISIBLE);
            listView.startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, R.anim.weight__open));
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, R.anim.weight__close));
            listView.setVisibility(View.GONE);
            findViewById(R.id.ll_list).startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, android.R.anim.fade_out));
            findViewById(R.id.ll_list).setVisibility(View.GONE);
        }
    }

    /**
     * 拍照获取相片
     **/
    public void doTakePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File mTmpFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName,
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        mUri = Uri.fromFile(mTmpFile);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
            startActivityForResult(cameraIntent, 0);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, 0);
        }
    }

    /**
     * 根据图片所属文件夹路径，刷新页面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        // adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();

        if (code == 100) {   //某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            tv_file.setText(folderName);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) {  //photoNameShow
            tv_file.setText(photoNameShow);
            list.addAll(getLatestImagePaths(photoNumShow));
        }
        if (photoConfigure.isCamera() && ((list != null && list.size() > 0 && !list.get(0).equals("camera")) || (list.size() == 0))) {
            list.add(0, "camera");
        }
        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            //滚动至顶部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }

    /**
     * 判断预览按钮是否可以点击
     */
    private void updata_btn_preview() {
        if (list_selected != null && list_selected.size() > 0) {
            btn_sure.setClickable(true);
            btn_sure.setBackgroundResource(R.drawable.shape_btn);
            btn_sure.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btn_look.setClickable(true);
            btn_look.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btn_look.setText("预览(" + list_selected.size() + ")");
        } else {
            btn_sure.setText("完成");
            btn_sure.setClickable(false);
            btn_sure.setBackgroundResource(R.drawable.shape_btn2);
            btn_sure.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            btn_look.setClickable(false);
            btn_look.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            btn_look.setText("预览");
        }
    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath) {
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return null;
        }

        ArrayList<String> imageFilePaths = new ArrayList<String>();
        for (int i = allFileNames.length - 1; i >= 0; i--) {
            if (Utility.isImage(allFileNames[i])) {
                imageFilePaths.add(folderPath + File.separator + allFileNames[i]);
            }
        }

        return imageFilePaths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + getPackageName());
        String thmPath = appDir.getAbsolutePath();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        ContentResolver mContentResolver = getContentResolver();
        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            //从最新的图片开始读取.
            //当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
                    String path = cursor.getString(0);
                    if (checkImgPath(path)) {
                        // 不扫描压缩路径
                        if (!path.contains(thmPath)) {
                            latestImagePaths.add(path);
                        }
                    }
                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }
        return latestImagePaths;
    }

    // 获取已选择的图片路径
    private ArrayList<String> getSelectImagePaths() {
        list_selected = adapter.getSelectionMap();
        return list_selected;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                // 拍照返回照片
                ArrayList<String> mList = new ArrayList<String>();
                mList.clear();
                list_selected = getSelectImagePaths();
                if (list_selected != null && list_selected.size() > 0) {
                    mList.addAll(list_selected);
                }
                updata_btn_preview();
                mList.add(mUri.getPath());
                // ---------特俗处理三星手机---------
                if ("samsung".equals(SDCardImageLoader.getDeviceBrand())) {
                    if (SDCardImageLoader.readPictureDegree(mUri.getPath()) != 0) {
                        Bitmap bitmap = SDCardImageLoader.toturn(BitmapFactory.decodeFile(mUri.getPath()),
                                SDCardImageLoader.readPictureDegree(mUri.getPath()));
                        saveBitmapFile(bitmap, mUri.getPath());
                        // 防止内存溢出
                        bitmap.recycle();
                        bitmap = null;
                        System.gc();
                    }
                }
                // --------------------------------
                if (callback != null) {
                    callback.onHanlderSuccess(mList);
                    PhotoWallActivity2.this.finish();
                }
                try {
                    // 刷新在系统相册中显示
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(mUri);
                    sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (listView.getVisibility() == View.GONE) {
            if (callback != null) {
                callback.onHanlderSuccess(getSelectImagePaths());
                PhotoWallActivity2.this.finish();
            }
        } else {
            listView.startAnimation(AnimationUtils.loadAnimation(PhotoWallActivity2.this, R.anim.weight__close));
            listView.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.ll_list).setVisibility(View.GONE);
                }
            }, 500);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        PhotoWallActivity2.callback = null;
        activity = null;
        super.onDestroy();
    }

    /**
     * 获取目录中图片的个数。
     */
    private int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            if (com.cyf.cyfimageselector.utils.Utility.isImage(file.getName())) {
                count++;
            }
        }

        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                if (com.cyf.cyfimageselector.utils.Utility.isImage(file.getName())) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        File appDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + getPackageName());
        String thmPath = appDir.getAbsolutePath();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<PhotoAlbumLVItem> list = null;
        if (cursor != null) {
            if (cursor.moveToLast()) {
                //路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<String>();
                list = new ArrayList<PhotoAlbumLVItem>();

                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);

                    File parentFile = new File(imagePath).getParentFile();
                    String parentPath = parentFile.getAbsolutePath();

                    // 不扫描重复路径
                    if (!cachePath.contains(parentPath)) {
                        if (checkImgPath(getFirstImagePath(parentFile))) {
                            // 不扫描压缩路径
                            if (!thmPath.equals(parentPath)) {
                                list.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile),
                                        getFirstImagePath(parentFile)));
                            }
                        }
                        cachePath.add(parentPath);
                    }

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }

            cursor.close();
        }

        return list;
    }

    // 判断图片路径是否是标准路径
    private boolean checkImgPath(String path) {
        if (path != null && !"".equals(path) && new File(path).exists()) {
            if (path.startsWith("/storage/emulated/0") || path.startsWith("/storage/sdcard0") || path.startsWith("file:///mnt/sdcard")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理结果
     */
    public interface OnHanlderResultCallback {
        /**
         * 处理成功
         *
         * @param resultList
         */
        public void onHanlderSuccess(List<String> resultList);

    }

    /**
     * PhotoWall中GridView的适配器
     *
     * @author cyf
     */
    public class PhotoWallAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<String> imagePathList = null;
        private ArrayList<String> imagePathListSelected = null;
        private TextView btn_sure;

        public PhotoWallAdapter(Context context, ArrayList<String> imagePathList, ArrayList<String> imagePathListSelected, TextView btn_sure) {
            this.context = context;
            this.imagePathList = imagePathList;
            this.btn_sure = btn_sure;
            this.imagePathListSelected = imagePathListSelected;
            if (this.imagePathListSelected == null) {
                this.imagePathListSelected = new ArrayList<>();
            }
        }

        @Override
        public int getCount() {
            return imagePathList == null ? 0 : imagePathList.size();
        }

        @Override
        public Object getItem(int position) {
            return imagePathList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int mPosition = position;
            String filePath = (String) getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.photo_wall_item, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.photo_wall_item_photo);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.photo_wall_item_cb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //tag的key必须使用id的方式定义以保证唯一，否则会出现IllegalArgumentException.
            holder.checkBox.setTag(R.string.app_name, holder.imageView);
            boolean flag_selected = false;
            for (int j = 0; j < imagePathListSelected.size(); j++) {
                if (imagePathList.get(position).equals(imagePathListSelected.get(j))) {
                    flag_selected = true;
                }
            }
            ImageView image = (ImageView) holder.checkBox.getTag(R.string.app_name);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
            holder.checkBox.setChecked(flag_selected);
            if (flag_selected) {
                image.setColorFilter(Color.parseColor("#66000000"));
            } else {
                image.setColorFilter(null);
            }
            if (filePath.equals("camera")) {
                holder.imageView.setImageResource(R.mipmap.ic_camera);
            } else {
                SDCardImageLoader.setImgThumbnail(context, list.get(position), holder.imageView);
            }
            if (imagePathList.get(mPosition).equals("camera")) {
                // 拍照
                holder.checkBox.setVisibility(View.GONE);
            } else {
                // 相册
                holder.checkBox.setVisibility(View.VISIBLE);
            }
            if (photoConfigure.isSingle()) {
                // 单选
                holder.checkBox.setVisibility(View.GONE);
                image.setColorFilter(null);
            } else {
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ImageView image = (ImageView) buttonView.getTag(R.string.app_name);
                        if (isChecked) {
                            imagePathListSelected.add(imagePathList.get(mPosition));
                            image.setColorFilter(Color.parseColor("#66000000"));
                            if (imagePathListSelected.size() > photoConfigure.getNum()) {
                                imagePathListSelected.remove(imagePathList.get(mPosition));
                                image.setColorFilter(null);
                                buttonView.setChecked(false);
                                Utility.showToast(context, "您已选择超过" + photoConfigure.getNum() + "张图片");
                            }
                        } else {
                            imagePathListSelected.remove(imagePathList.get(mPosition));
                            image.setColorFilter(null);
                        }
                        if (imagePathListSelected.size() > 0) {
                            btn_sure.setText("完成(" + imagePathListSelected.size() + "/" + photoConfigure.getNum() + ")");
                        }
                        updata_btn_preview();
                    }
                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (imagePathList.get(mPosition).equals("camera")) {
                            // 拍照
                            doTakePhoto();
                        } else {
                            // 预览
                            ArrayList<String> mList = new ArrayList<>();
                            // 全部图片
                            int index = mPosition;
                            mList.addAll(imagePathList);
                            if (photoConfigure.isCamera()) {
                                mList.remove(0);
                                index--;
                            }
                            PhotoPreviewActivity.openPhotoPreview(PhotoWallActivity2.this, index, photoConfigure.getNum(), 1, mList, imagePathListSelected, new PhotoPreviewActivity.OnHanlderResultCallback() {
                                @Override
                                public void onHanlderSuccess(List<String> resultList) {
                                    if (callback != null) {
                                        callback.onHanlderSuccess(resultList);
                                        PhotoWallActivity2.this.finish();
                                    }
                                }
                            });
                        }
                    }
                });
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            CheckBox checkBox;
        }

        public ArrayList<String> getSelectionMap() {
            return imagePathListSelected;
        }

        public void clearSelectionMap() {
            imagePathListSelected.clear();
        }
    }

    public void saveBitmapFile(Bitmap bitmap, String path) {
        File file = new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
