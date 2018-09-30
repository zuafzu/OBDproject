package com.cy.obdproject.bean;

import java.io.Serializable;

public class LoginBean implements Serializable{

    private String token;
    private String userType;
    private String userId;

    private String heartBeat;// 间隔时间
    private String threshold;// 超时时间

    public LoginBean() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(String heartBeat) {
        this.heartBeat = heartBeat;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "token='" + token + '\'' +
                ", userType='" + userType + '\'' +
                ", userId='" + userId + '\'' +
                ", heartBeat='" + heartBeat + '\'' +
                ", threshold='" + threshold + '\'' +
                '}';
    }
}
