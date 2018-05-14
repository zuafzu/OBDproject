package com.cy.obdproject.tools;

public class ECUTools {

    public static final String ERR = "err";
    public static final String WAIT = "wait";

    public static String getData(String data, int type, String key) {
        if (data.contains("7F2278")) {
            return WAIT;
        }
        StringBuilder mData = new StringBuilder();
        int index = data.indexOf(key);
        if (index != -1) {
            String msg = data.substring(index + 6, data.length() - 2);
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
        }
        return ERR;
    }

}
