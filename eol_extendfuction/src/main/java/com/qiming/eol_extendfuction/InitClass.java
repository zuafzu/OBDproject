package com.qiming.eol_extendfuction;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitClass {

    private com.qiming.eol_public.InitClass publicUnit;
    public void setPublicUnit(com.qiming.eol_public.InitClass publicUnit){
        this.publicUnit = publicUnit;
    }

    public  String GetSubString(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String string = jo.optString("DATA");
            int START = Integer.valueOf(jo.optString("START"));
            int LEN = Integer.valueOf(jo.optString("LEN"));

            jsonObject.put("RESULT", "SUCCESS");
            if (LEN == 0) {
                jsonObject.put("DATA", string.substring(START, string.length()));
            } else {
                jsonObject.put("DATA", string.substring(START, START + LEN));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public  String GetDate(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String FORMAT = jo.optString("FORMAT");
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String string = sdf.format(date);
            if (FORMAT.equals("3")) {
                string = string.substring(2, string.length());
            } else if (FORMAT.equals("4")) {

            }
            jsonObject.put("DATA", string);
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public  String SetGlobalVariable(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            jsonObject.put("KEY", jo.optString("KEY"));
            jsonObject.put("VALUE", jo.optString("VALUE"));
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public  String ParserDtcExtendStateBitInfo(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String TYPE = jo.optString("TYPE");
            String DATA = jo.optString("DATA");
            int STARTPOS = Integer.valueOf(jo.optString("STARTPOS"));
            int SPLIT_DTCEACHLEN = Integer.valueOf(jo.optString("SPLIT_DTCEACHLEN"));
            String DTCBASEPATH = jo.optString("DTCBASEPATH");
            int DTC_START_BIT = Integer.valueOf(jo.optString("DTC_START_BIT"));
            int DTC_LEN_BIT = Integer.valueOf(jo.optString("DTC_LEN_BIT"));

            // 读取xml文件
            File file = new File(com.qiming.eol_public.InitClass.pathMokuai + "/" + DTCBASEPATH);
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            // String res = EncodingUtils.getString(buffer, "UTF-8");
            fis.close();
            // 解析xml文件
            List<Map<String, String>> mapList = new ArrayList<>();
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(new ByteArrayInputStream(buffer), "UTF-8");
            int event = pullParser.getEventType();
            String type = "";
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        // 开始
                        break;
                    case XmlPullParser.START_TAG:
                        if ("DTCS".equals(pullParser.getName())) {
                            type = pullParser.getAttributeValue(0);
                        }
                        if ("PBCU".equals(type)) {
                            if ("DTC".equals(pullParser.getName())) {
                                Map<String, String> map = new HashMap<>();
                                map.put(pullParser.getAttributeValue(0).toLowerCase(), pullParser.getAttributeValue(1));
                                mapList.add(map);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        // 结束
                        break;
                }
                event = pullParser.next();
            }

            JSONArray jsonArray = new JSONArray();
            if (TYPE.equals("PBCU")) {
                DATA = DATA.substring(STARTPOS * 2, jo.optString("DATA").length());
                for (int i = 0; i < DATA.length() / (SPLIT_DTCEACHLEN * 2); i++) {
                    String a = DATA.substring(i * SPLIT_DTCEACHLEN * 2, ((i + 1) * SPLIT_DTCEACHLEN * 2));
                    String item = StringTools.hexStrToBinaryStr(a);
                    if (item.length() < SPLIT_DTCEACHLEN * 8) {
                        for (int j = 0; j < SPLIT_DTCEACHLEN * 8 - item.length(); j++) {
                            item = "0" + item;
                        }
                    }
                    item = item.substring(DTC_START_BIT, DTC_LEN_BIT);
                    String starKey = item.substring(0, 2);
                    switch (starKey) {
                        case "00":
                            starKey = "P";
                            break;
                        case "01":
                            starKey = "C";
                            break;
                        case "10":
                            starKey = "B";
                            break;
                        case "11":
                            starKey = "U";
                            break;
                    }
                    String b = item.substring(2, item.length());
                    if (b.length() < DTC_LEN_BIT) {
                        int c = (DTC_LEN_BIT - b.length());
                        for (int j = 0; j < c; j++) {
                            b = "0" + b;
                        }
                    }
                    String endKey = StringTools.binaryStrToHexStr(b);
                    String code = starKey + endKey;
                    JSONObject object = new JSONObject();
                    object.put(code, "无效故障代码");
                    // 匹配故障信息
                    for (int j = 0; j < mapList.size(); j++) {
                        if (mapList.get(j).containsKey(code.toLowerCase())) {
                            object.put(code, mapList.get(j).get(code.toLowerCase()));
                            object.put("data", a.substring(0, 6));
                        }
                    }
                    jsonArray.put(object);
                }
            }
            jsonObject.put("DATA", jsonArray.toString());
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

}
