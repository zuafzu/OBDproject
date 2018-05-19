package com.cy.obdproject.bean;

import java.io.Serializable;

public class SocketBean implements Serializable{

    private String name;
    private int type;
    private String data;

    public SocketBean() {
        super();
    }

    public SocketBean(String name, int type,  String data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
                ", type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
