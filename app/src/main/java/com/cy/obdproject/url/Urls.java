package com.cy.obdproject.url;

public class Urls {

//    public static final String ws_url = "ws://47.104.212.193:2012";//演示版
//    public static String base_url = "http://47.104.212.193:8099";//演示版

    public static final String ws_url = "ws://47.104.212.193:2013";//测试版
    private static String base_url = "http://47.104.212.193:8100";//测试版

    public static String uploadImg = "";// 上传图片url

    public final String auth_login = base_url + "/login";// 登录
    public final String updateMsg = base_url + "/updateMsg";// 上传信息
    public final String getMsg = base_url + "/getMsg";// 获取信息
    public final String requestList = base_url + "/requestList";// 请求列表（专家端获取请求用户列表s，用户获取专家列表z）
    public final String fileList = base_url + "/fileList";// 刷写文件列表
    public final String uploadCheck = base_url + "/uploadCheck";// 上传vn号和图片信息
    public final String getUploadImgUrl = base_url + "/getUploadImgUrl";// 获取上传图片地址
    public final String changePwd = base_url + "/changePwd";// 修改密码

    public final String setFileDownOK = base_url + "/setFileDownOK"; // 生产文件下载成功
    public final String getControlFile = base_url + "/getControlFile"; // 最新流程文件
    public final String getModuleFile = base_url + "/getModuleFile"; // 最新模块文件
    public final String getAppFile = base_url + "/getAppFile"; // 最新应用文件

}
