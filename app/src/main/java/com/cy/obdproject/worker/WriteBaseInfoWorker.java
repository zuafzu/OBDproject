package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.BaseInfoBean;
import com.cy.obdproject.bean.SocketBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteBaseInfoWorker {

    private SocketBean socketBean;
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

    public void init(Activity activity, SocketBean socketBean,SocketCallBack socketCallBack) {
        this.activity = activity;
        this.socketBean = socketBean;
        this.socketCallBack = socketCallBack;
        handler = new Handler();
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
                    String msg = ECUTools.getData(data,
                            WriteBaseInfoWorker.this.socketBean.getType(),
                            WriteBaseInfoWorker.this.socketBean.getKey());
                    if (msg.equals(ECUTools.ERR)) {
                        putData("返回数据异常");
                    } else if (msg.equals(ECUTools.WAIT)) {
                        startTime();
                    } else {
                        BaseInfoBean baseInfoBean = new BaseInfoBean();
                        baseInfoBean.setName(WriteBaseInfoWorker.this.socketBean.getName());
                        baseInfoBean.setValue(msg);
                        baseInfoBeanList.add(baseInfoBean);
                        index++;
                        next();
                    }
                } else {
                    putData("返回数据超时");
                }
            }
        };
    }

    public void start() {
        baseInfoBeanList.clear();
        index = 0;
        next();
    }

    private void replay() {
        Log.e("cyf", "发送信息 : " + msg + "  " + index);
        SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
        startTime();
    }

    private void startTime() {
        sysTime1 = new Date().getTime();
        sysTime2 = 0L;
        handler.postDelayed(runnable, timeOut);
    }

    private void putData(final String msg) {
        WriteBaseInfoWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WriteBaseInfoWorker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        if(index == 0){

        }else if(index == 1){

        }else if(index == 3){

        }else if(index == 4){
            msg = ECUagreement.a(socketBean.getCanLinkNum(),
                    socketBean.getCanId(),
                    socketBean.getLength(),
                    socketBean.getData());
            replay();
        }else {
            putData(new Gson().toJson(baseInfoBeanList));
        }
    }

}