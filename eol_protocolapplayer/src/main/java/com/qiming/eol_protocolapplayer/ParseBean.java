package com.qiming.eol_protocolapplayer;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseBean {
    private String id;
    private String sid;
    private String nameZh;
    private String nameEn;
    private int beginPosition;
    private int length;

    private int bitStart = 0;
    private int bitLength = 0;

    private float coefficient;

    private float offsets;

    private ParseTypeEnum parseType;

    private String unit;

    private String enumDesc;
    private double valueMin = -1.0;
    private double valueMax = -1.0;
    private String unitENG;

    public void Parse(JSONObject rs) {
        try {
            this.setSid(rs.has("SID") ? rs.getString("SID") : "");
            this.setId(rs.has("DID") ? rs.getString("DID") : "");
            this.setNameZh(rs.has("Name") ? rs.getString("Name") : "");
            this.setNameEn(rs.has("Name_ENG") ? rs.getString("Name_ENG") : "");
            this.setBeginPosition(Integer.parseInt(checkEmpty((rs.has("Byte_Start") ? rs.getString("Byte_Start") : "-1"), "-1")));
            this.setLength(Integer.parseInt(checkEmpty((rs.has("Byte_Length") ? rs.getString("Byte_Length") : "-1"), "-1")));
            this.setBitStart(Integer.parseInt(checkEmpty((rs.has("Bit_Start") ? rs.getString("Bit_Start") : "-1"), "-1")));
            this.setBitLength(Integer.parseInt(checkEmpty((rs.has("Bit_Length") ? rs.getString("Bit_Length") : "-1"), "-1")));
            this.setOffsets(Float.parseFloat(checkEmpty((rs.has("Offset") ? rs.getString("Offset") : "-1"), "-1")));
            this.setCoefficient(Float.parseFloat(checkEmpty((rs.has("Coefficient") ? rs.getString("Coefficient") : "-1"), "-1")));
            this.setParseType(ParseTypeEnum.valueOf(Integer.parseInt(checkEmpty((rs.has("Type") ? rs.getString("Type") : "0"), "0"))));
            this.setEnumDesc(rs.has("Enum") ? rs.getString("Enum") : "");
            this.setUnit(rs.has("Unit") ? rs.getString("Unit") : "");
            this.setUnitENG(rs.has("Unit_ENG") ? rs.getString("Unit_ENG") : "");
            this.setValueMin(Double.parseDouble(checkEmpty((rs.has("Value_Min") ? rs.getString("Value_Min") : "-1"), "-1")));
            this.setValueMax(Double.parseDouble(checkEmpty((rs.has("Value_Max") ? rs.getString("Value_Max") : "1"), "1")));
        } catch (JSONException e) {
            LogTools.errLog(e);
        }
    }

    private String checkEmpty(String data, String value) {
        if (data == null || data.trim().equals("")) {
            return value;
        }
        return data.trim();
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBeginPosition() {
        return beginPosition;
    }

    public void setBeginPosition(int beginPosition) {
        this.beginPosition = beginPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public float getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(float coefficient) {
        this.coefficient = coefficient;
    }

    public float getOffsets() {
        return offsets;
    }

    public void setOffsets(float offsets) {
        this.offsets = offsets;
    }

    public ParseTypeEnum getParseType() {
        return parseType;
    }

    public void setParseType(ParseTypeEnum parseType2) {
        this.parseType = parseType2;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

//	@Override
//	public String toString() 
//	{
//		return "ParseBean [id=" + id + ", nameZh=" + nameZh + ", nameEn=" + nameEn + ", beginPosition=" + beginPosition + ", length=" + length
//				+ ", bitStart=" + bitStart + ", bitLength=" + bitLength+", coefficient=" + coefficient + ", offsets=" + offsets + ", parseType=" + parseType + "]";
//	}

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEnumDesc() {
        return enumDesc;
    }

    public void setEnumDesc(String enumDesc) {
        this.enumDesc = enumDesc;
    }

    public int getBitStart() {
        return bitStart;
    }

    public void setBitStart(int bitStart) {
        this.bitStart = bitStart;
    }

    public int getBitLength() {
        return bitLength;
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    public double getValueMin() {
        return valueMin;
    }

    public void setValueMin(double valueMin) {
        this.valueMin = valueMin;
    }

    public double getValueMax() {
        return valueMax;
    }

    public void setValueMax(double valueMax) {
        this.valueMax = valueMax;
    }

    public String getUnitENG() {
        return unitENG;
    }

    public void setUnitENG(String unitENG) {
        this.unitENG = unitENG;
    }
}
