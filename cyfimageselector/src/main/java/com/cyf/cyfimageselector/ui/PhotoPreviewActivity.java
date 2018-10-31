package com.cyf.cyfimageselector.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.cyf.cyfimageselector.GlideApp;
import com.cyf.cyfimageselector.R;
import com.cyf.cyfimageselector.base.PhotoBaseActivity;
import com.cyf.cyfimageselector.pinchimage.PinchImageView;
import com.cyf.cyfimageselector.recycler.CyfRecyclerView;
import com.cyf.cyfimageselector.utils.SDCardImageLoader;
import com.cyf.cyfimageselector.utils.ScreenUtils;
import com.cyf.cyfimageselector.utils.Utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhotoPreviewActivity extends PhotoBaseActivity {

    private static final String intent_index = "index";
    private static final String intent_list = "list";
    private static final String intent_num = "num";
    private static final String intent_list2 = "list2";
    private static final String intent_type = "type";

    public static OnHanlderResultCallback callback;

    private TextView tv_toolsbar_title;
    private TextView tv_toolsbar_left, tv_toolsbar_right;
    private ViewPager viewPage;
    private LinearLayout ll_action;
    private LinkedList<View> gestureImageViewList;
    private CheckBox checkbox;
    private CheckBox checkbox2;
    private List<String> strCheckBoxList;//0未选中，1选中
    private List<String> stringList;
    private List<String> stringList2;

    private ViewPagerAdapter pagerAdapter;

    private int index = 0;//默认选择
    private int type = 0;//0保存，1完成，2删除，3不显示
    private List<String> selectedList;
    private int num = 0;//可选最大数量
    private int httpNum = 0;// 网络图片数量

    private int mIndex = 0;//当前选择
    private String downloadPath = "";//当前下载地址

    // 跳转到该界面
    public static void openPhotoPreview(Activity activity, int index, int num, int type, ArrayList<String> imagePathList, OnHanlderResultCallback callback) {
        PhotoPreviewActivity.callback = callback;
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewActivity.intent_index, index);
        intent.putStringArrayListExtra(PhotoPreviewActivity.intent_list, imagePathList);
        intent.putExtra(PhotoPreviewActivity.intent_num, num);
        intent.putExtra(PhotoPreviewActivity.intent_type, type);
        activity.startActivityForResult(intent, 0);
    }

    // 跳转到该界面2
    public static void openPhotoPreview(Activity activity, int index, int num, int type, ArrayList<String> imagePathList, ArrayList<String> selectImagePathList, OnHanlderResultCallback callback) {
        PhotoPreviewActivity.callback = callback;
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewActivity.intent_index, index);
        intent.putStringArrayListExtra(PhotoPreviewActivity.intent_list2, selectImagePathList);
        intent.putStringArrayListExtra(PhotoPreviewActivity.intent_list, imagePathList);
        intent.putExtra(PhotoPreviewActivity.intent_num, num);
        intent.putExtra(PhotoPreviewActivity.intent_type, type);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_preview);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        selectedList = new ArrayList<>();
        stringList = getIntent().getStringArrayListExtra(intent_list);
        selectedList.addAll(stringList);
        index = getIntent().getIntExtra(intent_index, 0);
        type = getIntent().getIntExtra(intent_type, 0);
        if (type == 1) {
            num = getIntent().getIntExtra(intent_num, 0);
            if (getIntent().hasExtra(PhotoPreviewActivity.intent_list2)) {
                stringList2 = getIntent().getStringArrayListExtra(intent_list2);
                selectedList.clear();
                selectedList.addAll(stringList2);
                for (int i = 0; i < selectedList.size(); i++) {
                    if (selectedList.get(i).startsWith("http")) {
                        httpNum++;
                    }
                }
            }
        }
        initView();
    }

    private void initView() {
        if (stringList != null) {
            ll_action = (LinearLayout) findViewById(R.id.ll_action);
            ll_action.setVisibility(View.GONE);
            checkbox = findViewById(R.id.checkbox);
            checkbox2 = findViewById(R.id.checkbox2);
            tv_toolsbar_title = (TextView) findViewById(R.id.tv_toolsbar_title);
            tv_toolsbar_left = (TextView) findViewById(R.id.tv_toolsbar_left);
            tv_toolsbar_left.setVisibility(View.VISIBLE);
            tv_toolsbar_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (type == 2) {
                        // 删除功能
                        callback.onHanlderSuccess(stringList);
                    }
                    finish();
                }
            });
            tv_toolsbar_right = (TextView) findViewById(R.id.tv_toolsbar_right);
            if (type == 0) {
                tv_toolsbar_right.setVisibility(View.VISIBLE);
                tv_toolsbar_right.setText("保存");
                tv_toolsbar_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveFile(stringList.get(mIndex));
                    }
                });
            } else if (type == 1) {
                ll_action.setVisibility(View.VISIBLE);
                checkbox.setVisibility(View.VISIBLE);
                tv_toolsbar_right.setVisibility(View.VISIBLE);
                if (CyfRecyclerView.isOriginalShow && CyfRecyclerView.onCyfThumbnailsListener != null) {
                    checkbox2.setVisibility(View.VISIBLE);
                    checkbox2.setChecked(CyfRecyclerView.isOriginalDrawing);
                    checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            CyfRecyclerView.isOriginalDrawing = b;
                        }
                    });
                } else {
                    checkbox2.setVisibility(View.GONE);
                }
                if (stringList2 != null) {
                    if (stringList2.size() > 0) {
                        tv_toolsbar_right.setText("完成(" + stringList2.size() + "/" + num + ")");
                        tv_toolsbar_right.setClickable(true);
                        tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn);
                        tv_toolsbar_right.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    } else {
                        tv_toolsbar_right.setText("完成");
                        tv_toolsbar_right.setClickable(false);
                        tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn2);
                        tv_toolsbar_right.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                    }
                } else {
                    if (stringList.size() > 0) {
                        tv_toolsbar_right.setText("完成(" + stringList.size() + "/" + num + ")");
                        tv_toolsbar_right.setClickable(true);
                        tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn);
                        tv_toolsbar_right.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    } else {
                        tv_toolsbar_right.setText("完成");
                        tv_toolsbar_right.setClickable(false);
                        tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn2);
                        tv_toolsbar_right.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                    }
                }
                tv_toolsbar_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.onHanlderSuccess(selectedList);
                        PhotoPreviewActivity.this.finish();
                    }
                });
            } else if (type == 2) {
                tv_toolsbar_right.setVisibility(View.VISIBLE);
                tv_toolsbar_right.setText("删除");
                tv_toolsbar_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (stringList.size() > 1) {
                            // 移除数据
                            stringList.remove(mIndex);
                            pagerAdapter.notifyDataSetChanged();
                            // 修改标题
                            tv_toolsbar_title.setText((mIndex + 1) + "/" + stringList.size());
                        } else if (stringList.size() == 1) {
//                            CustomDialog.Builder builder = new CustomDialog.Builder(PhotoPreviewActivity.this);
//                            builder.setCancel(true);
//                            builder.setMessage("确认移除当前图片吗?");
//                            builder.setTitle("提示");
//                            builder.setPositiveButton("取消",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    }, R.drawable.background_btn);
//                            builder.setNegativeButton("确定",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                            stringList.clear();
//                                            callback.onHanlderSuccess(stringList);
//                                            finish();
//                                        }
//                                    }, R.drawable.background_btn);
//                            builder.create().show();
                            stringList.clear();
                            callback.onHanlderSuccess(stringList);
                            finish();
                        }
                        Toast.makeText(PhotoPreviewActivity.this, "图片已删除!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                tv_toolsbar_right.setVisibility(View.GONE);
            }
            viewPage = findViewById(R.id.viewPager);
            gestureImageViewList = new LinkedList<>();
            strCheckBoxList = new ArrayList<>();
            tv_toolsbar_title.setText("-/" + stringList.size());
            // 防止页面等待时间过长
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stringList2 != null) {
                        for (int i = 0; i < stringList.size(); i++) {
                            strCheckBoxList.add("0");
                        }
                        for (int i = 0; i < stringList.size(); i++) {
                            if (stringList2 != null && stringList2.size() > 0) {
                                for (int j = 0; j < stringList2.size(); j++) {
                                    if (stringList.get(i).equals(stringList2.get(j))) {
                                        // checkBoxList.get(i).setChecked(true);
                                        strCheckBoxList.set(i, "1");
                                        if (i == index) {
                                            setCheckBoxData(checkbox, true);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < stringList.size(); i++) {
                            strCheckBoxList.add("1");
                        }
                    }
                    pagerAdapter = new ViewPagerAdapter();
                    viewPage.setAdapter(pagerAdapter);
                    viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            mIndex = position;
                            tv_toolsbar_title.setText((position + 1) + "/" + stringList.size());
                            if (type == 0) {
                                // 保存
                                isSaved(position);
                            } else if (type == 1) {
                                // 完成
                                setCheckBoxData(checkbox, strCheckBoxList.get(position).equals("1"));
                                setPhotoBottom();
                            }
                            // 恢复图片大小
                            PinchImageView piv = viewPage.findViewById(position);
                            if (piv != null) {
                                piv.reset();
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    if (index == 0) {
                        if (type == 0) {
                            // 保存
                            isSaved(0);
                        }
                        setCheckBoxData(checkbox, strCheckBoxList.get(0).equals("1"));
                    } else {
                        viewPage.setCurrentItem(index);
                    }
                    tv_toolsbar_title.setText((index + 1) + "/" + stringList.size());
                    setPhotoBottom();
                }
            }, 0);
        }
    }

    // 判断是否保存过
    private void isSaved(int position) {
        String[] names = stringList.get(position).split("/");
        String name = names[names.length - 1];
        File myCaptureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName, name);
        if (myCaptureFile.exists()) {
            tv_toolsbar_right.setTextColor(ContextCompat.getColor(PhotoPreviewActivity.this, android.R.color.darker_gray));
            tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn2);
            tv_toolsbar_right.setText("已保存");
            tv_toolsbar_right.setClickable(false);
        } else {
            tv_toolsbar_right.setTextColor(ContextCompat.getColor(PhotoPreviewActivity.this, android.R.color.white));
            tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn);
            tv_toolsbar_right.setText("保存");
            tv_toolsbar_right.setClickable(true);
        }
    }

    // 保存图片到本地
    private void saveFile(final String filePath) {
        downloadPath = filePath;
        showProgressDialog("正在努力保存中...");
        GlideApp.with(PhotoPreviewActivity.this).load(filePath).
                into(new CustomImageViewTarget(new ImageView(this), 5000, 5000));
    }

    private void saveBitmap(final String filePath, final Bitmap resource) {
        BufferedOutputStream bos = null;
        try {
            String[] names = filePath.split("/");
            String name = names[names.length - 1];
            File foder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName);
            if (!foder.exists()) {
                foder.mkdirs();
            }
            File myCaptureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName, name);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            resource.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            com.cyf.cyfimageselector.utils.Utility.showToast(PhotoPreviewActivity.this, "图片成功保存至" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName + "目录");
            Uri mUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + filePathName + "/" + name));
            // 刷新在系统相册中显示
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(mUri);
            sendBroadcast(intent);
        } catch (Exception e) {

        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isSaved(mIndex);
            disnissProgressDialog();
        }
    }

    // 返回时把值返回上一界面
    private void dataFinish() {
        if (type == 1) {
            PhotoWallActivity2.setSelectedData(selectedList);
        }
    }

    // 不触动checkbox监听的情况下赋值
    private void setCheckBoxData(final CheckBox cb, boolean flag) {
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        cb.setChecked(flag);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    strCheckBoxList.set(mIndex, "1");
                } else {
                    strCheckBoxList.set(mIndex, "0");
                }
                // 修改tv_toolsbar_right的值
                int mNum = 0;
                for (int i = 0; i < strCheckBoxList.size(); i++) {
                    if (strCheckBoxList.get(i).equals("1")) {
                        mNum++;
                    }
                }
                // 判断图片是否超过限定数量
                if ((httpNum + mNum) > num) {
                    setCheckBoxData(cb, false);
                    strCheckBoxList.set(mIndex, "0");
                    Utility.showToast(PhotoPreviewActivity.this, "您已选择超过" + num + "张图片");
                } else {
                    // 添加或者移除图片
                    if (b) {
                        selectedList.add(stringList.get(mIndex));
                    } else {
                        for (int i = 0; i < selectedList.size(); i++) {
                            if (stringList.get(mIndex).equals(selectedList.get(i))) {
                                selectedList.remove(i);
                                break;
                            }
                        }
                    }
                    // 修改按钮文字显示
                    if ((httpNum + mNum) > 0) {
                        tv_toolsbar_right.setText("完成(" + (httpNum + mNum) + "/" + num + ")");
                        tv_toolsbar_right.setClickable(true);
                        tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn);
                        tv_toolsbar_right.setTextColor(ContextCompat.getColor(PhotoPreviewActivity.this, android.R.color.white));
                    } else {
                        tv_toolsbar_right.setText("完成");
                        tv_toolsbar_right.setClickable(false);
                        tv_toolsbar_right.setBackgroundResource(R.drawable.shape_btn2);
                        tv_toolsbar_right.setTextColor(ContextCompat.getColor(PhotoPreviewActivity.this, android.R.color.darker_gray));
                    }
                    // 更新下方图片显示
                    setPhotoBottom();
                }
            }
        });
    }

    /**
     * 为下面界面赋值
     */
    private void setPhotoBottom() {
        if (type == 1) {
            findViewById(R.id.ll_selected).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.ll_selected2)).removeAllViews();
            if (selectedList.size() > 0) {
                findViewById(R.id.ll_selected).setVisibility(View.VISIBLE);
                for (int i = 0; i < selectedList.size(); i++) {
                    final int a = i;
                    View view = getLayoutInflater().inflate(R.layout.photo_preview_bottom, null);
                    final ImageView iv = view.findViewById(R.id.photo_item);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 滑动到指定位置
                            for (int i = 0; i < stringList.size(); i++) {
                                if (stringList.get(i).equals(selectedList.get(a))) {
                                    viewPage.setCurrentItem(i, true);
                                    break;
                                }
                            }
                        }
                    });
                    String filePath = selectedList.get(i);
                    SDCardImageLoader.setImgThumbnail(this, filePath, iv);
                    if (stringList.get(mIndex).equals(selectedList.get(i))) {
                        findViewById(R.id.horizontalScrollView).post(new Runnable() {
                            @Override
                            public void run() {
                                ((HorizontalScrollView) findViewById(R.id.horizontalScrollView)).
                                        smoothScrollTo(a * ScreenUtils.dp2px(40), 0);
                            }
                        });
                        view.findViewById(R.id.item_selected).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.item_selected).setVisibility(View.GONE);
                    }
                    ((LinearLayout) findViewById(R.id.ll_selected2)).addView(view);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (type == 2) {
            // 删除功能
            callback.onHanlderSuccess(stringList);
        }
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        callback = null;
        dataFinish();
        super.onDestroy();
    }

    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return stringList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final PinchImageView piv;
            if (gestureImageViewList.size() > 0) {
                piv = (PinchImageView) gestureImageViewList.remove();
                piv.reset();
            } else {
                piv = new PinchImageView(PhotoPreviewActivity.this);
            }
            piv.setId(position);
            if (type == 0) {
                piv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        // 保存，发送给好友，下方Dialog弹窗
                        Log.e("cyf", "图片" + position + "被长按了！！！");
                        return false;
                    }
                });
            }
            piv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // view.playSoundEffect(SoundEffectConstants.CLICK);
                    if (type == 2) {
                        // 删除功能
                        callback.onHanlderSuccess(stringList);
                    }
                    finish();
                }
            });
            SDCardImageLoader.setImg(PhotoPreviewActivity.this, stringList.get(position), piv);
            container.addView(piv);
            return piv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            PinchImageView piv = (PinchImageView) object;
            container.removeView(piv);
            gestureImageViewList.add(piv);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            PinchImageView piv = (PinchImageView) object;
            SDCardImageLoader.setImg(PhotoPreviewActivity.this, stringList.get(position), piv);
            // viewPage.setMainPinchImageView(piv);
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

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
        void onHanlderSuccess(List<String> resultList);

    }

    public class CustomImageViewTarget<T> extends ImageViewTarget<BitmapDrawable> {

        private int width, height;

        @Override
        protected void setResource(@Nullable BitmapDrawable resource) {
            if (resource == null) {
                // Toast.makeText(PhotoPreviewActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                disnissProgressDialog();
            } else {
                saveBitmap(downloadPath, drawableToBitmap(resource));
            }
        }

        public CustomImageViewTarget(ImageView view, int width, int height) {
            super(view);
            this.width = width;
            this.height = height;
        }

        @Override
        public void getSize(SizeReadyCallback cb) {
            if (width > 0 && height > 0) {
                cb.onSizeReady(width, height);
                return;
            }
            super.getSize(cb);
        }
    }

    public static Bitmap drawableToBitmap(BitmapDrawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
