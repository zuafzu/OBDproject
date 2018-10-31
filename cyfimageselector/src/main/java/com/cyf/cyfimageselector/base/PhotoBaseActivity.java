package com.cyf.cyfimageselector.base;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;

/**
 * Created by caoyingfu on 2017/8/3.
 */

public class PhotoBaseActivity extends FragmentActivity {

    public static String filePathName = "Camera";

    protected ProgressDialog progressDialog;

    /**
     * 弹出dialog(无参)
     */
    public void showProgressDialog() {
        showProgressDialog("正在努力加载中...");
    }

    /**
     * 弹出dialog(有参)
     */
    public void showProgressDialog(String string) {
        if (this != null && progressDialog == null) {
            progressDialog = new ProgressDialog(this);
//            TextView tv = new TextView(this);
//            tv.setText(string);
//            progressDialog.setContentView(tv);
            progressDialog.setMessage(string);
            progressDialog.setCancelable(false);
        }
        if (this != null && progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 取消dialog
     */
    public void disnissProgressDialog() {
        if (this != null && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    protected int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
