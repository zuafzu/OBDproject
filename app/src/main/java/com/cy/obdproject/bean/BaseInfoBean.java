package com.cy.obdproject.bean;

public class BaseInfoBean {

    private String id;
    private String name;
    private String parsingType;//解析类型
    private String enumValue;//枚举值（用^分隔）
    private String byteLength;//字节长度

    private String value;

    public BaseInfoBean() {
        super();
    }

    public BaseInfoBean(String id, String name, String parsingType, String enumValue, String byteLength, String value) {
        this.id = id;
        this.name = name;
        this.parsingType = parsingType;
        this.enumValue = enumValue;
        this.byteLength = byteLength;
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

    public String getParsingType() {
        return parsingType;
    }

    public void setParsingType(String parsingType) {
        this.parsingType = parsingType;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public String getByteLength() {
        return byteLength;
    }

    public void setByteLength(String byteLength) {
        this.byteLength = byteLength;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BaseInfoBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", parsingType='" + parsingType + '\'' +
                ", enumValue='" + enumValue + '\'' +
                ", byteLength='" + byteLength + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
