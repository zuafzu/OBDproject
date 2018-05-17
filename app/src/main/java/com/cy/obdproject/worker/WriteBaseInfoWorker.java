package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.SocketBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;

import java.util.Date;

public class WriteBaseInfoWorker {

    private SocketBean socketBean;

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

    private String key = "";

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
                        if (index == 1) {

                            // key = ECUTools._GetKey()
                        }
                        index++;
                        next();
                    }
                } else {
                    putData("返回数据超时");
                }
            }
        };
    }

    public void start(SocketBean socketBean) {
        this.socketBean = socketBean;
        index = 0;
        next();
    }

    private void replay() {
        Log.e("cyf", "发送信息 : " + msg + "  " + index);
        if (SocketService.Companion.getIntance() != null && SocketService.Companion.getIntance().isConnected()) {
            SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
            startTime();
        } else {
            putData("OBD未连接");
        }
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
        if (index == 0) {
            msg = ECUagreement.a("10", "000007E3", "0002", "1003");
        } else if (index == 1) {
            msg = ECUagreement.a("10", "000007E3", "0002", "2701");
        } else if (index == 2) {
            msg = ECUagreement.a("10", "000007E3", "0006", "2702" + key);
        } else if (index == 3) {
            msg = ECUagreement.a(socketBean.getCanLinkNum(),
                    socketBean.getCanId(),
                    socketBean.getLength(),
                    socketBean.getData());
            replay();
        } else {
            putData("0");
        }
    }

}
