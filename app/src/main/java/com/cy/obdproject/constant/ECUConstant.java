package com.cy.obdproject.constant;

import com.cy.obdproject.bean.SocketBean;

import java.util.ArrayList;

public class ECUConstant {

    /**
     * 读基本信息
     *
     * @return
     */
    public static ArrayList<SocketBean> getReadBaseInfoData() {
        ArrayList socketBeanList = new ArrayList();
        socketBeanList.add(new SocketBean("vin：", "62F190", 3, "10", "18da00fa", "0003", "22F190"));
        socketBeanList.add(new SocketBean("硬件版本号：", "62F1A6", 3, "10", "18da00fa", "0003", "22F1A6"));
        socketBeanList.add(new SocketBean("软件版本号：", "62F1A5", 3, "10", "18da00fa", "0003", "22F1A5"));
        return socketBeanList;
    }

}
