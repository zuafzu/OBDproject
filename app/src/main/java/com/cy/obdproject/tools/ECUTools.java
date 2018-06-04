package com.cy.obdproject.tools;

import com.cy.obdproject.agreement.ECUagreement;

public class ECUTools {

    public static final String ERR = "err";
    public static final String WAIT = "wait";

    /**
     * 解析返回的数据(读数据)
     *
     * @param data
     * @param type
     * @param myData
     * @return
     */
    public static String getData(String data, String type, String myData) {
        return getData(data, Integer.valueOf(type), myData);
    }

    /**
     * 解析返回的数据(读数据)
     *
     * @param data
     * @param type
     * @param myData
     * @return
     */
    public static String getData(String data, int type, String myData) {
        if (data.contains("7F2278")) {
            return WAIT;
        }
        int a = myData.indexOf(ECUagreement.canId);
        String mKey1 = myData.substring(a + ECUagreement.canId.length() + 4, a + ECUagreement.canId.length() + 6);
        int b = data.indexOf(ECUagreement.reCanId);
        String mKey2 = data.substring(b + ECUagreement.reCanId.length() + 4, b + ECUagreement.reCanId.length() + 6);
        if (a != -1 && b != -1 && Integer.valueOf(mKey2, 16) - Integer.valueOf(mKey1, 16) == 0x40) {
            StringBuilder mData = new StringBuilder();
            try {
                // --------------------------已和张哥确认（+6这个长度是固定的，3个字节）---------------------------------------
                String msg = data.substring(b + ECUagreement.reCanId.length() + 4 + 6, data.length() - 2);
                if (msg.length() % 2 == 0) {
                    Integer[] strings = new Integer[msg.length() / 2];
                    if (type == 1) {
                        // 返回原始数据
                        mData = new StringBuilder(msg);
                    } else if (type == 2) {
                        // 返回十进制数字
                        for (int i = 0; i < msg.length() / 2; i++) {
                            strings[i] = Integer.parseInt(msg.substring(i * 2, (i + 1) * 2), 16);
                            mData.append(Integer.parseInt(msg.substring(i * 2, (i + 1) * 2), 16));
                        }
                    } else if (type == 3) {
                        // 返回char
                        for (int i = 0; i < strings.length; i++) {
                            mData.append(((char) Integer.parseInt(msg.substring(i * 2, (i + 1) * 2), 16)));
                        }
                    }
                    return mData.toString();
                }
            } catch (Exception e) {
                return "";
            }
        }
        return ERR;
    }

    /**
     * 解析返回的数据(安全请求)
     *
     * @param data
     * @param type
     * @param myData
     * @return
     */
    public static String getData2(String data, int type, String myData) {
        if (data.contains("7F2278")) {
            return WAIT;
        }
        int a = myData.indexOf(ECUagreement.canId);
        String mKey1 = myData.substring(a + ECUagreement.canId.length() + 4, a + ECUagreement.canId.length() + 6);
        int b = data.indexOf(ECUagreement.reCanId);
        String mKey2 = data.substring(b + ECUagreement.reCanId.length() + 4, b + ECUagreement.reCanId.length() + 6);
        if (a != -1 && b != -1 && Integer.valueOf(mKey2, 16) - Integer.valueOf(mKey1, 16) == 0x40) {
            StringBuilder mData = new StringBuilder();
            try {
                // --------------------------已和张哥确认（+4这个长度是固定的，2个字节）---------------------------------------
                String msg = data.substring(b + ECUagreement.reCanId.length() + 4 + 4, data.length() - 2);
                if (msg.length() % 2 == 0) {
                    Integer[] strings = new Integer[msg.length() / 2];
                    if (type == 1) {
                        // 返回原始数据
                        mData = new StringBuilder(msg);
                    } else if (type == 2) {
                        // 返回十进制数字
                        for (int i = 0; i < msg.length() / 2; i++) {
                            strings[i] = Integer.parseInt(msg.substring(i * 2, (i + 1) * 2), 16);
                            mData.append(Integer.parseInt(msg.substring(i * 2, (i + 1) * 2), 16));
                        }
                    } else if (type == 3) {
                        // 返回char
                        for (int i = 0; i < strings.length; i++) {
                            mData.append(((char) Integer.parseInt(msg.substring(i * 2, (i + 1) * 2), 16)));
                        }
                    }
                    return mData.toString();
                }
            } catch (Exception e) {
                return "";
            }
        }
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
