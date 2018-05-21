package com.cy.obdproject.url;

public class Urls {
    public static final String ws_url = "ws://47.104.212.193:2012";//ws://10.133.73.204:2012

    public static String base_url = "http://47.104.212.193:8099";
//    public static String base_url = "http://10.133.73.55:8099";

    public final String auth_login = base_url + "/login";// 登录

    public final String updateMsg = base_url + "/updateMsg";// 上传信息
    public final String getMsg = base_url + "/getMsg";// 获取信息

}
