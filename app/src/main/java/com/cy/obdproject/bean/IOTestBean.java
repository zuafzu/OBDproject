package com.cy.obdproject.bean;

import java.io.Serializable;

public class IOTestBean implements Serializable{

    private String name;
    private String id;

    public IOTestBean() {
    }

    public IOTestBean(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IOTestBean{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
