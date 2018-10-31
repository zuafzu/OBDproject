package com.qiming.eol_scriptrunner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.qiming.eol_public.InitClass;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Script {


    private int miCmdCount = 0;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Command> mCmds = new HashMap<>();

    private boolean isRun = false;// 当前脚本是否运行

    private ScriptManager manager;
    private String scriptName = "";
    private Context context;

    public Script() {
        super();
    }

    Script(ScriptManager manager, String scriptName, Context context) {
        super();
        this.manager = manager;
        this.scriptName = scriptName;
        this.context = context;
    }

    public boolean LoadCodeSeg(String strSingleLine) {
        try {
            String[] strItems = strSingleLine.split("~");
            Command Cmd = new Command();
            Cmd.cmdLine = strSingleLine;
            Cmd.iLineNo = Integer.parseInt(strItems[0]);
            Cmd.strFileName = strItems[1];
            Cmd.strName = strItems[2];
            Cmd.Input = new JSONObject(strItems[3]);
            Cmd.Output = new JSONObject(strItems[4]);
            Cmd.iIfSuccessGoto = Integer.parseInt(strItems[5]);
            Cmd.iIfFaultGoto = Integer.parseInt(strItems[6]);
            if (strItems.length == 8) {
                Cmd.ErrorPro = Integer.parseInt(strItems[7]);
            }
            mCmds.put(miCmdCount++, Cmd);
        } catch (Exception e) {
            LogTools.errLog(e);
            return false;
        }
        return true;
    }


    public boolean Run() {
        isRun = true;
        int iIP = 0;
        while (isRun) {
            try {
                writeLog(scriptName + " -- " + "开始循环1" + "\n");
                if (iIP >= miCmdCount || iIP < 0) {
                    isRun = false;
                    manager.setBreak(true);
                    break;
                }
                writeLog(scriptName + " -- " + "开始循环2" + "\n");
                String strFucName = mCmds.get(iIP).strName;
                String strFileName = mCmds.get(iIP).strFileName;
                writeLog(scriptName + " -- " + mCmds.get(iIP).cmdLine + "\n");
                //从全局变量中，初始化入口
                String strInput = mCmds.get(iIP).GetInputParams(manager.mEnviment);
                writeLog(scriptName + " -- " + "传入参数：" + strInput + "\n");
                String strOutput = "";
                strOutput = EolAPI.CALL(manager.PublicUnit, strFileName, strFucName, strInput);
                writeLog(scriptName + " -- " + "传出参数：" + strOutput + "\n");
                // 返回给全局变量
                JSONObject jsonOut = new JSONObject(strOutput);
                String key = mCmds.get(iIP).InitOutputParams(jsonOut);
                if (strFileName.equals("EOL_ExtendFuction") && strFucName.equals("SetGlobalVariable")) {
                    manager.mEnviment.put(key, mCmds.get(iIP).Output.optString("VALUE"));
                } else {
                    manager.mEnviment.put(key, mCmds.get(iIP).Output);
                }
                // Log.i("cyf", "当前iIP : " + iIP);
                writeLog(scriptName + " -- " + "当前iIP：" + iIP + "\n");
                // 根据返回结果跳转
                if (jsonOut.get("RESULT").equals("SUCCESS")) {
                    iIP = mCmds.get(iIP).iIfSuccessGoto;
                    Log.e("cyf", "成功跳转iIP : " + iIP);
                } else {
                    showError(mCmds.get(iIP));
                    iIP = mCmds.get(iIP).iIfFaultGoto;

                    manager.setMiLastErrorNo(iIP);
                    manager.setMstrLastErrorMsg("执行第" + iIP + "行失败，DESC：" + jsonOut.get("DESC"));
                    Log.e("cyf", "失败跳转iIP : " + iIP);
                    Log.e("cyf", "DESC : " + jsonOut.get("DESC"));
                }
                writeLog(scriptName + " -- " + "跳转iIP：" + iIP + "\n");
            } catch (Exception e) {
                LogTools.errLog(e);
                manager.setMiLastErrorNo(iIP);
                manager.setMstrLastErrorMsg(e.getMessage());

                showError(mCmds.get(iIP));
                iIP = mCmds.get(iIP).iIfFaultGoto;
                writeLog(scriptName + " -- " + "错误跳转iIP：" + iIP + "\n");
            }
        }
        return true;
    }

    private void writeLog(String str) {
        // 顺序日志
        if (ScriptManager.isOrderLog) {
            String filePath = InitClass.pathShunxu;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Date date = new Date(System.currentTimeMillis());
            String fileName = simpleDateFormat.format(date) + "log.txt";
            FileUtil.writeTxtToFile(str, filePath, fileName);
        }
    }

    private void showError(Command command) {
        // 1弹出对话框阻塞，2输出到界面（红色字体），3记录到日志，0初始值
        final int iLineNo = command.iLineNo;
        final String DESC = command.Output.optString("DESC");
        switch (command.ErrorPro) {
            case 1:
                if (manager.getHandler() != null) {
                    Message message = new Message();
                    message.obj = "第" + iLineNo + "行\n" + DESC;
                    manager.getHandler().sendMessage(message);
                    manager.setBreak(false);
                } else {
                    manager.setBreak(true);
                }
                // manager.setBreak(false);
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (manager.isBreak()) {
                        break;
                    }
                }
                break;
            case 2:
                try {
//                    String libPath = Environment.getExternalStorageDirectory() + File.separator + "eol_message.jar"; // 要动态加载的jar
//                    File dexDir = activity.getDir("dex", MODE_PRIVATE); // 优化后dex的路径
//                    /**
//                     * 进行动态加载，利用java的反射调用com.test.dynamic.MyClass的方法
//                     */
//                    DexClassLoader classLoader = new DexClassLoader(libPath, dexDir.getAbsolutePath(), null, activity.getClassLoader());
//                    Class<Object> cls = (Class<Object>) classLoader.loadClass("com.qiming.eol_message.InitClass");

                    manager.PublicUnit.AddMessage2("第" + iLineNo + "行错误：" + DESC);
                } catch (Exception e) {
                    LogTools.errLog(e);
                }
                break;
            case 3:
                String filePath = InitClass.pathCuowu;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS");
                Date date = new Date(System.currentTimeMillis());
                String fileName = simpleDateFormat.format(date) + "log.txt";
                FileUtil.writeTxtToFile(simpleDateFormat2.format(date) + "  " + iLineNo + "  " + DESC, filePath, fileName);
                break;
            default:
                break;
        }
    }

    public void stopRun() {
        isRun = false;
    }

    public boolean isRun() {
        return isRun;
    }

}
