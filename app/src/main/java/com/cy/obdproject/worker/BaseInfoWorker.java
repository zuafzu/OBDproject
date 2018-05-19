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

public class BaseInfoWorker {

    private List<SocketBean> socketBeanList;
    private List<BaseInfoBean> baseInfoBeanList = new ArrayList<>();

    private List<String> myData = new ArrayList<>();
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

    public void init(Activity activity, List<SocketBean> socketBeanList, SocketCallBack socketCallBack) {
        this.activity = activity;
        this.socketBeanList = socketBeanList;
        this.socketCallBack = socketCallBack;
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
                myData.add(data);
            }
        };
    }

    public void start() {
        baseInfoBeanList.clear();
        index = 0;
        if (SocketService.Companion.getIntance() != null && SocketService.Companion.getIntance().isConnected()) {
            next();
        } else {
            putData("OBD未连接");
        }
    }

    private boolean replay() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        Log.e("cyf", "发送信息 : " + msg + "  ");
        if (SocketService.Companion.getIntance() != null && SocketService.Companion.getIntance().isConnected()) {
            SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
            startTime();
        }
        return sleep() || checkData();
    }

    private boolean sleep() {
        while (myData.size() == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sysTime2 = new Date().getTime();
            if (sysTime2 - sysTime1 > timeOut) {
                putData("返回数据超时");
                return true;
            }
        }
        return false;
    }

    private void startTime() {
        sysTime1 = new Date().getTime();
        sysTime2 = 0L;
        handler.postDelayed(runnable, timeOut);
    }

    private boolean checkData() {
        handler.removeCallbacks(runnable);
        sysTime2 = new Date().getTime();
        if (sysTime2 - sysTime1 <= timeOut) {
            String mmsg = "";
            for (int i = 0; i < myData.size(); i++) {
                mmsg = ECUTools.getData(myData.get(i), socketBeanList.get(index).getType(), msg);
                if (mmsg.equals(ECUTools.ERR)) {
                    myData.remove(i);
                    i--;
                } else if (mmsg.equals(ECUTools.WAIT)) {
                    myData.remove(i);
                    startTime();
                    return sleep() || checkData();
                } else {
                    break;
                }
            }
            if (myData.size() == 0) {
                putData("返回数据异常");
                return true;
            }
        } else {
            putData("返回数据超时");
            return true;
        }
        return false;
    }

    private void putData(final String msg) {
        BaseInfoWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseInfoWorker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (index < socketBeanList.size()) {
                    msg = ECUagreement.a(socketBeanList.get(index).getData());
                    if (replay()) {
                        return;
                    }
                    String mmsg = ECUTools.getData(myData.get(0), socketBeanList.get(index).getType(), msg);
                    BaseInfoBean baseInfoBean = new BaseInfoBean();
                    baseInfoBean.setName(BaseInfoWorker.this.socketBeanList.get(index).getName());
                    baseInfoBean.setValue(mmsg);
                    baseInfoBeanList.add(baseInfoBean);
                    index++;
                    next();
                } else {
                    putData(new Gson().toJson(baseInfoBeanList));
                }
            }
        }).start();
    }

}
