package com.qiming.eol_scriptrunner;

import android.os.Handler;

import com.qiming.eol_public.InitClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dalvik.system.DexClassLoader;

import static android.content.Context.MODE_PRIVATE;

public class ScriptManager {

    public static boolean isOrderLog = false;// 是否开启顺序日志
    public static boolean isImLog = false;// 是否开启通讯日志

    public static String mDstName = "";// socket的ip地址
    public static int dstPort = -1;// socket的端口号

    public static String uiData = "";

    private Handler handler;// 错误信息
    private boolean isBreak = false;// 是否跳出错误等待dialog的循环

    private String mstrLastErrorMsg = "";
    private int miLastErrorNo = 0;

    public JSONObject mEnviment = new JSONObject();
    private Map<String, Script> list = new HashMap<>();

    com.qiming.eol_public.InitClass PublicUnit = null;

    public void setPublicUnit(com.qiming.eol_public.InitClass PublicUnit) {
        this.PublicUnit = PublicUnit;
    }

    // 构造方法
    public ScriptManager() {
    }


    public String getMstrLastErrorMsg() {
        return mstrLastErrorMsg;
    }

    public void setMstrLastErrorMsg(String mstrLastErrorMsg) {
        this.mstrLastErrorMsg = mstrLastErrorMsg;
    }

    public int getMiLastErrorNo() {
        return miLastErrorNo;
    }

    public void setMiLastErrorNo(int miLastErrorNo) {
        this.miLastErrorNo = miLastErrorNo;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    public void Run(String name) {
        new Thread(new RunnableWithParams(list.get(name), new RunnableWithParams.IRunnableWithParams() {

            @Override
            public void Run(Object UserParams) {
                if (((Script) UserParams) != null) {
                    ((Script) UserParams).Run();
                }
            }
        })).start();
    }

    private void LoadUISeg(String strSingleLine) {
        uiData = strSingleLine;
    }

    private boolean LoadDataSeg(String strSingleLine) {
        try {
            JSONObject obj = new JSONObject(strSingleLine);
            Iterator iterator = obj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next() + "";
                mEnviment.put(key, obj.optString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean LoadScript(String strFileFullName) {
        try {
            File file = new File(InitClass.pathLiucheng + "/" + strFileFullName);
            InputStream instream = new FileInputStream(file);
            LoadScript(instream);
        } catch (Exception e) {
            setMstrLastErrorMsg(e.getMessage());
//            if (context != null) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, "没有找到指定文件", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
            return false;
        }
        return true;
    }

    public boolean LoadScript(InputStream instream) {
        try {
            String strParserType = "";
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String strSingleLine;
            Script script = null;
            String name = "";
            //分行读取
            while ((strSingleLine = buffreader.readLine()) != null) {
                // 去掉前后空格，并且去掉非法字符
                strSingleLine = strSingleLine.trim().replace("\uFEFF", "");
                if (strSingleLine.length() == 0) {
                    continue;
                }
                if (strSingleLine.equals("UI_BEGIN")) {
                    strParserType = strSingleLine;
                    continue;
                }
                if (strSingleLine.equals("UI_END")) {
                    strParserType = strSingleLine;
                    continue;
                }
                if (strSingleLine.equals("FUN_BEGIN")) {
                    strParserType = strSingleLine;
                    continue;
                }
                if (strSingleLine.equals("FUN_END")) {
                    strParserType = strSingleLine;
                    continue;
                }
                if (strSingleLine.equals("DATA_BEGIN")) {
                    strParserType = strSingleLine;
                    continue;
                }
                if (strSingleLine.equals("DATA_END")) {
                    strParserType = strSingleLine;
                    continue;
                }
                if (strSingleLine.indexOf("CODE_BEGIN") >= 0) {
                    String[] tmp = strSingleLine.split(" ");
                    name = tmp[1];
                    script = new Script(this, name, PublicUnit.context);
                    strParserType = "CODE_BEGIN";
                    continue;
                }
                if (strSingleLine.indexOf("CODE_END") >= 0) {
                    list.put(name, script);
                    strParserType = "CODE_END";
                    continue;
                }
                if (strParserType.equals("UI_BEGIN")) {
                    LoadUISeg(strSingleLine);
                }
                if (strParserType.equals("FUN_BEGIN")) {
                    String libPath = InitClass.pathMokuai + "/" + strSingleLine.toLowerCase() + ".jar"; // 要动态加载的jar
                    File dexDir = PublicUnit.context.getDir("dex", MODE_PRIVATE); // 优化后dex的路径
                    /**
                     * 进行动态加载，利用java的反射调用com.test.dynamic.MyClass的方法
                     */
                    DexClassLoader classLoader = new DexClassLoader(libPath, dexDir.getAbsolutePath(), null, PublicUnit.context.getClassLoader());
                    Class<Object> cls = (Class<Object>) classLoader.loadClass("com.qiming." + strSingleLine.toLowerCase() + ".InitClass");
                    Object object = cls.newInstance();

                    com.qiming.eol_public.InitClass.ClassUnit unit = new com.qiming.eol_public.InitClass.ClassUnit();
                    unit.clazz = cls;
                    unit.Instance = object;
                    PublicUnit.SetMap(strSingleLine.toLowerCase().replace(".dll", ""), unit);

                    Method method = cls.getMethod("setPublicUnit", com.qiming.eol_public.InitClass.class);

                    method.invoke(object, PublicUnit);


                }
                if (strParserType.equals("DATA_BEGIN")) {
                    LoadDataSeg(strSingleLine);
                }
                if (strParserType.equals("CODE_BEGIN")) {
                    script.LoadCodeSeg(strSingleLine);
                }
            }
            instream.close();
        } catch (Exception e) {
            setMstrLastErrorMsg(e.getMessage());
//            if (context != null) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, "没有找到指定文件", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
            return false;
        }
        return true;
    }

    public boolean isRun(String name) {
        if (list.get(name) == null) {
            return false;
        }
        return list.get(name).isRun();
    }

    public void stopRun(String name) {
        if (list.get(name) != null) {
            list.get(name).stopRun();
        }
    }

    public void stopRun() {
        // 全体停止
        for (String key : list.keySet()) {
            if (list.get(key) != null) {
                list.get(key).stopRun();
            }
        }
    }

}
