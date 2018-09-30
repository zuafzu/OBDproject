package com.qiming.eol_scriptrunner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Command {

    public int iLineNo;
    public String strFileName;
    public String strName;
    public JSONObject Input = new JSONObject();
    public JSONObject Output = new JSONObject();
    public int iIfSuccessGoto;
    public int iIfFaultGoto;
    public int ErrorPro = 0;// 1弹出对话框阻塞，2输出到界面（红色字体），3记录到日志，0初始值
    public String cmdLine = "";
    private String strReturnKey = "";

    public String InitOutputParams(JSONObject val) {
        String strKey = "";
        if (strReturnKey.length() == 0) {
            Iterator iterator = Output.keys();
            while (iterator.hasNext()) {
                strKey = iterator.next() + "";
                strReturnKey = strKey;
                break;
            }
        } else {
            strKey = strReturnKey;
        }
        Output = val;
        return strKey;
    }

    public String GetInputParams(JSONObject Public) {
        JSONObject tmpParam = new JSONObject();
        try {
            Iterator iterator = Input.keys();
            while (iterator.hasNext()) {
                String key = iterator.next() + "";
                String value = Input.optString(key);

                StringBuilder strValue = new StringBuilder();
                String[] strTmps = value.split("`");

                for (String strTmp : strTmps) {
                    if (!strTmp.contains("&")) {
                        strValue.append(strTmp);
                    } else {
                        strValue.append(strTmp.substring(0, strTmp.indexOf("&")));
                        String tmp = strTmp.substring(strTmp.indexOf("&") + 1);

                        if (!tmp.contains("$")) {
                            strValue.append(Public.optString(tmp));
                        } else {
                            String strTmpKey = tmp.substring(0, tmp.indexOf("$"));
                            tmp = tmp.substring(tmp.indexOf("$") + 1);
                            strValue.append(Public.getJSONObject(strTmpKey).optString(tmp));
                        }
                    }
                }
                tmpParam.put(key, strValue.toString());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmpParam.toString();
    }


}
