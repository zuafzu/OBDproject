package com.qiming.eol_public;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InitClass {

    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/红旗故障诊断";
    public static final String pathApk = path + "/APK";
    public static final String pathMokuai = path + "/模块包";
    public static final String pathLiucheng = path + "/流程文件";
    public static final String pathShuaxie = path + "/刷写文件";
    private static final String path2 = path + "/日志";
    public static final String pathShunxu = path2 + "/顺序日志/";
    public static final String pathTongxun = path2 + "/通讯日志/";
    public static final String pathCuowu = path2 + "/错误日志/";

    public static class ClassUnit {
        public ClassUnit() {
        }

        public Class<?> clazz = null;
        public Object Instance = null;
    }

    private Map<String, ClassUnit> stringClassMap = new HashMap<>();
    public Context context = null;

    public void SetMap(String key, ClassUnit obj) {
        if (stringClassMap.containsKey(key) == false) {
            stringClassMap.put(key, obj);
        }
    }

    public void setMessageHandler(Handler handler) {
        try {
            Object o = stringClassMap.get("eol_message").Instance;


            Method m = stringClassMap.get("eol_message").clazz.getMethod("setHandler", Handler.class);


            m.invoke(o, handler);
        } catch (Exception e) {

        }
    }

    public boolean ScriptManagerLoadScript(String path) {
        boolean result = false;
        try {
            Object o = stringClassMap.get("eol_scriptrunner").Instance;

            Method m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("LoadScript", String.class);

            result = (boolean) m.invoke(o, path);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e);
        }

        return result;
    }

    public boolean ScriptManagerLoadScript(InputStream instream) {
        boolean result = false;
        try {

            Object o = stringClassMap.get("eol_scriptrunner").Instance;

            Method m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("LoadScript", InputStream.class);

            result = (boolean) m.invoke(o, instream);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e);
        }

        return result;
    }

    public void setScriptManagerParam(boolean isOrderLog, boolean isImLog, String DstName, int dstPort) {
        try {
            Object o = stringClassMap.get("eol_scriptrunner").Instance;

            Field f1 = stringClassMap.get("eol_scriptrunner").clazz.getField("isOrderLog");

            f1.set(o, isOrderLog);

            Field f2 = stringClassMap.get("eol_scriptrunner").clazz.getField("isImLog");

            f2.set(o, isImLog);

            Field f3 = stringClassMap.get("eol_scriptrunner").clazz.getField("mDstName");

            f3.set(o, DstName);

            Field f4 = stringClassMap.get("eol_scriptrunner").clazz.getField("dstPort");

            f4.set(o, dstPort);

        } catch (Exception e) {
        }
    }

    public void setBreak(boolean isBreak) {
        try {
            Object o = stringClassMap.get("eol_scriptrunner").Instance;

            Method m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("setBreak", boolean.class);

            m.invoke(o, isBreak);
        } catch (Exception e) {
        }
    }

    public void InitDynamicData(Handler Success, String Items) {
        try {

            Object o = stringClassMap.get("eol_message").Instance;

            Method m = stringClassMap.get("eol_message").clazz.getMethod("SetReadLiveItems", String.class);

            m.invoke(o, Items);

            m = stringClassMap.get("eol_message").clazz.getMethod("setHandler", Handler.class);

            m.invoke(o, Success);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetReadFreezeItems(Handler Success, String Items) {
        try {

            Object o = stringClassMap.get("eol_message").Instance;

            Method m = stringClassMap.get("eol_message").clazz.getMethod("SetReadFreezeItems", String.class);

            m.invoke(o, Items);

            m = stringClassMap.get("eol_message").clazz.getMethod("setHandler", Handler.class);

            m.invoke(o, Success);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetEvent(Handler h, String event) {
        try {

            Object o = stringClassMap.get("eol_scriptrunner").Instance;


            Method m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("setHandler", Handler.class);

            m.invoke(o, h);

            m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("Run", String.class);

            m.invoke(o, event);

        } catch (Exception e) {
            Log.e("cyf", "e: " + e.getMessage());
        }
    }

    public boolean GetScriptIsRun(String event) {
        try {

            Object o = stringClassMap.get("eol_scriptrunner").Instance;


            Method m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("isRun", String.class);

            boolean r = (boolean) m.invoke(o, event);

            return r;
        } catch (Exception e) {
        }

        return false;
    }

    public String GetUI() {
        try {
            Object o = stringClassMap.get("eol_scriptrunner").Instance;

            Field f1 = stringClassMap.get("eol_scriptrunner").clazz.getField("uiData");

            return (String) f1.get(o);
        } catch (Exception e) {
        }

        return "";
    }

    public void SetPublicUnit(String key, InitClass publicUnit) {
        try {

            Object o = stringClassMap.get(key).Instance;


            Method m = stringClassMap.get(key).clazz.getMethod("setPublicUnit", InitClass.class);

            m.invoke(o, publicUnit);

        } catch (Exception e) {
        }

    }

    public String CALL(String key, String FunName, String param) {
        try {
            Object o = stringClassMap.get(key).Instance;


            Method m = stringClassMap.get(key).clazz.getMethod(FunName, String.class);

            return (String) m.invoke(o, param);
        } catch (Exception e) {
        }
        return "";
    }


    public void AddMessage2(String message) {
        try {

            Object o = stringClassMap.get("eol_message").Instance;


            Method m = stringClassMap.get("eol_message").clazz.getMethod("AddMessage2", String.class);

            m.invoke(o, message);

        } catch (Exception e) {
        }

    }

    public void setAllBytesData(byte[] allBytesData) {
        try {

            Object o = stringClassMap.get("eol_producefileparser").Instance;

            Method m = stringClassMap.get("eol_producefileparser").clazz.getMethod("setAllBytesData", byte[].class);

            m.invoke(o, allBytesData);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e);
        }
    }

    public byte[] getBytesData() {
        byte[] result = null;

        try {

            Object o = stringClassMap.get("eol_producefileparser").Instance;


            Method m = stringClassMap.get("eol_producefileparser").clazz.getMethod("getBytesData");

            result = (byte[]) m.invoke(o);

        } catch (Exception e) {
            Log.e("cyf", "e : " + e.getMessage());
        }


        return result;
    }


    public void SetFlashPos(int pos) {
        try {

            Object o = stringClassMap.get("eol_message").Instance;


            Method m = stringClassMap.get("eol_message").clazz.getMethod("SetFlashPos", int.class);

            m.invoke(o, pos);

        } catch (Exception e) {
            Log.e("cyf", "e : " + e.getMessage());
        }


    }


    public void SetBaseInfo(String info) {
        try {

            Object o = stringClassMap.get("eol_message").Instance;


            Method m = stringClassMap.get("eol_message").clazz.getMethod("SetBaseInfo", String.class);

            m.invoke(o, info);

        } catch (Exception e) {
        }


    }


    public void SetLiveInfo(String info) {
        try {

            Object o = stringClassMap.get("eol_message").Instance;


            Method m = stringClassMap.get("eol_message").clazz.getMethod("SetLiveInfo", String.class);

            m.invoke(o, info);

        } catch (Exception e) {
        }


    }

    public void stopRun() {
        try {

            Object o = stringClassMap.get("eol_scriptrunner").Instance;

            Method m = stringClassMap.get("eol_scriptrunner").clazz.getMethod("stopRun", (Class<?>) null);

            m.invoke(o, (Object) null);

        } catch (Exception e) {
        }


    }

    public String getIp() {
        String result = null;
        try {
            Field f3 = stringClassMap.get("eol_scriptrunner").clazz.getField("mDstName");
            result = (String) f3.get(stringClassMap.get("eol_scriptrunner").clazz);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e.getMessage());
        }
        return result;
    }


    public int getPort() {
        int result = -1;
        try {
            Field f4 = stringClassMap.get("eol_scriptrunner").clazz.getField("dstPort");
            result = f4.getInt(stringClassMap.get("eol_scriptrunner").clazz);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e.getMessage());
        }
        return result;
    }

    public boolean isImLog() {
        boolean result = false;
        try {
            Field f4 = stringClassMap.get("eol_scriptrunner").clazz.getField("isImLog");
            result = f4.getBoolean(stringClassMap.get("eol_scriptrunner").clazz);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e.getMessage());
        }
        return result;
    }

    public boolean isOrderLog() {
        boolean result = false;
        try {
            Field f4 = stringClassMap.get("eol_scriptrunner").clazz.getField("isOrderLog");
            result = f4.getBoolean(stringClassMap.get("eol_scriptrunner").clazz);
        } catch (Exception e) {
            Log.e("cyf", "e : " + e.getMessage());
        }
        return result;
    }

}
