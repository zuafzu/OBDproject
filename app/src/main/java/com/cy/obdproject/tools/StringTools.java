package com.cy.obdproject.tools;

public class StringTools {

    /**
     * 十六进制串转化为byte数组
     *
     * @return the array of byte
     */
    public static final byte[] hex2byte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param b byte[] 需要转换的字节数组
     * @return String 十六进制字符串
     */
    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public byte[] _GetKey(int mask, byte[] seed, int seedLen) {
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

}
