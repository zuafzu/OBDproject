package com.qiming.eol_extendfuction;

import android.annotation.SuppressLint;
import android.util.Log;

import com.qiming.eol_public.InitClass;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogTools {

    public static void errLog(Exception e) {
        e.printStackTrace();
        String filePath = InitClass.pathCuowu;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        // @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS");
        Date date = new Date(System.currentTimeMillis());
        String fileName = simpleDateFormat.format(date) + "log.txt";
        FileUtil.writeTxtToFile(e.getMessage(), filePath, fileName);
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            FileUtil.writeTxtToFile(sw.toString(), filePath, fileName);
            Log.i("cyf", "LogTools 错误提示：\n" + sw.toString());
        } catch (Exception e2) {
            LogTools.errLog(e2);
        }
    }

}
