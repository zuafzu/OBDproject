package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.ErrorCodeBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.constant.ECUConstant;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorCodeWorker {

    private List<ErrorCodeBean> errorCodeBeanList;

    private List<String> myData = new ArrayList<>();
    private final int timeOut = 3000;// 超时时间
    private Activity activity;
    private SocketCallBack socketCallBack;
    private String msg = "";
    private MySocketClient.ConnectLinstener connectLinstener;
    private Long sysTime1 = 0L;
    private Long sysTime2 = 0L;
    private Handler handler;
    private Runnable runnable;

    public void init(Activity activity, SocketCallBack socketCallBack) {
        this.activity = activity;
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
                myData.add(data);
            }
        };
    }

    public void start() {
        if (SocketService.Companion.getIntance() != null &&
                SocketService.Companion.getIntance().isConnected() &&
                SocketService.Companion.isConnected()) {
            next();
        } else {
            putData("OBD未连接");
        }
    }

    private boolean replay() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        if (SocketService.Companion.getIntance() != null &&
                SocketService.Companion.getIntance().isConnected() &&
                SocketService.Companion.isConnected()) {
            Log.e("cyf", "发送信息 : " + msg + "  ");
            SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
            startTime();
        } else {
            putData("OBD连接断开，请重新启动软件");
            return true;
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
                mmsg = ECUTools.getData(myData.get(i), 1, msg);
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
        ErrorCodeWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorCodeWorker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        // 更改canId和reCanId
        ECUagreement.canId = "000007A2";
        ECUagreement.reCanId = "000007AA";

        new Thread(new Runnable() {
            @Override
            public void run() {
                msg = ECUagreement.a("190201");
                if (replay()) {
                    return;
                }
                String data = ECUTools.getData(myData.get(0), 1, msg);
                errorCodeBeanList = new ArrayList<>();
                for (int i = 0; i < data.length() / 8; i++) {
                    // 550010760010000007EB0007590201 11111122 AA
                    String a = data.substring(i * 8, ((i + 1) * 8) - 2);
                    String item = StringTools.hexStrToBinaryStr(a);
                    if (item.length() < 24) {
                        for (int j = 0; j < 24 - item.length(); j++) {
                            item = "0" + item;
                        }
                    }
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
                    String endKey = StringTools.binaryStrToHexStr("00" + b);
                    String code = starKey + endKey;
                    List<ErrorCodeBean> list = ECUConstant.getErrorCodeData();
                    boolean flag = true;
                    for (int j = 0; j < list.size(); j++) {
                        if (code.toLowerCase().equals(list.get(j).getCode().toLowerCase())) {
                            errorCodeBeanList.add(list.get(j));
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        errorCodeBeanList.add(new ErrorCodeBean(code, "无效故障代码"));
                    }
                }
                putData(new Gson().toJson(errorCodeBeanList));
            }
        }).start();
    }

}
