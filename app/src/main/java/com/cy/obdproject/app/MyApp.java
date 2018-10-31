package com.cy.obdproject.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.cy.obdproject.service.MService;
import com.qiming.eol_public.InitClass;
import com.tencent.bugly.Bugly;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MyApp extends Application {

    private Intent intentOne = null;
    private List<Activity> activityList;
    public InitClass publicUnit = new InitClass();

    public Intent getIntentOne() {
        return intentOne;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "7cfd480a08", false);
        activityList = new ArrayList<>();
        publicUnit.context = this;
        // 设置https
        try {
            InputStream is = getAssets().open("static-s.cer");
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(new InputStream[]{is}, null, null);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    //其他配置
                    .build();
            OkHttpUtils.initClient(okHttpClient);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void stopCallPhone() {
        // 开始屏蔽电话
        Log.i("cyf", "开始屏蔽电话");
        intentOne = new Intent(this, MService.class);
        startService(intentOne);
    }

}
