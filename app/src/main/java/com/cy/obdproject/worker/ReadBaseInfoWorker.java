package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.BaseInfoBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadBaseInfoWorker {

    private String name = "";
    private String key = "";
    private int type = 1;
    private List<BaseInfoBean> baseInfoBeanList = new ArrayList<>();

    private final int timeOut = 3000;// 超时时间
    private Activity activity;
    private SocketCallBack socketCallBack;
    private String msg = "";
    private int index = 0;
    private MySocketClient.ConnectLinstener connectLinstener;
    private Long sysTime1 = 0L;
    private Long sysTime2 = 0L;
    private Handler handler;
    private Runnable runnable;

    public void start() {
        index = 0;
        next();
    }

    public void start(Activity activity, SocketCallBack socketCallBack) {
        this.activity = activity;
        this.socketCallBack = socketCallBack;
        index = 0;
        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (sysTime2 == 0) {
                    putData("返回数据超时");
                }
            }
        };
        connectLinstener = new MySocketClient.ConnectLinstener() {
            @Override
            public void onReceiveData(String data) {
                handler.removeCallbacks(runnable);
                sysTime2 = new Date().getTime();
                if (sysTime2 - sysTime1 <= timeOut) {
                    String msg = ECUTools.getData(data, type, key);
                    if (msg.equals(ECUTools.ERR)) {
                        putData("返回数据异常");
                        return;
                    }
                    BaseInfoBean baseInfoBean = new BaseInfoBean();
                    baseInfoBean.setName(name);
                    baseInfoBean.setValue(msg);
                    baseInfoBeanList.add(baseInfoBean);
                    next();
                } else {
                    putData("返回数据超时");
                }
            }
        };
        next();
    }

    private void replay() {
        Log.e("cyf", "发送信息 : " + msg);
        SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
        sysTime1 = new Date().getTime();
        sysTime2 = 0L;
        handler.postDelayed(runnable, timeOut);
    }

    private void putData(final String msg) {
        index = 0;
        ReadBaseInfoWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ReadBaseInfoWorker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        msg = "";
        switch (index) {
            case 0:
                name = "vin：";
                key = "62F190";
                type = 3;
                msg = ECUagreement.a("10", "18da00fa", "0003", "22F190");
                replay();
                break;
            case 1:
                name = "硬件版本号：";
                key = "62F1A6";
                type = 3;
                msg = ECUagreement.a("10", "18da00fa", "0003", "22F1A6");
                replay();
                break;
            case 2:
                name = "软件版本号：";
                key = "62F1A5";
                type = 3;
                msg = ECUagreement.a("10", "18da00fa", "0003", "22F1A5");
                replay();
                break;
            default:
                putData(new Gson().toJson(baseInfoBeanList));
                break;
        }
        index++;
    }

}
