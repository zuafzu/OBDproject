package com.cy.obdproject.app;

import android.app.Activity;
import android.app.Application;

import com.tencent.bugly.Bugly;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {

    private List<Activity> activityList;

    public List<Activity> getActivityList() {
        return activityList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "7cfd480a08", false);
        activityList = new ArrayList<>();
    }
}
