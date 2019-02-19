package com.qiming.eol_safeaccess;

import org.json.JSONException;
import org.json.JSONObject;

public class InitClass {

    private com.qiming.eol_public.InitClass publicUnit;

    public void setPublicUnit(com.qiming.eol_public.InitClass publicUnit) {
        this.publicUnit = publicUnit;
    }

    public String CalcKeyForBoot(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String seed = jo.optString("SEED");
            String type = jo.optString("CAR_TYPE");
            long mask = 0;
            String data = "";
            switch (type) {
                case "EV_HCU":
                    mask = 0x000000004a68795bL;
                    data = StringTools.byte2hex(_GetKey(mask, StringTools.hex2byte(seed)));
                    break;

                case "EV_DSCU":
                    mask = 0x00000000a28ee118L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_EPS":
                    mask = 0x000000006C5F40BB;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_PLGM":
                    mask = 0x00000000ab59ef31L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_SCCU":
                    mask = 0x00000000a703395dL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_AC":
                    mask = 0x00000000ad7adfaaL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_ACP-F(高配)":
                    mask = 0x00000000AF9534FBL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_ACP-F(低配)":
                    mask = 0x00000000AF9534FBL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_ACPF":
                    mask = 0x00000000AF9534FBL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_ADV":
                    mask = 0x00000000D39A6ED2L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_BMS":
                    mask = 0x0000000059DB7D73L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_OBC":
                    mask = 0x0000000061AF6E20L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_DCDC":
                    mask = 0x000000005DDA5C9CL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_PDCU":
                    mask = 0x000000009BA336A8L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_RLDCU":
                    mask = 0x000000009DFA64A8L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_DDCU":
                    mask = 0x000000009942E26BL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_RRDCU":
                    mask = 0x00000000A048D307L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_HUD":
                    mask = 0x00000000A28EE118L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_MCU":
                    mask = 0x0000000055ACF799L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_T-Box":
                    mask = 0x00000000B9BD8A2DL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_AVM":
                    mask = 0x00000000877710E9L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_ESP":
                    mask = 0x0000000072F229D0L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "EV_BCM1":
                    mask = 0x000000008e9bbd8eL;
                    data = StringTools.byte2hex(_GetKey(mask, StringTools.hex2byte(seed)));
                    break;
                case "EV_BCM2":
                    mask = 0x000000008eacbd9fL;
                    data = StringTools.byte2hex(_GetKey(mask, StringTools.hex2byte(seed)));
                    break;

                //----------------
                case "HS5_EPS":
                    mask = 0x000000006C5F40BB;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_ACU":
                    mask = 0x0000000076188246;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_TCU":
                    mask = 0x000000002650b89bL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_SW":
                    mask = 0x000000006c5f40bbL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_CDC":
                    mask = 0x000000006fb51729L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                //add by zxy

                case "HS5_EMS":
                    mask = 0x000000001B17D02FL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;


                case "HS5_EGSM":
                    mask = 0x00000000362FA05EL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_ACM":
                    mask = 0x000000003C94F328L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_ESP_APB":
                    mask = 0x0000000072F229D0L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;


                case "HS5_AC":
                    mask = 0x00000000AD7ADFAAL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_SCM":
                    mask = 0x0000000096D8F940L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_DSCU":
                    mask = 0x00000000A28EE118L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_SCCU":
                    mask = 0x00000000A703395DL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_BCM1":
                    mask = 0x0000000091E67F4AL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_BCM2":
                    mask = 0x0000000091E67F4AL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_HUD":
                    mask = 0x00000000BBB4B937L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;


                case "HS5_AVS":
                    mask = 0x00000000877710E9L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_PDC":
                    mask = 0x0000000084BA681AL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_IFC":
                    mask = 0x000000007F13D8FCL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_T_BOX":
                    mask = 0x00000000B9BD8A2DL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_DDCU":
                    mask = 0x000000009942E26BL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_PDCU":
                    mask = 0x000000009BA336A8L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

                case "HS5_RLDCU":
                    mask = 0x000000009DFA64A8L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_RRDCU":
                    mask = 0x00000000A048D307L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;


                case "HS5_GW":
                    mask = 0x00000000BDA6B146L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_ADV":
                    mask = 0x00000000D39A6ED2L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_ALS":
                    mask = 0x000000008CC78AE9L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_4WD":
                    mask = 0x0000000047AE6FD9L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_DMSCU":
                    mask = 0x000000004CA17136L;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;
                case "HS5_LCDA":
                    mask = 0x0000000081EEFDDCL;
                    data = StringTools.byte2hex(_GetBootKey(mask, Long.valueOf(seed, 16)));
                    break;

            }
            if (mask == 0) {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：无对应电控安全算法。");
                return jsonObject.toString();
            }
            jsonObject.put("DATA", data);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    private static byte[] _GetKey(long mask, byte[] seed) {
        long wort = 0;
        wort = (long) (seed[0] & 0xFF);
        wort = wort << 8;
        wort = wort | (long) (seed[1] & 0xFF);
        wort = wort << 8;
        wort = wort | (long) (seed[2] & 0xFF);
        wort = wort << 8;
        wort = wort | (long) (seed[3] & 0xFF);
        for (int i = 0; i < 35; i++) {
            if ((wort & 0x0000000080000000L) > 0) {
                wort = wort << 1;
                wort = wort ^ mask;
            } else {
                wort = wort << 1;
            }
        }
        byte[] key = new byte[4];
        key[0] = (byte) (wort >> 24);
        key[1] = (byte) (wort >> 16);
        key[2] = (byte) (wort >> 8);
        key[3] = (byte) (wort >> 0);
        return key;
    }

    public static byte[] _GetBootKey(long iMASK, Long iwSeed) {
        long MASK = iMASK;
        long wSeed = getUnsignedIntt(iwSeed);
        long iterations;
        long wLastSeed;
        long wTemp;
        long wLSBit;
        long wTop31Bits;
        long jj, SB1, SB2, SB3;
        long temp;
        wLastSeed = wSeed;
        temp = getUnsignedIntt((int) ((((MASK & 0x00000800) >> 10) | ((MASK & 0x00200000) >> 21))));
        if (temp == 0) {
            wTemp = getUnsignedIntt((int) ((wSeed & 0xff000000) >> 24));
        } else if (temp == 1) {
            wTemp = getUnsignedIntt((int) ((wSeed & 0x00ff0000) >> 16));
        } else if (temp == 2) {
            wTemp = getUnsignedIntt((int) ((wSeed & 0x0000ff00) >> 8));
        } else {
            wTemp = getUnsignedIntt((int) (wSeed & 0x000000ff));
        }
        SB1 = getUnsignedIntt((int) ((MASK & 0x000003FC) >> 2));
        SB2 = getUnsignedIntt((int) (((MASK & 0x7F800000) >> 23) ^ 0xA5));
        SB3 = getUnsignedIntt((int) (((MASK & 0x001FE000) >> 13) ^ 0x5A));
        iterations = (long) (((wTemp ^ SB1) & SB2) + SB3);
        for (jj = 0; jj < iterations; jj++) {
            wTemp = ((wLastSeed & 0x40000000) / 0x40000000) ^ ((wLastSeed & 0x01000000) / 0x01000000) ^ ((wLastSeed & 0x1000) / 0x1000) ^ ((wLastSeed & 0x04) / 0x04);
            wLSBit = (wTemp & 0x00000001);
            wLastSeed = getUnsignedIntt((int) (wLastSeed << 1)); /* Left Shift the bits */
            wTop31Bits = getUnsignedIntt((int) (wLastSeed & 0xFFFFFFFE));
            wLastSeed = getUnsignedIntt((int) (wTop31Bits | wLSBit));
        }

        if ((MASK & 0x00000001) != 0) {
            wTop31Bits = ((wLastSeed & 0x00FF0000) >> 16) | ((wLastSeed & 0xFF000000) >> 8) | ((wLastSeed & 0x000000FF) << 8) | ((wLastSeed & 0x0000FF00) << 16);
        } else
            wTop31Bits = wLastSeed;

        wTop31Bits = (wTop31Bits ^ MASK);
        byte[] keys = new byte[4];
        keys[3] = (byte) wTop31Bits;
        keys[2] = (byte) (wTop31Bits >> 8);
        keys[1] = (byte) (wTop31Bits >> 16);
        keys[0] = (byte) (wTop31Bits >> 24);
        //String strResult = bytesToHexFun1(keys);
        return keys;
    }

    public static long getUnsignedIntt(Long data) {     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFFl;
    }

    public static long getUnsignedIntt(int data) {     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFFl;
    }


}