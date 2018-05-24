package com.cy.obdproject.url;

public class Urls {

    public static final String ws_url = "ws://47.104.212.193:2012";//ws://10.133.73.204:2012
//    public static final String ws_url = "ws://10.133.73.77:2012";//ws://10.133.73.204:2012

    public static String base_url = "http://47.104.212.193:8099";
//    public static String base_url = "http://10.133.73.77:8099";

    public final String auth_login = base_url + "/login";// 登录
    public final String updateMsg = base_url + "/updateMsg";// 上传信息
    public final String getMsg = base_url + "/getMsg";// 获取信息
    public final String requestList = base_url + "/requestList";// 请求列表（专家端获取请求用户列表s，用户获取专家列表z）
    public final String fileList = base_url + "/fileList";// 刷写文件列表

    public final String checkOBD = base_url+"/checkOBD";// 验证OBD
    public final String upLog = base_url+"/upLog";// Log上传

}
