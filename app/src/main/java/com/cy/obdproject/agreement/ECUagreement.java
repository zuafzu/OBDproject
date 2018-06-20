package com.cy.obdproject.agreement;

public class ECUagreement {

    public static String canLinkNum = "10"; // 1个字节
    public static String canId = "000007A2"; // 4个字节（发送的CanId）

    public static String reCanId = "000007AA"; // 4个字节（返回的CanId）

    /**
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
