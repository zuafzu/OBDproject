package com.cy.obdproject.bean;

import java.io.Serializable;

public class DynamicDataBean implements Serializable {

    private String id;
    private String name;
    private String parsingType;//解析类型
    private String coefficient;//系数
    private String offset;//偏移
    private String enumValue;//枚举值（用^分隔）
    private String unit;//单位

    private String value = "";
    private String isSelect = "0";

    public DynamicDataBean() {
        super();
    }

    public DynamicDataBean(String id, String name, String parsingType, String coefficient, String offset, String enumValue, String unit, String value, String isSelect) {
        this.id = id;
        this.name = name;
        this.parsingType = parsingType;
        this.coefficient = coefficient;
        this.offset = offset;
        this.enumValue = enumValue;
        this.unit = unit;
        this.value = value;
        this.isSelect = isSelect;
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

    public String getParsingType() {
        return parsingType;
    }

    public void setParsingType(String parsingType) {
        this.parsingType = parsingType;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
                ", parsingType='" + parsingType + '\'' +
                ", coefficient='" + coefficient + '\'' +
                ", offset='" + offset + '\'' +
                ", enumValue='" + enumValue + '\'' +
                ", unit='" + unit + '\'' +
                ", value='" + value + '\'' +
                ", isSelect='" + isSelect + '\'' +
                '}';
    }
}
