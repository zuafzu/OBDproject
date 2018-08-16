package com.cy.obdproject.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.cy.obdproject.R;
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

public class BaseActivity extends AppCompatActivity {

    private ClickMethoListener clickMethoListener;
    private ProgressDialog progressDialog;
    public MyApp myApp;
    public boolean isUserConnected = false;// 是否用户远程协助中
    public boolean isProfessionalConnected = false;// 是否专家远程协助中

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
        // 专家端进入界面延迟3秒再操作，防止操作过快丢数据
        if (isProfessionalConnected) {
            showProgressDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                }
            }, 3000);
        }
    }

    public void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("");//2.设置标题
        progressDialog.setMessage("正在加载中，请稍等......");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                                        if (WebSocketService.Companion.getIntance() != null && WebSocketService.Companion.getIntance().isConnected()) {
                                            JSONObject jsonObject = new JSONObject(response.getData());
                                            WebSocketBean webSocketBean = new WebSocketBean();
                                            webSocketBean.setS("" + SPTools.INSTANCE.get(BaseActivity.this, Constant.USERID, ""));
                                            webSocketBean.setR("" + SPTools.INSTANCE.get(BaseActivity.this, Constant.ZFORUID, ""));
                                            webSocketBean.setC("D");
                                            webSocketBean.setD(jsonObject.optString("id"));
                                            webSocketBean.setE("");
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

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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

                // 如果长连接开启，并且我是专家端
                if (clickMethoListener != null) {
                    sendClick(BaseActivity.this.getLocalClassName(), view.getTag().toString());
                    clickMethoListener.doMethod(view.getTag().toString());
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void sendClick(String className, String tag) {
        if (WebSocketService.Companion.getIntance() != null && (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userProfessional) {
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

}
