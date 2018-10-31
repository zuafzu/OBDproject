package com.qiming.eol_scriptrunner;

import org.json.JSONException;
import org.json.JSONObject;

public class EolAPI {

    public static String CALL(com.qiming.eol_public.InitClass publicUnit, String strDllPath, String strFucName, String strInput) {
        String data = "";
        try {





            data = publicUnit.CALL(strDllPath.toLowerCase().replace(".dll",""),strFucName,strInput);
        } catch (Exception e) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "jar包模块异常");
                data = jsonObject.toString();
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
            LogTools.errLog(e);
        }

        return data;
    }

}
