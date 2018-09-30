package com.cy.obdproject.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.cy.obdproject.service.MService;
import com.qiming.eol_public.InitClass;
import com.tencent.bugly.Bugly;

import java.util.ArrayList;
import java.util.List;

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
    }


    public void stopCallPhone(){
        // 开始屏蔽电话
        Log.i("cyf","开始屏蔽电话");
        intentOne = new Intent(this, MService.class);
        startService(intentOne);
    }

}
