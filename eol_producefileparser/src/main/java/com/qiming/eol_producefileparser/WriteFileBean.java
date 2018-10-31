package com.qiming.eol_producefileparser;

import java.io.Serializable;
import java.util.Arrays;

public class WriteFileBean implements Serializable {

    private String address;
    private String endAddress;
    private String length;
    private String crc;
    private byte[] data;

    public WriteFileBean() {
        super();
    }

    public WriteFileBean(String address, String endAddress, String length, String crc, byte[] data) {
        this.address = address;
        this.endAddress = endAddress;
        this.length = length;
        this.crc = crc;
        this.data = data;
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

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "WriteFileBean{" +
                "address='" + address + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", length='" + length + '\'' +
                ", crc='" + crc + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
