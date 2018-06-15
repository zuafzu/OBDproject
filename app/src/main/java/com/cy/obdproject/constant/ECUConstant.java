package com.cy.obdproject.constant;

import com.cy.obdproject.bean.BaseInfoBean;
import com.cy.obdproject.bean.DynamicDataBean;
import com.cy.obdproject.bean.ErrorCodeBean;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import jxl.Sheet;
import jxl.Workbook;

public class ECUConstant {

    /**
     * 读基本信息
     *
     * @return
     */
    public static ArrayList<BaseInfoBean> getReadBaseInfoData() {
//        ArrayList<SocketBean> socketBeanList = new ArrayList<>();
//        socketBeanList.add(new SocketBean("模式配置：", 1, "22F100"));
//        socketBeanList.add(new SocketBean("FAW ECU启动软件版本号：", 3, "22F180"));
//        socketBeanList.add(new SocketBean("FAW ECU应用软件版本号：", 3, "22F181"));
//        socketBeanList.add(new SocketBean("生产文件号：", 3, "22F182"));
//        socketBeanList.add(new SocketBean("供应商ECU启动软件版本号：", 3, "22F183"));
//        socketBeanList.add(new SocketBean("FAW部件号：", 3, "22F187"));
//        socketBeanList.add(new SocketBean("FAW ECU软件版本号：", 3, "22F189"));
//        socketBeanList.add(new SocketBean("ECU序列号：", 3, "22F18C"));
//        socketBeanList.add(new SocketBean("FAW车辆识别号码：", 3, "22F190"));
//        socketBeanList.add(new SocketBean("FAW ECU硬件版本号：", 3, "22F191"));
//        socketBeanList.add(new SocketBean("供应商ECU硬件编号：", 3, "22F192"));
//        socketBeanList.add(new SocketBean("供应商ECU硬件版本号：", 3, "22F193"));
//        socketBeanList.add(new SocketBean("供应商ECU软件编号：", 3, "22F194"));
//        socketBeanList.add(new SocketBean("供应商ECU软件版本号：", 3, "22F195"));
//        socketBeanList.add(new SocketBean("维修店代码和/或诊断仪序列号：", 3, "22F198"));
//        socketBeanList.add(new SocketBean("编程日期：", 1, "22F199"));
//        socketBeanList.add(new SocketBean("FAW标定软件版本号：", 3, "22F19C"));
//        socketBeanList.add(new SocketBean("ECU安装日期：", 1, "22F19D"));
//        socketBeanList.add(new SocketBean("FAW预留：", 1, "22F1A0"));
//        socketBeanList.add(new SocketBean("车辆配置信息：", 1, "22F1A1"));
//        socketBeanList.add(new SocketBean("SK码：", 1, "22F1A3"));
//        socketBeanList.add(new SocketBean("供应商ECU校准软件版本号：", 3, "22F1A4"));
//        socketBeanList.add(new SocketBean("车辆规格编号：", 3, "22F1A5"));
//        socketBeanList.add(new SocketBean("FAW生产线中的汽车制造日期：", 1, "22F1A6"));
//        socketBeanList.add(new SocketBean("FAW预留：", 1, "22F1A7"));
//        socketBeanList.add(new SocketBean("车辆运输模式：", 1, "22F1A8"));
//        socketBeanList.add(new SocketBean("车辆售后服务模式：", 1, "22F1A9"));
//        socketBeanList.add(new SocketBean("a2l文件ID：", 1, "22F1AA"));
//        socketBeanList.add(new SocketBean("噪声Simu语音配置：", 1, "22F1AB"));
//        return socketBeanList;
        ArrayList<BaseInfoBean> socketBeanList = new ArrayList<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(Constant.xlsFilePath));
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(5);
            for (int j = 0; j < sheet.getRows(); j++) {
                if (j != 0) {
                    if (!(sheet.getCell(0, j)).getContents().isEmpty() && !(sheet.getCell(1, j)).getContents().isEmpty()) {
                        BaseInfoBean baseInfoBean = new BaseInfoBean();
                        baseInfoBean.setId("22" + sheet.getCell(0, j).getContents().replace(" ", ""));
                        baseInfoBean.setName(sheet.getCell(1, j).getContents());
                        baseInfoBean.setParsingType(sheet.getCell(9, j).getContents());
                        baseInfoBean.setEnumValue(sheet.getCell(10, j).getContents());
                        socketBeanList.add(baseInfoBean);
                    }
                }
            }
        } catch (Exception e) {

        }
        return socketBeanList;
    }

    /**
     * 写基本信息(读)
     *
     * @return
     */
    public static ArrayList<BaseInfoBean> getWriteBaseInfoData1() {
//        ArrayList<SocketBean> socketBeanList = new ArrayList<>();
//        socketBeanList.add(new SocketBean("模式配置：", 1, "22F100"));
//        socketBeanList.add(new SocketBean("FAW车辆识别号码：", 3, "22F190"));
//        socketBeanList.add(new SocketBean("维修店代码和/或诊断仪序列号：", 3, "22F198"));
//        socketBeanList.add(new SocketBean("ECU安装日期：", 1, "22F19D"));
//        socketBeanList.add(new SocketBean("车辆规格编号：", 3, "22F1A5"));
//        socketBeanList.add(new SocketBean("FAW生产线中的汽车制造日期：", 1, "22F1A6"));
//        socketBeanList.add(new SocketBean("车辆运输模式：", 1, "22F1A8"));
//        socketBeanList.add(new SocketBean("车辆售后服务模式：", 1, "22F1A9"));
//        socketBeanList.add(new SocketBean("噪声Simu语音配置：", 1, "22F1AB"));
//        socketBeanList.add(new SocketBean("车辆配置信息：", 1, "22F1A1"));
//        return socketBeanList;
        ArrayList<BaseInfoBean> socketBeanList = new ArrayList<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(Constant.xlsFilePath));
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(7);
            for (int j = 0; j < sheet.getRows(); j++) {
                if (j != 0) {
                    if (!(sheet.getCell(0, j)).getContents().isEmpty() && !(sheet.getCell(1, j)).getContents().isEmpty()) {
                        BaseInfoBean baseInfoBean = new BaseInfoBean();
                        baseInfoBean.setId("22" + sheet.getCell(0, j).getContents().replace(" ", ""));
                        baseInfoBean.setName(sheet.getCell(1, j).getContents());
                        baseInfoBean.setByteLength(sheet.getCell(4, j).getContents().replace(" ", ""));
                        baseInfoBean.setParsingType(sheet.getCell(9, j).getContents().replace(" ", ""));
                        baseInfoBean.setEnumValue(sheet.getCell(10, j).getContents().replace(" ", ""));
                        socketBeanList.add(baseInfoBean);
                    }
                }
            }
        } catch (Exception e) {

        }
        return socketBeanList;
    }

    /**
     * 写基本信息(写)
     *
     * @return
     */
    public static ArrayList<BaseInfoBean> getWriteBaseInfoData2() {
//        ArrayList<SocketBean> socketBeanList = new ArrayList<>();
//        socketBeanList.add(new SocketBean("模式配置：", 1, "2EF100"));
//        socketBeanList.add(new SocketBean("FAW车辆识别号码：", 1, "2EF190"));
//        socketBeanList.add(new SocketBean("维修店代码和/或诊断仪序列号：", 1, "2EF198"));
//        socketBeanList.add(new SocketBean("ECU安装日期：", 1, "2EF19D"));
//        socketBeanList.add(new SocketBean("车辆规格编号：", 1, "2EF1A5"));
//        socketBeanList.add(new SocketBean("FAW生产线中的汽车制造日期：", 1, "2EF1A6"));
//        socketBeanList.add(new SocketBean("车辆运输模式：", 1, "2EF1A8"));
//        socketBeanList.add(new SocketBean("车辆售后服务模式：", 1, "2EF1A9"));
//        socketBeanList.add(new SocketBean("噪声Simu语音配置：", 1, "2EF1AB"));
//        socketBeanList.add(new SocketBean("车辆配置信息：", 1, "2EF1A1"));
//        return socketBeanList;
        ArrayList<BaseInfoBean> socketBeanList = new ArrayList<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(Constant.xlsFilePath));
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(7);
            for (int j = 0; j < sheet.getRows(); j++) {
                if (j != 0) {
                    if (!(sheet.getCell(0, j)).getContents().isEmpty() && !(sheet.getCell(1, j)).getContents().isEmpty()) {
                        BaseInfoBean baseInfoBean = new BaseInfoBean();
                        baseInfoBean.setId("2E" + sheet.getCell(0, j).getContents().replace(" ", ""));
                        baseInfoBean.setName(sheet.getCell(1, j).getContents());
                        baseInfoBean.setByteLength(sheet.getCell(4, j).getContents().replace(" ", ""));
                        baseInfoBean.setParsingType(sheet.getCell(9, j).getContents().replace(" ", ""));
                        baseInfoBean.setEnumValue(sheet.getCell(10, j).getContents().replace(" ", ""));
                        socketBeanList.add(baseInfoBean);
                    }
                }
            }
        } catch (Exception e) {

        }
        return socketBeanList;
    }

    /**
     * 故障信息
     *
     * @return
     */
    public static ArrayList<ErrorCodeBean> getErrorCodeData() {
        ArrayList<ErrorCodeBean> socketBeanList = new ArrayList<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(Constant.xlsFilePath));
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(8);
            for (int j = 0; j < sheet.getRows(); j++) {
                if (j != 0) {
                    if (!(sheet.getCell(0, j)).getContents().isEmpty() && !(sheet.getCell(1, j)).getContents().isEmpty()) {
                        socketBeanList.add(new ErrorCodeBean((sheet.getCell(0, j)).getContents().replace(" ", ""),
                                (sheet.getCell(1, j)).getContents()));
                    }
                }
            }
        } catch (Exception e) {

        }
        return socketBeanList;
    }

    /**
     * 动态数据
     *
     * @return
     */
    public static CopyOnWriteArrayList<DynamicDataBean> getDynamicData() {
        CopyOnWriteArrayList<DynamicDataBean> dynamicDataBeans = new CopyOnWriteArrayList<>();
        try {
            Workbook book = Workbook.getWorkbook(new File(Constant.xlsFilePath));
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(6);
            for (int j = 0; j < sheet.getRows(); j++) {
                if (j != 0) {
                    if (!(sheet.getCell(0, j)).getContents().isEmpty() && !(sheet.getCell(1, j)).getContents().isEmpty()) {
                        DynamicDataBean dynamicDataBean = new DynamicDataBean();
                        dynamicDataBean.setId("22" + (sheet.getCell(0, j)).getContents().replace(" ", ""));
                        dynamicDataBean.setName((sheet.getCell(1, j)).getContents());
                        dynamicDataBean.setCoefficient((sheet.getCell(7, j)).getContents().replace(" ", ""));
                        dynamicDataBean.setOffset((sheet.getCell(8, j)).getContents().replace(" ", ""));
                        dynamicDataBean.setParsingType((sheet.getCell(9, j)).getContents().replace(" ", ""));
                        dynamicDataBean.setEnumValue((sheet.getCell(10, j)).getContents().replace(" ", ""));
                        dynamicDataBean.setUnit((sheet.getCell(13, j)).getContents().replace(" ", ""));
                        dynamicDataBeans.add(dynamicDataBean);
                    }
                }
            }
        } catch (Exception e) {

        }
        return dynamicDataBeans;
    }
}
