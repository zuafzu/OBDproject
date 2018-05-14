package com.cy.obdproject.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cy.obdproject.app.MyApp;
import com.cy.obdproject.bean.WebSocketBean;
import com.cy.obdproject.constant.Constant;
import com.cy.obdproject.socket.WebSocketService;
import com.google.gson.Gson;

public class BaseActivity extends AppCompatActivity {

    private ClickMethoListener clickMethoListener;

    private ProgressDialog progressDialog;
    public MyApp myApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) getApplication();
        myApp.getActivityList().add(this);
        if (this instanceof ClickMethoListener) {
            clickMethoListener = (ClickMethoListener) this;
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
                    if (WebSocketService.Companion.getIntance() != null && myApp.getUserType() == Constant.userProfessional) {
                        // 点击事件的远程控制
//                        String str = BaseActivity.this.getLocalClassName() + "," + view.getTag().toString() + "";
//
                        WebSocketBean webSocketBean = new WebSocketBean();
                        webSocketBean.setS("");// 自己（专家）id
                        webSocketBean.setR("");// 连接用户id
                        webSocketBean.setC("D");
                        webSocketBean.setD("");// 自定义的json串
                        WebSocketService.Companion.getIntance().sendMsg(new Gson().toJson(webSocketBean));
                    }
                    clickMethoListener.doMethod(view.getTag().toString());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApp.getActivityList().remove(this);
    }

    public interface ClickMethoListener {
        void doMethod(String string);
    }

}
