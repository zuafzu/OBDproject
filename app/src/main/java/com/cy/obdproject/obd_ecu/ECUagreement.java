package com.cy.obdproject.obd_ecu;

public class ECUagreement {

    /**
     * @param canLinkNum 1个字节
     * @param canId      4个字节
     * @param length     2个字节(长度)
     * @param data
     * @return
     */
    public static String a(String canLinkNum, String canId, String length, String data) {
        String string = 75 + "" + OBDagreement.numPlus() + "" + canLinkNum + "" + canId + "" + length + "" + data;
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

}
