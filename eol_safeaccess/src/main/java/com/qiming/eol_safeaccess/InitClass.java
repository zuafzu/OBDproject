package com.qiming.eol_safeaccess;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class InitClass {

    private com.qiming.eol_public.InitClass publicUnit;
    public void setPublicUnit(com.qiming.eol_public.InitClass publicUnit){
        this.publicUnit = publicUnit;
    }

    public String CalcKey(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String seed = jo.optString("SEED");
            Log.e("cyf","seed : "+seed);
            jsonObject.put("DATA", StringTools.byte2hex(_GetKey(StringTools.hex2byte(seed))));
            Log.e("cyf","seed2 : "+StringTools.byte2hex(_GetKey(StringTools.hex2byte(seed))));
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息："+e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private byte[] _GetKey(byte[] seed) {
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
                wort = wort ^ 0x000000004a68795bL;
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

}
