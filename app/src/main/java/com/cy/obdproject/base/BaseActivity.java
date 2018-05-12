package com.cy.obdproject.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cy.obdproject.app.MyApp;
import com.cy.obdproject.bean.WebSocketBean;
import com.cy.obdproject.constant.Constant;
import com.cy.obdproject.net.WebSocketServie;
import com.google.gson.Gson;

public class BaseActivity extends AppCompatActivity {

    private ClickMethoListener clickMethoListener;
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

    public void setClickMethod(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickMethoListener != null) {
                    // 如果长连接开启，并且我是专家端
                    if (WebSocketServie.Companion.getIntance() != null && myApp.getUserType() == Constant.userProfessional) {
                        // 点击事件的远程控制
//                        String str = BaseActivity.this.getLocalClassName() + "," + view.getTag().toString() + "";
//
                        WebSocketBean webSocketBean = new WebSocketBean();
                        webSocketBean.setS("");// 自己（专家）id
                        webSocketBean.setR("");// 连接用户id
                        webSocketBean.setC("D");
                        webSocketBean.setD("");// 自定义的json串
                        WebSocketServie.Companion.getIntance().sendMsg(new Gson().toJson(webSocketBean));
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
