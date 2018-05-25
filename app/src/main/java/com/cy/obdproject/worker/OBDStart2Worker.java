package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.OBDagreement;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.StringTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OBDStart2Worker {

    private List<String> myData = new ArrayList<>();
    private final int timeOut = 3000;// 超时时间
    private Activity activity;
    private SocketCallBack socketCallBack;
    private String msg = "";
    private String key = "";
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
                    putData("连接超时");
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
        if (msg.length() > 8) {
            key = msg.substring(6, 8);
        }
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
            for (int i = 0; i < myData.size(); i++) {
                String mKey = myData.get(i).substring(6, 8);
                int length = Integer.parseInt(Integer.parseInt(myData.get(i).substring(2, 4), 16) + ""
                        + Integer.parseInt(myData.get(i).substring(4, 6), 16));
                if (mKey.equals(key) && length == ((myData.get(i).length() - 8) / 2) && myData.get(i).endsWith("00AA")) {
                    break;
                } else {
                    myData.remove(i);
                    i--;
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
        OBDStart2Worker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OBDStart2Worker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                msg = OBDagreement.a("10", "05", "0c");
                if (replay()) {
                    return;
                }
                msg = OBDagreement.b("10", "0007a120");
                if (replay()) {
                    return;
                }
                msg = OBDagreement.c("10", "F5400000", "00000000");
                if (replay()) {
                    return;
                }
                msg = OBDagreement.d("10", "000007A2", "000007AA");
                if (replay()) {
                    return;
                }
                msg = OBDagreement.e("10", "00");
                if (replay()) {
                    return;
                }
                msg = OBDagreement.f("10", "00");
                if (replay()) {
                    return;
                }
                putData("连接成功");
            }
        }).start();
    }

}
