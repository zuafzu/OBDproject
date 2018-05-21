package com.cy.obdproject.bean;

import java.io.Serializable;

/**
 * 长连接使用模型
 */
public class WebSocketBean implements Serializable {

    private String S = "";
    private String R = "";
    private String C = "";
    private String D = "";
    private String E = "";


    public WebSocketBean() {
        super();
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

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    @Override
    public String toString() {
        return "WebSocketBean{" +
                "S='" + S + '\'' +
                ", R='" + R + '\'' +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                ", E='" + E + '\'' +
                '}';
    }
}
