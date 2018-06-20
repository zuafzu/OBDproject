package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.activity.DynamicData2Activity;
import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.DynamicDataBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DynamicDataWorker {

    private CopyOnWriteArrayList<DynamicDataBean> dynamicDataBeans = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DynamicDataBean> dynamicDataBeans2 = new CopyOnWriteArrayList<>();

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

    public void init(Activity activity, CopyOnWriteArrayList<DynamicDataBean> dynamicDataBeans, SocketCallBack socketCallBack) {
        this.activity = activity;
        this.dynamicDataBeans = dynamicDataBeans;
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

    public void start(CopyOnWriteArrayList<DynamicDataBean> dynamicDataBeans) {
        this.dynamicDataBeans2 = dynamicDataBeans;
        index = 0;
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
            try {
                myData.remove(0);
            } catch (Exception e) {
                Log.e("cyf99", "e : " + e.getMessage());
            }
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
                if (myData.size() > i && dynamicDataBeans.size() > index) {
                    mmsg = ECUTools.getData(myData.get(i), dynamicDataBeans.get(index).getParsingType(), msg);
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
        DynamicDataWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DynamicDataWorker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        if (DynamicData2Activity.Companion.isStart()) {
            return;
        }
        // 更改canId和reCanId
        ECUagreement.canId = "000007A2";
        ECUagreement.reCanId = "000007AA";

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (index < dynamicDataBeans.size()) {
                    if (dynamicDataBeans.get(index).getIsSelect().equals("1")) {
                        for (int i = 0; i < dynamicDataBeans2.size(); i++) {
                            if (dynamicDataBeans.get(index).getId().equals(dynamicDataBeans2.get(i).getId())) {
                                msg = ECUagreement.a(dynamicDataBeans.get(index).getId());
                                if (replay()) {
                                    return;
                                }
                                String value = "无数据";
                                try {
                                    // String mmsg = ECUTools.getData(myData.get(0), dynamicDataBeans.get(index).getParsingType(), msg);
                                    String mmsg = ECUTools.getData(myData.get(0), 1, msg);
                                    if (!mmsg.isEmpty()) {
                                        int a;
                                        if (dynamicDataBeans.get(index).getOffset().contains("-")) {
                                            a = (int) (Long.valueOf(mmsg, 16)
                                                    * Double.valueOf(dynamicDataBeans.get(index).getCoefficient())
                                                    - Double.valueOf(dynamicDataBeans.get(index).getOffset().replace("-", "")));
                                        } else {
                                            a = (int) (Long.valueOf(mmsg, 16)
                                                    * Double.valueOf(dynamicDataBeans.get(index).getCoefficient())
                                                    + Double.valueOf(dynamicDataBeans.get(index).getOffset()));
                                        }
                                        value = a + "";
                                        if (!dynamicDataBeans.get(index).getEnumValue().isEmpty()) {
                                            String[] strs = dynamicDataBeans.get(index).getEnumValue().split("#");
                                            value = strs[a].split("\\^")[1];
                                        }
                                    }
                                    if (dynamicDataBeans.size() > index) {
                                        dynamicDataBeans.get(index).setValue(value + " " + dynamicDataBeans.get(index).getUnit());
                                    }
                                } catch (Exception e) {
                                    index = -1;
                                    Log.e("cyf99", "e : " + e.getMessage());
                                }
                                break;
                            }
                        }
                    }
                    index++;
                    if (!DynamicData2Activity.Companion.isStart()) {
                        next();
                    } else {
                        Log.e("cyf99", "已经停止");
                    }
                } else {
                    putData(new Gson().toJson(dynamicDataBeans));
                }
            }
        }).start();
    }

}
