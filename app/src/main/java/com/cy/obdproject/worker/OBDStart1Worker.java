package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.OBDagreement;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.StringTools;

import java.util.Date;

public class OBDStart1Worker {

    private final int timeOut = 3000;// 超时时间
    private Activity activity;
    private SocketCallBack socketCallBack;
    private String msg = "";
    private String key = "";
    private int index = 0;
    private MySocketClient.ConnectLinstener connectLinstener;
    private Long sysTime1 = 0L;
    private Long sysTime2 = 0L;
    private Handler handler;
    private Runnable runnable;

    public void init(Activity activity, SocketCallBack socketCallBack) {
        this.activity = activity;
        this.socketCallBack = socketCallBack;
        handler = new android.os.Handler();
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
                handler.removeCallbacks(runnable);
                sysTime2 = new Date().getTime();
                if (sysTime2 - sysTime1 <= timeOut) {
                    String mKey = data.substring(6, 8);
                    int length = Integer.parseInt(Integer.parseInt(data.substring(2, 4), 16) + ""
                            + Integer.parseInt(data.substring(4, 6), 16));
                    if (mKey.equals(key) && length == ((data.length() - 8) / 2) && data.endsWith("00AA")) {
                        next();
                    } else {
                        putData("连接异常");
                    }
                } else {
                    putData("连接超时");
                }
            }
        };
    }

    public void start(){
        next();
    }

    private void replay() {
        Log.e("cyf", "发送信息 : " + msg);
        if(SocketService.Companion.getIntance()!=null && SocketService.Companion.getIntance().isConnected()){
            SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
            sysTime1 = new Date().getTime();
            sysTime2 = 0L;
            handler.postDelayed(runnable, timeOut);
        }else{
            putData("OBD未连接");
        }
    }

    private void putData(final String msg) {
        index = 0;
        OBDStart1Worker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OBDStart1Worker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        msg = "";
        switch (index) {
            case 0:
                msg = OBDagreement.a("10", "05", "0c");
                replay();
                break;
            case 1:
                msg = OBDagreement.b("10", "0007a120");
                replay();
                break;
            case 2:
                msg = OBDagreement.c("10", "FC600000", "01000000");
                replay();
                break;
            case 3:
                msg = OBDagreement.d("10", "000007E3", "000007EB");
                replay();
                break;
            case 4:
                msg = OBDagreement.e("10", "00");
                replay();
                break;
            case 5:
                msg = OBDagreement.f("10", "00");
                replay();
                break;
            default:
                putData("连接成功");
                break;
        }
        if (msg.length() > 8) {
            key = msg.substring(6, 8);
        }
        index++;
    }

}
