package com.cy.obdproject.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.cy.obdproject.R;
import com.cy.obdproject.activity.LoginActivity;
import com.cy.obdproject.activity.MainActivity;
import com.cy.obdproject.activity.SelectCarTypeActivity;
import com.cy.obdproject.activity.WriteData2Activity;
import com.cy.obdproject.app.MyApp;
import com.cy.obdproject.bean.BaseBean;
import com.cy.obdproject.bean.WebSocketBean;
import com.cy.obdproject.constant.Constant;
import com.cy.obdproject.socket.WebSocketService;
import com.cy.obdproject.tools.NetTools;
import com.cy.obdproject.tools.SPTools;
import com.cy.obdproject.tools.StrZipUtil;
import com.cy.obdproject.url.Urls;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class BaseActivity extends AppCompatActivity {

    private ClickMethoListener clickMethoListener;
    private ProgressDialog progressDialog;
    private AlertDialog mDialog;
    public MyApp myApp;
    public boolean isUserConnected = false;// 是否用户远程协助中
    public boolean isProfessionalConnected = false;// 是否专家远程协助中

    public boolean isshowDissWait = true;

    public long delayMillis = 10 * 1000;// 专家端请求数据超时连接

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myApp = (MyApp) getApplication();
        isUserConnected = WebSocketService.Companion.getIntance() != null &&
                WebSocketService.Companion.getIntance().isConnected() &&
                (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userNormal;
        isProfessionalConnected = WebSocketService.Companion.getIntance() != null &&
                WebSocketService.Companion.getIntance().isConnected() &&
                (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userProfessional;
        myApp.getActivityList().add(this);
        if (this instanceof ClickMethoListener) {
            clickMethoListener = (ClickMethoListener) this;
        }
        // 开始屏蔽电话
        myApp.stopCallPhone();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isUserConnected = WebSocketService.Companion.getIntance() != null &&
                WebSocketService.Companion.getIntance().isConnected() &&
                (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userNormal;
        isProfessionalConnected = WebSocketService.Companion.getIntance() != null &&
                WebSocketService.Companion.getIntance().isConnected() &&
                (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userProfessional;
        if (isUserConnected) {
            if (findViewById(R.id.float_window) != null) {
                findViewById(R.id.float_window).setVisibility(View.VISIBLE);
                findViewById(R.id.float_window).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        } else {
            if (findViewById(R.id.float_window) != null) {
                findViewById(R.id.float_window).setVisibility(View.GONE);
            }
        }
    }

    public void showDissWait() {
        if (isshowDissWait) {
            showProgressDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                }
            }, delayMillis);
        }
    }

    public void showProgressDialog() {
        showProgressDialog("正在加载中，请稍等......");
    }

    public void showProgressDialog(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(BaseActivity.this);
                    progressDialog.setTitle("");//2.设置标题
                    progressDialog.setMessage(string);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } else if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
        });
    }

    public void dismissProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void showWebSocketStopDialog(String msg) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.setMessage(msg);
        } else {
            mDialog = new AlertDialog.Builder(this).
                    setTitle("提示").
                    setMessage(msg).
                    setCancelable(false).
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDialog.dismiss();
                            if (Constant.userProfessional ==
                                    (int) SPTools.INSTANCE.get(BaseActivity.this, Constant.USERTYPE, Constant.userProfessional)) {
                                // 专家
                                if (Objects.requireNonNull(WebSocketService.Companion.getIntance()).isConnected()) {
                                    // 返回专家主界面（用户列表界面）
                                    for (int j = 0; j < myApp.getActivityList().size(); j++) {
                                        if (!myApp.getActivityList().get(j).getLocalClassName().contains("RequestListActivity")) {
                                            myApp.getActivityList().get(j).finish();
                                        }
                                    }
                                } else {
                                    // 返回登录页面
                                    for (int j = 0; j < myApp.getActivityList().size(); j++) {
                                        myApp.getActivityList().get(j).finish();
                                    }
                                    Intent mIntent = new Intent(BaseActivity.this, LoginActivity.class);
                                    mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mIntent);
                                }
                            } else {
                                // 用户
                                if (BaseActivity.this.getLocalClassName().contains("WriteData2Activity") &&
                                        ((WriteData2Activity) BaseActivity.this).isStart()) {
                                    // 不处理（刷写过程中）
                                } else {
                                    // 返回用户主界面
                                    if (WebSocketService.Companion.getState() == 2) {
                                        for (int j = 0; j < myApp.getActivityList().size(); j++) {
                                            myApp.getActivityList().get(j).finish();
                                        }
                                        Intent mIntent1 = new Intent(BaseActivity.this, SelectCarTypeActivity.class);
                                        mIntent1.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mIntent1);
                                        Intent mIntent = new Intent(BaseActivity.this, MainActivity.class);
                                        mIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mIntent);
                                        if (WebSocketService.Companion.getIntance() != null) {
                                            WebSocketService.Companion.getIntance().close();
                                        }
                                    }
                                }
                            }
                        }
                    }).
                    show();
        }
    }

    public void setData(String data) {
        setAllData(data, "setData");
    }

    public void setData1(String data) {
        setAllData(data, "setData1");
    }

    public void setData2(String data) {
        setAllData(data, "setData2");
    }

    @SuppressLint("StaticFieldLeak")
    public void setAllData(String data, String method) {
        if (isUserConnected) {// 用户连接
            String str = "{\"activity\":\"" + this.getLocalClassName() + "\",\"method\":\"" + method + "\",\"data\":\"" + data.replace("\"", "\\\"") + "\"}";
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {
                        Log.i("cyf", "WebSocketServie0 发送 : " + strings[0]);
                        return StrZipUtil.compress(strings[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(final String s) {
                    super.onPostExecute(s);
                    if (!s.equals("")) {
                        final Map<String, String> map = new HashMap<>();
                        map.put("data", s);
                        NetTools.net(map, new Urls().updateMsg, BaseActivity.this, new NetTools.MyCallBack() {
                            @Override
                            public void getData(BaseBean response) {
                                if (response != null && "0".equals(response.getCode())) {
                                    try {
                                        if (WebSocketService.Companion.getIntance() != null && WebSocketService.Companion.getIntance().isConnected()) {
                                            JSONObject jsonObject = new JSONObject(response.getData());
                                            WebSocketBean webSocketBean = new WebSocketBean();
                                            webSocketBean.setS("" + SPTools.INSTANCE.get(BaseActivity.this, Constant.USERID, ""));
                                            webSocketBean.setR("" + SPTools.INSTANCE.get(BaseActivity.this, Constant.ZFORUID, ""));
                                            webSocketBean.setC("D");
                                            webSocketBean.setD(jsonObject.optString("id"));
                                            webSocketBean.setE("");
                                            WebSocketService.Companion.getIntance().sendMsg(new Gson().toJson(webSocketBean));
                                            Log.i("cyf", "WebSocketServie1 发送 : " + jsonObject.optString("id"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, "正在加载...", false, false);
                    }
                }
            }.execute(str);
        }
    }

    public void setClickMethod(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // 避免连续多次点击
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setClickMethod(view);
                    }
                }, 1000);

                // 如果长连接开启，并且我是专家端，并且不是断开按钮
                if (clickMethoListener != null) {
                    sendClick(BaseActivity.this.getLocalClassName(), view.getTag().toString());
                    clickMethoListener.doMethod(view.getTag().toString());
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void sendClick(String className, String tag) {
        if (WebSocketService.Companion.getIntance() != null &&
                (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userProfessional &&
                !"tv_ycxz".equals(tag)) {
            // 点击事件的远程控制
            String str = "{\"activity\":\"" + className + "\",\"tag\":\"" + tag + "\"}";
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {
//                        Log.e("cyf22", "原数据：" + strings[0]);
//                        Log.i("cyf22", "压缩数据：" + StrZipUtil.compress(strings[0]));
                        return StrZipUtil.compress(strings[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (!s.equals("")) {
                        Map<String, String> map = new HashMap<>();
                        map.put("data", s);
                        NetTools.net(map, new Urls().updateMsg, BaseActivity.this, new NetTools.MyCallBack() {
                            @Override
                            public void getData(BaseBean response) {
                                if (response != null && "0".equals(response.getCode())) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.getData());
                                        WebSocketBean webSocketBean = new WebSocketBean();
                                        webSocketBean.setS("" + SPTools.INSTANCE.get(BaseActivity.this, Constant.USERID, ""));
                                        webSocketBean.setR("" + SPTools.INSTANCE.get(BaseActivity.this, Constant.ZFORUID, ""));
                                        webSocketBean.setC("D");
                                        webSocketBean.setD(jsonObject.optString("id"));
                                        webSocketBean.setE("");
                                        if (WebSocketService.Companion.getIntance() != null && WebSocketService.Companion.getIntance().isConnected()) {
                                            WebSocketService.Companion.getIntance().sendMsg(new Gson().toJson(webSocketBean));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, "正在加载...", false, false);
                    }
                }
            }.execute(str);
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        myApp.getActivityList().remove(this);
        if (myApp.getActivityList().size() == 0) {
            try {
                myApp.publicUnit.stopRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 停止屏蔽电话
            if (myApp.getIntentOne() != null) {
                stopService(myApp.getIntentOne());
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isUserConnected && !isProfessionalConnected) {
            super.onBackPressed();
        }
    }

    public interface ClickMethoListener {
        void doMethod(String string);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
