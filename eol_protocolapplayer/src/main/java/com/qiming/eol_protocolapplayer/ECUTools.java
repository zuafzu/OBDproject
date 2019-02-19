package com.qiming.eol_protocolapplayer;

public class ECUTools {

    public static final String ERR = "err";
    public static final String WAIT = "wait";


    /**
     * 解析返回的数据(can线 读数据)
     *
     * @param data
     * @param myData
     * @return
     */
    public static String getData(String data, String myData) {
        if (data.toLowerCase().startsWith("55") && data.toLowerCase().endsWith("aa")) {
            if (data.length() >= 10) {
                String mm = data.substring(data.length() - 2 - 6, data.length() - 2).toLowerCase();
                if (mm.startsWith("7f") && mm.endsWith("78")) {
                    return WAIT;
                }
            }
            int a = myData.toLowerCase().indexOf(ECUagreement.Id.toLowerCase());
            String mKey1 = myData.substring(a + ECUagreement.Id.length() + 4, a + ECUagreement.Id.length() + 6);
            int b = data.toLowerCase().indexOf(ECUagreement.rId.toLowerCase());
            String mKey2 = data.substring(b + ECUagreement.rId.length() + 4, b + ECUagreement.rId.length() + 6);
            if (a != -1 && b != -1 && Integer.valueOf(mKey2, 16) - Integer.valueOf(mKey1, 16) == 0x40) {
//                try {
//                    // --------------------------已和张哥确认（+6这个长度是固定的，3个字节）---------------------------------------
//                    String msg = data.substring(b + ECUagreement.rId.length() + 4 + 6, data.length() - 2);
//                    if (msg.length() % 2 == 0) {
//                        // 返回原始数据
//                        StringBuilder mData = new StringBuilder(msg);
//                        return mData.toString();
//                    }
//                } catch (Exception e) {
//                    // LogTools.errLog(e);
//                    return "";
//                }
                return "";
            }
        }
        return ERR;
    }

    /**
     * 解析返回的数据(can线 安全请求)
     *
     * @param data
     * @param mKey1
     * @return
     */
    public static String getData2(String data, String mKey1) {
        if (data.toLowerCase().startsWith("55") && data.toLowerCase().endsWith("aa")) {
            if (data.length() >= 10) {
                String mm = data.substring(data.length() - 2 - 6, data.length() - 2).toLowerCase();
                if (mm.startsWith("7f") && mm.endsWith("78")) {
                    return WAIT;
                }
            }
            int b = data.toLowerCase().indexOf(ECUagreement.rId.toLowerCase());
            String mKey2 = data.substring(b + ECUagreement.rId.length() + 4, b + ECUagreement.rId.length() + 6);
            if (b != -1 && Integer.valueOf(mKey2, 16) - Integer.valueOf(mKey1, 16) == 0x40) {
//                try {
//                    // --------------------------已和张哥确认（+6这个长度是固定的，3个字节）---------------------------------------
//                    String msg = data.substring(b + ECUagreement.rId.length() + 4 + 6, data.length() - 2);
//                    if (msg.length() % 2 == 0) {
//                        // 返回原始数据
//                        StringBuilder mData = new StringBuilder(msg);
//                        return mData.toString();
//                    }
//                } catch (Exception e) {
//                    // LogTools.errLog(e);
//                    return "";
//                }
                return "";
            }
        }
        return ERR;
    }

    /**
     * 解析返回的数据(k线 读数据)
     *
     * @param data
     * @param myData
     * @return
     */
    public static String getData3(String data, String myData) {
//        if(data.toLowerCase().startsWith("55") && data.toLowerCase().endsWith("aa")){
//            if(data.length()>=10){
//                String mm = data.substring(data.length()-2-6,data.length()-2).toLowerCase();
//                if(mm.startsWith("7f")&&mm.endsWith("78")){
//                    return WAIT;
//                }
//            }
//        int a = myData.toLowerCase().indexOf(ECUagreement.Id.toLowerCase());
//        String mKey1 = myData.substring(a + ECUagreement.Id.length() + 4, a + ECUagreement.Id.length() + 6);
//        int b = data.toLowerCase().indexOf(ECUagreement.rId.toLowerCase());
//        String mKey2 = data.substring(b + ECUagreement.rId.length() + 4, b + ECUagreement.rId.length() + 6);
//        if (a != -1 && b != -1 && Integer.valueOf(mKey2, 16) - Integer.valueOf(mKey1, 16) == 0x40) {
//            try {
//                // --------------------------已和张哥确认（+6这个长度是固定的，3个字节）---------------------------------------
//                String msg = data.substring(b + ECUagreement.rId.length() + 4 + 6, data.length() - 2);
//                if (msg.length() % 2 == 0) {
//                    // 返回原始数据
//                    StringBuilder mData = new StringBuilder(msg);
//                    return mData.toString();
//                }
//            } catch (Exception e) {
//                // LogTools.errLog(e);
//                return "";
//            }
//        }
//        }
        return ERR;
    }

    /**
     * 录入
     *
     * @param data
     * @return
     */
    public static String putData(String data) {
        String[] strings = new String[data.length()];
        for (int i = 0; i < data.length(); i++) {
            strings[i] = data.substring(i, i + 1);
        }

        return parseAscii(data);
    }

    public static String parseAscii(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (int i = 0; i < bs.length; i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

    public static String toHex(int n) {
        StringBuilder sb = new StringBuilder();
        if (n / 16 == 0) {
            return toHexUtil(n);
        } else {
            String t = toHex(n / 16);
            int nn = n % 16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    private static String toHexUtil(int n) {
        String rt = "";
        switch (n) {
            case 10:
                rt += "A";
                break;
            case 11:
                rt += "B";
                break;
            case 12:
                rt += "C";
                break;
            case 13:
                rt += "D";
                break;
            case 14:
                rt += "E";
                break;
            case 15:
                rt += "F";
                break;
            default:
                rt += n;
        }
        return rt;
    }

}
