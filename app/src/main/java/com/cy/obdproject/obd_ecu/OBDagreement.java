package com.cy.obdproject.obd_ecu;

public class OBDagreement {

    public static Integer num = 0;// 1个字节（2位的16进制）

    // 自增16进制数
    public static String numPlus() {
        String crt = Integer.toHexString(num);
        if (num == 255) {
            num = 0;
        } else {
            num++;
        }
        if (crt.length() == 1) {
            crt = "0" + crt;
        }
        return crt;
    }

    /**
     * 设置CAN线可配置引脚
     *
     * @param canLinkNum  1个字节
     * @param canHeighPin 1个字节
     * @param canLowPin   1个字节
     * @return
     */
    public static String a(String canLinkNum, String canHeighPin, String canLowPin) {
        return "aa000501" + numPlus() + "" + canLinkNum + "" + canHeighPin + "" + canLowPin + "55";
    }


    /**
     * 设置CAN线通信速率
     *
     * @param canLinkNum    1个字节
     * @param canBraundrate 4个字节
     * @return
     */
    public static String b(String canLinkNum, String canBraundrate) {
        return "aa000741" + numPlus() + "" + canLinkNum + "" + canBraundrate + "55";
    }

    /**
     * 设置CAN线屏蔽字
     *
     * @param canLinkNum 1个字节
     * @param acr        4个字节
     * @param amr        4个字节
     * @return
     */
    public static String c(String canLinkNum, String acr, String amr) {
        return "aa000b42" + numPlus() + "" + canLinkNum + "" + acr + "" + amr + "55";
    }

    /**
     * 设置15765CANID
     *
     * @param canLinkNum 1个字节
     * @param test2ecu   4个字节
     * @param ecu2test   4个字节
     * @return
     */
    public static String d(String canLinkNum, String test2ecu, String ecu2test) {
        return "aa000b70" + numPlus() + "" + canLinkNum + "" + test2ecu + "" + ecu2test + "55";
    }

    /**
     * 设置15765流控中STmin时间
     *
     * @param canLinkNum 1个字节
     * @param stmin      1个字节
     * @return
     */
    public static String e(String canLinkNum, String stmin) {
        return "aa000471" + numPlus() + "" + canLinkNum + "" + stmin + "55";
    }

    /**
     * 设置15765开关
     *
     * @param canLinkNum 1个字节
     * @param state      1个字节
     * @return
     */
    public static String f(String canLinkNum, String state) {
        return "aa000474" + numPlus() + "" + canLinkNum + "" + state + "55";
    }



}
