package com.cy.obdproject.bean;

import java.io.Serializable;

/**
 * 长连接使用模型
 */
public class WebSocketBean implements Serializable {

    // {"S":"zuser","R":"user","C":"D","D":"operation command"}
    private String S;
    private String R;
    private String C;
    private String D;


    public WebSocketBean() {
        super();
    }

    public WebSocketBean(String s, String r, String c, String d) {
        S = s;
        R = r;
        C = c;
        D = d;
    }

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    public String getR() {
        return R;
    }

    public void setR(String r) {
        R = r;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    @Override
    public String toString() {
        return "WebSocketBean{" +
                "S='" + S + '\'' +
                ", R='" + R + '\'' +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                '}';
    }
}
