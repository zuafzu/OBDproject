package com.qiming.eol_protocolapplayer;

public class ECUagreement {

    public static String canLinkNum = "10"; // 1个字节
    public static String kLinkNum = "20"; // 1个字节

    public static String Id = ""; // "38f1" 2个字节（发送的kId）|| "000007A2" 4个字节（发送的CanId）
    public static String rId = ""; // "f138" 2个字节（返回的kId）|| "000007AA" 4个字节（返回的CanId）

    /**
     * can线数据
     *
     * @param data
     * @return
     */
    public static String a(String data) {
        String length = Integer.toHexString(data.replace(" ", "").length() / 2);
        if (length.length() == 1) {
            length = "000" + length;
        } else if (length.length() == 2) {
            length = "00" + length;
        } else if (length.length() == 3) {
            length = "0" + length;
        }
        String string = 75 + "" + OBDagreement.numPlus() + "" + canLinkNum + "" + Id + "" + length + "" + data;
        String sLength = Integer.toHexString(string.length() / 2);
        if (sLength.length() == 1) {
            return "aa" + "000" + sLength + string + "55";
        } else if (sLength.length() == 2) {
            return "aa" + "00" + sLength + string + "55";
        } else if (sLength.length() == 3) {
            return "aa" + "0" + sLength + string + "55";
        }
        return "aa" + Integer.toHexString(string.length() / 2) + string + "55";
    }

    /**
     * k线数据
     *
     * @param data
     * @return
     */
    public static String b(String data) {
        String length = Integer.toHexString((data.replace(" ", "").length() / 2) + 128);
        if (length.length() == 1) {
            length = "0" + length;
        }
        String endStr = getEndStr(Id + data);
        String string = 19 + "" + OBDagreement.numPlus() + "" + kLinkNum + "" + length + "" + Id + "" + data + endStr;
        String sLength = Integer.toHexString(string.length() / 2);
        if (sLength.length() == 1) {
            return "aa" + "000" + sLength + string + "55";
        } else if (sLength.length() == 2) {
            return "aa" + "00" + sLength + string + "55";
        } else if (sLength.length() == 3) {
            return "aa" + "0" + sLength + string + "55";
        }
        return "aa" + Integer.toHexString(string.length() / 2) + string + "55";
    }

    /**
     * 返回k线发送需要的最后一位累加值
     * 未实现需要孙哥提供方法
     *
     * @param string 要发送的k线id和真实数据
     * @return
     */
    public static String getEndStr(String string) {
//        String dataAndId = "8338f114ffff";
//
//        long endInt = 0;
//        for (int i = 0; i < dataAndId.length() / 2; i++) {
//            String mm = dataAndId.substring(2 * i, 2 * i + 2);
//            Long in = Long.valueOf(mm, 16);
//            endInt = endInt + in;
//        }
//
//        String endStr = String.format("%1s", Long.toHexString(endInt).toUpperCase());
//
//        return endStr;
        return "";
    }

}
