package com.cy.obdproject.bean;

import java.io.Serializable;

public class SocketBean implements Serializable{

    private String name;
    private String key;
    private int type;
    private String canLinkNum;
    private String canId;
    private String length;
    private String data;

    public SocketBean() {
        super();
    }

    public SocketBean(String name, String key, int type, String canLinkNum, String canId, String length, String data) {
        this.name = name;
        this.key = key;
        this.type = type;
        this.canLinkNum = canLinkNum;
        this.canId = canId;
        this.length = length;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCanLinkNum() {
        return canLinkNum;
    }

    public void setCanLinkNum(String canLinkNum) {
        this.canLinkNum = canLinkNum;
    }

    public String getCanId() {
        return canId;
    }

    public void setCanId(String canId) {
        this.canId = canId;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SocketBean{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", type=" + type +
                ", canLinkNum='" + canLinkNum + '\'' +
                ", canId='" + canId + '\'' +
                ", length='" + length + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
