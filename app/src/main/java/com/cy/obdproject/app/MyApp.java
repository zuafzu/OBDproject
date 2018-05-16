package com.cy.obdproject.app;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class MyApp extends Application {

    private List<Activity> activityList;
    private int userType = 0;// 默认没有用户

    public List<Activity> getActivityList() {
        return activityList;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityList = new ArrayList<>();
    }
}
