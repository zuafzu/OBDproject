package com.cy.obdproject.agreement;

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
     * @param canHeighPin 1个字节
     * @param canLowPin   1个字节
     * @return
     */
    public static String a(String canHeighPin, String canLowPin) {
        return "aa000501" + numPlus() + "" + ECUagreement.canLinkNum + "" + canHeighPin + "" + canLowPin + "55";
    }


    /**
     * 设置CAN线通信速率
     *
     * @param canBraundrate 4个字节
     * @return
     */
    public static String b(String canBraundrate) {
        return "aa000741" + numPlus() + "" + ECUagreement.canLinkNum + "" + canBraundrate + "55";
    }

    /**
     * 设置CAN线屏蔽字
     *
     * @param acr 4个字节
     * @param amr 4个字节
     * @return
     */
    public static String c(String acr, String amr) {
        return "aa000b42" + numPlus() + "" + ECUagreement.canLinkNum + "" + acr + "" + amr + "55";
    }

    /**
     * 设置15765CANID
     *
     * @param test2ecu 4个字节
     * @param ecu2test 4个字节
     * @return
     */
    public static String d(String test2ecu, String ecu2test) {
        return "aa000b70" + numPlus() + "" + ECUagreement.canLinkNum + "" + test2ecu + "" + ecu2test + "55";
    }

    /**
     * 设置15765流控中STmin时间
     *
     * @param stmin 1个字节
     * @return
     */
    public static String e(String stmin) {
        return "aa000471" + numPlus() + "" + ECUagreement.canLinkNum + "" + stmin + "55";
    }

    /**
     * 设置15765开关
     *
     * @param state 1个字节
     * @return
     */
    public static String f(String state) {
        return "aa000474" + numPlus() + "" + ECUagreement.canLinkNum + "" + state + "55";
    }


    /**
     * start 3e
     *
     * @return
     */
    public static String g() {
        return "aa001764" + numPlus() + "" + ECUagreement.canLinkNum + "010000" + ECUagreement.canId + "08023e800000000000000007d0" + "55";
    }

    /**
     * stop 3e
     *
     * @return
     */
    public static String h() {
        return "aa001764" + numPlus() + "" + ECUagreement.canLinkNum + "000000" + ECUagreement.canId + "08023e80000000000000000fa0" + "55";
    }

    /**
     * stop can
     *
     * @return
     */
    public static String i() {
        return "aa0003" + numPlus() + "7b1055" + "55";
    }


}
