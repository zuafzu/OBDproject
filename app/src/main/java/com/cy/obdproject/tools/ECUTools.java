package com.cy.obdproject.tools;

public class ECUTools {

    public static final String ERR = "err";
    public static final String WAIT = "wait";

    /**
     * 解析返回的数据
     *
     * @param data
     * @param type
     * @param key
     * @return
     */
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

    /**
     * 录入
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

    public static byte[] _GetKey( byte[] seed) {
        return _GetKey(0x4a68795b,seed,4);
    }

    public static byte[] _GetKey(int mask, byte[] seed, int seedLen) {
        int retLen = 0;
        byte[] bytes = new byte[4];
        byte[] key = new byte[4];

        int wort;

        if (seed[1] == 0 && seed[2] == 0)
            return null;
        else {
            retLen = seedLen - 1;
            wort = seed[1] << 24 + seed[2] << 16 + seed[3] << 8 + seed[4];

            for (int i = 0; i < 35; i++) {
                if ((wort & 0x80000000) > 0) {
                    wort = wort << 1;
                    wort = (wort ^ mask);
                } else {
                    wort = wort << 1;
                }
            }
            for (int i = 0; i < 4; i++) {
                key[3 - i] = bytes[i];
            }
        }
        return key;
    }

    public static String parseAscii(String str){
        StringBuilder sb=new StringBuilder();
        byte[] bs=str.getBytes();
        for(int i=0;i<bs.length;i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

    public static String toHex(int n){
        StringBuilder sb=new StringBuilder();
        if(n/16==0){
            return toHexUtil(n);
        }else{
            String t=toHex(n/16);
            int nn=n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    private static String toHexUtil(int n){
        String rt="";
        switch(n){
            case 10:rt+="A";break;
            case 11:rt+="B";break;
            case 12:rt+="C";break;
            case 13:rt+="D";break;
            case 14:rt+="E";break;
            case 15:rt+="F";break;
            default:
                rt+=n;
        }
        return rt;
    }

}
