package com.cy.obdproject.bean;

import java.io.Serializable;
import java.util.Arrays;

public class WriteFileBean implements Serializable{

    private String address;
    private String endAddress;
    private String length;
    private byte[] data;
    private short[] data2;

    public WriteFileBean() {
        super();
    }

    public WriteFileBean(String address, String endAddress, String length, byte[] data, short[] data2) {
        this.address = address;
        this.endAddress = endAddress;
        this.length = length;
        this.data = data;
        this.data2 = data2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public short[] getData2() {
        return data2;
    }

    public void setData2(short[] data2) {
        this.data2 = data2;
    }

    @Override
    public String toString() {
        return "WriteFileBean{" +
                "address='" + address + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", length='" + length + '\'' +
                ", data=" + Arrays.toString(data) +
                ", data2=" + Arrays.toString(data2) +
                '}';
    }
}
