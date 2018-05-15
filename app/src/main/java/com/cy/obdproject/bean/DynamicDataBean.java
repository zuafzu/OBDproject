package com.cy.obdproject.bean;

import java.io.Serializable;

public class DynamicDataBean implements Serializable{
    private String id;
    private String name;
    private String value = "value";
    private String isSelect = "0";

    public DynamicDataBean() {
        super();
    }

    public DynamicDataBean(String id, String name, String value, String isSelect) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.isSelect = isSelect;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(String isSelect) {
        this.isSelect = isSelect;
    }

    @Override
    public String toString() {
        return "DynamicDataBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", isSelect='" + isSelect + '\'' +
                '}';
    }
}
