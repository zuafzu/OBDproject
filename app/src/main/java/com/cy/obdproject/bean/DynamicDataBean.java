package com.cy.obdproject.bean;

import java.io.Serializable;

public class DynamicDataBean implements Serializable {


    private String SID;
    private String DID;
    private String Name;
    private String Name_ENG;
    private String Byte_Start;
    private String Byte_Length;
    private String Bit_Start;
    private String Bit_Length;
    private String Coefficient;
    private String Offset;
    private String Type;
    private String Enum;
    private String Unit;
    private String Unit_ENG;
    private String Value_Min;
    private String Value_Max;

//    private String id;
//    private String name;
//    private String byteLength;//字节长度
//    private String parsingType;//解析类型
//    private String coefficient;//系数
//    private String offset;//偏移
//    private String enumValue;//枚举值（用^分隔）
//    private String unit;//单位

    private String value = "";
    private String isSelect = "0";

    public DynamicDataBean() {
        super();
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName_ENG() {
        return Name_ENG;
    }

    public void setName_ENG(String name_ENG) {
        Name_ENG = name_ENG;
    }

    public String getByte_Start() {
        return Byte_Start;
    }

    public void setByte_Start(String byte_Start) {
        Byte_Start = byte_Start;
    }

    public String getByte_Length() {
        return Byte_Length;
    }

    public void setByte_Length(String byte_Length) {
        Byte_Length = byte_Length;
    }

    public String getBit_Start() {
        return Bit_Start;
    }

    public void setBit_Start(String bit_Start) {
        Bit_Start = bit_Start;
    }

    public String getBit_Length() {
        return Bit_Length;
    }

    public void setBit_Length(String bit_Length) {
        Bit_Length = bit_Length;
    }

    public String getCoefficient() {
        return Coefficient;
    }

    public void setCoefficient(String coefficient) {
        Coefficient = coefficient;
    }

    public String getOffset() {
        return Offset;
    }

    public void setOffset(String offset) {
        Offset = offset;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getEnum() {
        return Enum;
    }

    public void setEnum(String anEnum) {
        Enum = anEnum;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getUnit_ENG() {
        return Unit_ENG;
    }

    public void setUnit_ENG(String unit_ENG) {
        Unit_ENG = unit_ENG;
    }

    public String getValue_Min() {
        return Value_Min;
    }

    public void setValue_Min(String value_Min) {
        Value_Min = value_Min;
    }

    public String getValue_Max() {
        return Value_Max;
    }

    public void setValue_Max(String value_Max) {
        Value_Max = value_Max;
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
                "SID='" + SID + '\'' +
                ", DID='" + DID + '\'' +
                ", Name='" + Name + '\'' +
                ", Name_ENG='" + Name_ENG + '\'' +
                ", Byte_Start='" + Byte_Start + '\'' +
                ", Byte_Length='" + Byte_Length + '\'' +
                ", Bit_Start='" + Bit_Start + '\'' +
                ", Bit_Length='" + Bit_Length + '\'' +
                ", Coefficient='" + Coefficient + '\'' +
                ", Offset='" + Offset + '\'' +
                ", Type='" + Type + '\'' +
                ", Enum='" + Enum + '\'' +
                ", Unit='" + Unit + '\'' +
                ", Unit_ENG='" + Unit_ENG + '\'' +
                ", Value_Min='" + Value_Min + '\'' +
                ", Value_Max='" + Value_Max + '\'' +
                ", value='" + value + '\'' +
                ", isSelect='" + isSelect + '\'' +
                '}';
    }
}
