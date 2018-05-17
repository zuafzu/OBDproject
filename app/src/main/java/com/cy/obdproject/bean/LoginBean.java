package com.cy.obdproject.bean;

import java.io.Serializable;

public class LoginBean implements Serializable{

    private String token;
    private String userType;
    private String userId;

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

    @Override
    public String toString() {
        return "LoginBean{" +
                "token='" + token + '\'' +
                ", userType='" + userType + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
