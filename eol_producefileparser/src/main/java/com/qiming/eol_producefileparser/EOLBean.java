package com.qiming.eol_producefileparser;

import java.util.ArrayList;
import java.util.List;

public class EOLBean {

    private String start;
    private String end;
    private String length;
    private String crc;
    private List<WriteFileBean> datas = new ArrayList<>();

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public List<WriteFileBean> getDatas() {
        return datas;
    }

    public void setDatas(List<WriteFileBean> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "EOLBean{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", length='" + length + '\'' +
                ", crc='" + crc + '\'' +
                ", datas=" + datas +
                '}';
    }
}
