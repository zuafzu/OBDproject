package com.qiming.eol_protocolapplayer;

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
        // Log.e("cyf", "加密前发送数据：" + string);
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
     * @return
     */
    public static String e() {
        // return decodeString("aa000471" + numPlus() + "" + ECUagreement.canLinkNum + "" + stmin + "55");
        return decodeString("aa000471" + numPlus() + "" + ECUagreement.canLinkNum + "" + "01" + "55");
    }

    /**
     * 设置15765流控中STmin时间2
     *
     * @param stmin 1个字节
     * @return
     */
    public static String t(String stmin) {
        return decodeString("aa000472" + numPlus() + "" + ECUagreement.canLinkNum + "" + stmin + "55");
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
     * start 3e
     *
     * @return
     */
    public static String g(String data) {
        // data = "023e800000000000   000007d0";
        return decodeString("aa001764" + numPlus() + "" + ECUagreement.canLinkNum + "010000" + ECUagreement.Id + "08" + data + "55");
    }

    /**
     * stop 3e
     *
     * @return
     */
    public static String h() {
        return decodeString("aa001764" + numPlus() + "" + ECUagreement.canLinkNum + "000000" + ECUagreement.Id + "08023e80000000000000000fa0" + "55");
    }

    /**
     * stop can
     *
     * @return
     */
    public static String i() {
        return decodeString("aa00030f" + numPlus() + "10" + "55");
    }


    /**
     * 设置K线可配置引脚
     *
     * @param pin 1个字节
     * @return
     */
    public static String j(String pin) {
        // aa 00 04 02 00 20 06 55
        return decodeString("aa000402" + numPlus() + "" + ECUagreement.kLinkNum + "" + pin + "55");
    }

    /**
     * 设置K线通信速率
     *
     * @param kBraundrate 4个字节
     * @return
     */
    public static String k(String kBraundrate) {
        // aa 00 07 10 01 20 00 00 28 a0 55
        return decodeString("aa000710" + numPlus() + "" + ECUagreement.kLinkNum + "" + kBraundrate + "55");
    }

    /**
     * 设置诊断仪在线发送方式
     *
     * @return
     */
    public static String l() {
        // aa 00 04 2a 02 20 01 55
        return decodeString("aa00042a" + numPlus() + "" + ECUagreement.kLinkNum + "01" + "55");
    }


    /**
     * 设置诊断仪在线最大时间间隔
     *
     * @return
     */
    public static String m() {
        // aa 00 07 2b 03 20 00 00 0b b8 55
        return decodeString("aa00072b" + numPlus() + "" + ECUagreement.kLinkNum + "00000bb8" + "55");
    }

    /**
     * 设置K线Wakeup时间
     *
     * @return
     */
    public static String n() {
        // aa 00 05 15 04 20 00 31 55
        return decodeString("aa000515" + numPlus() + "" + ECUagreement.kLinkNum + "0031" + "55");
    }

    /**
     * 设置K线通信时间P1Max
     *
     * @param CP1MAX 2个字节
     * @return
     */
    public static String o(String CP1MAX) {
        // aa 00 05 12 05 20 00 14 55
        return decodeString("aa000512" + numPlus() + "" + ECUagreement.kLinkNum + "" + CP1MAX + "55");
    }

    /**
     * 设置K线通信时间P4Min
     *
     * @param P4Min 2个字节
     * @return
     */
    public static String p(String P4Min) {
        // aa 00 05 13 06 20 00 00 55
        return decodeString("aa000513" + numPlus() + "" + ECUagreement.kLinkNum + "" + P4Min + "55");
    }

    /**
     * K线初始化设置
     *
     * @param INIT_MODE 1个字节
     * @return
     */
    public static String q(String INIT_MODE) {
        // aa 00 04 28 07 20 02 55
        return decodeString("aa000428" + numPlus() + "" + ECUagreement.kLinkNum + "" + INIT_MODE + "55");
    }

    /**
     * 快速初始化
     *
     * @return
     */
    public static String r() {
        // aa 00 08 18 08 20 81 38 f1 81 2b 55
        String endStr = ECUagreement.getEndStr(ECUagreement.Id + 81);
        return decodeString("aa000818" + numPlus() + "" + ECUagreement.kLinkNum + "81" + ECUagreement.Id + "81" + endStr + "55");
    }

    /**
     * stop k
     *
     * @return
     */
    public static String s() {
        // AA 00 03 0F CRT 20 55
        return decodeString("aa00030f" + numPlus() + "" + ECUagreement.kLinkNum + "" + "55");
    }


}
