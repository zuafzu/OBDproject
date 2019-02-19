package com.cy.obdproject.constant;

public class Constant {

    public static final int userNormal = 0;//普通用户
    public static final int userProfessional = 1;//专家

    public static final String TOKEN = "token";
    public static final String ISLOGIN = "isLogin";
    public static final String USERTYPE = "userType";
    public static final String USERID = "userId";
    public static final String ZFORUID = "zforUid";//专家选择的用户ID
    public static final String CARNAME = "carName";
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "passWord";
    public static final String IP = "ip";
    public static final String SSID = "ssid";
    public static final String SSPW = "pw";

    public static String mDstName = "192.168.43.68";
    //     public static int mDstPort = 9200;// tcp新obd端口号
    public static int mDstPort = 6954; // tcp老obd端口号

    public static int SEND_PORT = 8989; //udp发送端口号
    public static int RECEIVE_PORT = 10000; //udp接收端口号

}
