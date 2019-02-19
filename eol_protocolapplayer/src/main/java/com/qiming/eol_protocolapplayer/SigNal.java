package com.qiming.eol_protocolapplayer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SigNal {

    private String id;

    private String name;

    private String value;

    private String note;

    private String reserved1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    @Override
    public String toString() {
        return "SigNal [id=" + id + ", name=" + name + ", value=" + value + ", note=" + note + ", reserved1=" + reserved1 + "]";
    }

    public static SigNal Pase4Service(ParseBean bean, String content/*��ȥ��0x62*/) {
        SigNal sigNal = new SigNal();
        sigNal.setId(bean.getId());
        sigNal.setName(bean.getNameZh());
        int iByte_Begin = bean.getBeginPosition();
        int iByte_EffLength = bean.getLength();
        int len_PID = 4;
        String strHexValue = content.substring(len_PID + iByte_Begin * 2, len_PID + iByte_Begin * 2 + iByte_EffLength * 2); // �ֽڽ�ȡ

        if (ParseTypeEnum.EnumType.equals(bean.getParseType())) {
            // int iIntConValue = ProtocolUtil.converHex8StrToInt(strHexValue);

            int iBit_Start = bean.getBitStart();
            int iBit_EffLength = bean.getBitLength();
            int iIntValue = (int) tools_HexString2UnsignedLong(strHexValue);
            if (iByte_EffLength <= 1) {
                iIntValue = tools_CutBit(iIntValue, iBit_Start, iBit_EffLength);
            }

            String enumDesc = bean.getEnumDesc();
            String[] arr_enumDesc = enumDesc.split("#");
            Map<String, String> map_enumDesc = new HashMap<String, String>();
            for (String string : arr_enumDesc) {
                String[] temp = string.split("\\^");
                map_enumDesc.put(temp[0], temp[1]);
            }
            if (map_enumDesc.get(iIntValue + "") == null) {
                //System.out.println(ProtclParseException.Type.ParseType_EX + "(" + iIntValue + ")");
            }
            String strUnit = bean.getUnit();
            if (strUnit.equals("-") || strUnit.equals(" ") || strUnit == null) {
                strUnit = "";
            }
            // [G.P.S]�޸Ķ�̬����-ö������ Ϊ ö��ֵ+��:��+��������
            String val = iIntValue + ":" + map_enumDesc.get(String.valueOf(iIntValue)) + " " + strUnit;
            sigNal.setValue(val);
        } else if (ParseTypeEnum.StringType.equals(bean.getParseType())) {
            String s = tools_hexString2AsciiString(strHexValue);
            if(!s.equals("")){
                String unit = bean.getUnit();
                if (unit == null) {
                    sigNal.setValue(s.trim());
                } else {
                    if (unit.equals("-") || unit.equals(" ")) {
                        unit = "";
                    }
                    sigNal.setValue(s.trim() + unit);
                }
            }
        } else if (ParseTypeEnum.BCDType.equals(bean.getParseType())) {
            sigNal.setValue(strHexValue);
        } else if (ParseTypeEnum.SignedIntType.equals(bean.getParseType())) {
            int iIntValue = converHex8StrToInt(strHexValue);
            String strUnit = bean.getUnit();
            if (strUnit.equals("-") || strUnit.equals(" ") || strUnit == null) {
                strUnit = "";
            }
            sigNal.setValue(tools_ToBigMath(((float) iIntValue * (float) bean.getCoefficient() + bean.getOffsets()), 0) + strUnit);
        } else if (ParseTypeEnum.UnsignedIntType.equals(bean.getParseType())) {
            String strUnit = bean.getUnit();
            if (strUnit.equals("-") || strUnit.equals(" ") || strUnit == null) {
                strUnit = "";
            }
            long lValue = tools_HexString2UnsignedLong(strHexValue);// ProtocolUtil.converHex8StrToLong(strHexValue);
            sigNal.setValue(tools_ToBigMath(((float) lValue * (float) bean.getCoefficient() + bean.getOffsets()), 0) + strUnit);
        } else if (ParseTypeEnum.SignedFloatType.equals(bean.getParseType())) {
            String strUnit = bean.getUnit();
            if (strUnit.equals("-") || strUnit.equals(" ") || strUnit == null) {
                strUnit = "";
            }
            float fValue = converHex8StrToLong(strHexValue);
            sigNal.setValue(tools_ToBigMath(((float) fValue * (float) bean.getCoefficient() + bean.getOffsets())) + strUnit);
        } else if (ParseTypeEnum.UnsignedFloatType.equals(bean.getParseType())) {
            String strUnit = bean.getUnit();
            if (strUnit.equals("-") || strUnit.equals(" ") || strUnit == null) {
                strUnit = "";
            }
            double dValue = tools_HexString2UnsignedLong(strHexValue);// ProtocolUtil.converHex8StrToLong(strHexValue);
            sigNal.setValue(tools_ToBigMath(((float) dValue * (float) bean.getCoefficient() + bean.getOffsets())) + strUnit);
        } else if (ParseTypeEnum.HEXType.equals(bean.getParseType())) {
            sigNal.setValue(strHexValue);
        } else {

        }
        return sigNal;

    }

    private static String tools_ToBigMath(double dData) {
        return tools_ToBigMath(dData, 3);
    }

    private static long tools_HexString2UnsignedLong(String data) {
        int iStrLen = data.length();

        if (iStrLen <= 2) {
            return ((byte) Integer.valueOf(data, 16).byteValue()) & 0x0000000FF;// ȥ����
        } else if (iStrLen <= 4) {
            return Integer.valueOf(data, 16) & 0x000000FFFF;// ȥ����
        } else if (iStrLen <= 8) {
            return Integer.valueOf(data, 16) & 0x0FFFFFFFFL;// ȥ����
        }
        return 0;
    }

    private static int tools_CutBit(int iIntValue, int iBit_Start, int iBit_EffLength) {
        if (iBit_Start + iBit_EffLength > 8) {
            return iIntValue;
        }
        int iIntValue_Temp = iIntValue << (8 - iBit_Start - iBit_EffLength); // ��ͷ
        iIntValue = iIntValue_Temp >> (8 - iBit_EffLength); //ȥβ
        switch (iBit_EffLength) {
            case 1:
                iIntValue = iIntValue & 0x01;
                break;
            case 2:
                iIntValue = iIntValue & 0x03;
                break;
            case 3:
                iIntValue = iIntValue & 0x07;
                break;
            case 4:
                iIntValue = iIntValue & 0x0F;
                break;
            case 5:
                iIntValue = iIntValue & 0x1F;
                break;
            case 6:
                iIntValue = iIntValue & 0x3F;
                break;
            case 7:
                iIntValue = iIntValue & 0x7F;
                break;
            case 8:
                iIntValue = iIntValue & 0xFF;
                break;
        }
        return iIntValue;
    }

    private static String tools_hexString2AsciiString(String hex) {
        if (hex == null) {
            return "null";
        }

        int len = hex.length();
        String ascii = "";
        for (int i = 0; i < len; i += 2) {
            if (i + 2 <= len) {
                String s = hex.substring(i, i + 2);
                int n = Integer.parseInt(s, 16);
                if (n >= 0x20 && n <= 0x7e) {
                    ascii += (char) n;
                }else{
                    return "";
                }
            }
        }

        return ascii;
    }

    private static int converHex8StrToInt(String hex) {
        if (hex.contains("0x")) {
            hex = hex.replace("0x", "");
        }
        if (hex.contains("0X")) {
            hex = hex.replace("0X", "");
        }
        return converByte4ToInt(hexString2Bytes(hex));
    }

    private static int converByte4ToInt(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    private static byte[] hexString2Bytes(String src) {
        src = makeUpHexString8(src);
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }


    private static String makeUpHexString8(String str) {
        if (str.length() < 8) {
            String hex = "";
            for (int i = 0; i < 8 - str.length(); i++) {
                hex = hex + "0";
            }
            return hex + str;
        }
        return str;
    }

    private static String tools_ToBigMath(double dData, int iDceimalPlace) {
        BigDecimal d1 = new BigDecimal(Double.toString(dData));
        BigDecimal d2 = new BigDecimal(Double.toString(1));
        return d1.divide(d2, iDceimalPlace, BigDecimal.ROUND_HALF_UP).toString();
    }

    private static long converHex8StrToLong(String hex) {
        return converByte4ToLong(hexString2Bytes(hex));
    }


    private static long converByte4ToLong(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        long n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

}
