package com.qiming.eol_protocolapplayer;

import android.util.Log;

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
     * 加密
     *
     * @param string
     * @return
     */
    public static String decodeString(String string) {
        Log.e("cyf", "加密前发送数据：" + string);
        // return string;
        String crt = string.substring(8, 10);
        byte[] oldByte = StringTools.hex2byte(string.substring(12, string.length() - 2));
        byte[] newByte = new byte[oldByte.length];
        ECU2Tools.VCIAlg(true, Integer.valueOf(string.substring(6, 8), 16), StringTools.hex2byte(crt)[0], oldByte, oldByte.length, newByte, newByte.length);
        return string.substring(0, 12) + StringTools.byte2hex(newByte) + "55";
    }

    /**
     * 解密
     *
     * @param string
     * @return
     */
    public static String unDecodeString(String string) {
        // return string;
        String crt = string.substring(8, 10);
        byte[] oldByte = StringTools.hex2byte(string.substring(12, string.length() - 2));
        byte[] newByte = new byte[oldByte.length];
        ECU2Tools.VCIAlg(false, Integer.valueOf(string.substring(6, 8), 16), StringTools.hex2byte(crt)[0], oldByte, oldByte.length, newByte, newByte.length);
        return string.substring(0, 12) + StringTools.byte2hex(newByte) + "AA";
    }

    /**
     * 设置CAN线可配置引脚
     *
     * @param canHeighPin 1个字节
     * @param canLowPin   1个字节
     * @return
     */
    public static String a(String canHeighPin, String canLowPin) {
        return decodeString("aa000501" + numPlus() + "" + ECUagreement.canLinkNum + "" + canHeighPin + "" + canLowPin + "55");
    }


    /**
     * 设置CAN线通信速率
     *
     * @param canBraundrate 4个字节
     * @return
     */
    public static String b(String canBraundrate) {
        return decodeString("aa000741" + numPlus() + "" + ECUagreement.canLinkNum + "" + canBraundrate + "55");
    }

    /**
     * 设置CAN线屏蔽字
     *
     * @param acr 4个字节
     * @param amr 4个字节
     * @return
     */
    public static String c(String acr, String amr) {
        return decodeString("aa000b42" + numPlus() + "" + ECUagreement.canLinkNum + "" + acr + "" + amr + "55");
    }

    /**
     * 设置15765CANID
     *
     * @param test2ecu 4个字节
     * @param ecu2test 4个字节
     * @return
     */
    public static String d(String test2ecu, String ecu2test) {
        return decodeString("aa000b70" + numPlus() + "" + ECUagreement.canLinkNum + "" + test2ecu + "" + ecu2test + "55");
    }

    /**
     * 设置15765流控中STmin时间
     *
     * @param stmin 1个字节
     * @return
     */
    public static String e(String stmin) {
        return decodeString("aa000471" + numPlus() + "" + ECUagreement.canLinkNum + "" + stmin + "55");
    }

    /**
     * 设置15765开关
     *
     * @param state 1个字节
     * @return
     */
    public static String f(String state) {
        return decodeString("aa000474" + numPlus() + "" + ECUagreement.canLinkNum + "" + state + "55");
    }




    /**
     * stop can
     *
     * @return
     */
    public static String i() {
        return decodeString("aa00030f" + numPlus() + "10" + "55");
    }


}