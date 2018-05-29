package com.cy.obdproject.base;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.cy.obdproject.url.Urls;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void setAllData(String data, String method) {
        if (isUserConnected) {// 用户连接
            String str = "{\"activity\":\"" + this.getLocalClassName() + "\",\"method\":\"" + method + "\",\"data\":\"" + data.replace("\"", "\\\"") + "\"}";
            Map<String, String> map = new HashMap<>();
            map.put("data", str);
            NetTools.net(map, new Urls().updateMsg, this, new NetTools.MyCallBack() {
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

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setClickMethod(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickMethoListener != null) {
                    // 如果长连接开启，并且我是专家端
                    sendClick(BaseActivity.this.getLocalClassName(), view.getTag().toString());
                    clickMethoListener.doMethod(view.getTag().toString());
                }
            }
        });
    }

    public void sendClick(String className, String tag) {
        if (WebSocketService.Companion.getIntance() != null && (int) SPTools.INSTANCE.get(this, Constant.USERTYPE, 0) == Constant.userProfessional) {
            // 点击事件的远程控制
            String str = "{\"activity\":\"" + className + "\",\"tag\":\"" + tag + "\"}";
            Map<String, String> map = new HashMap<>();
            map.put("data", str);
            NetTools.net(map, new Urls().updateMsg, this, new NetTools.MyCallBack() {
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
