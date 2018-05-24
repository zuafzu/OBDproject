package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.WriteFileBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteDataWorker {

    private ArrayList<WriteFileBean> writeFileBeans;

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

    public void start(ArrayList<WriteFileBean> writeFileBeans) {
        this.writeFileBeans = writeFileBeans;
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
        WriteDataWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WriteDataWorker.this.socketCallBack.getData(msg);
            }
        });
    }

    private void next() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                msg = ECUagreement.a("1003");
//                if (replay()) {
//                    return;
//                }
//                msg = ECUagreement.a("2701");
//                if (replay()) {
//                    return;
//                }
//                String data = myData.get(0);
//                msg = ECUagreement.a("2702" +
//                        StringTools.byte2hex(ECUTools._GetKey(StringTools.hex2byte(ECUTools.getData2(data, 1, msg)))));
//                if (replay()) {
//                    return;
//                }
//                SocketService.Companion.getIntance().sendMsg(writeFileBeans.get(0).getData(),connectLinstener);
//                msg = ECUagreement.a(socketBean.getData());
//                if (replay()) {
//                    return;
//                }
//                putData("刷写成功");
                // 1、发送7DF  指令1003 长度2 不需要接收
                msg = ECUagreement.a("1003");
                // 2、发送7DF  指令 长度2  只发送不需要接收
                msg = ECUagreement.a("3E80");
                // 3、发送7DF  指令8502 长度2  只发送不需要接收
                msg = ECUagreement.a("8502");
                // 4、发送7DF  指令 长度3  只发送不需要接收
                msg = ECUagreement.a("280303");
                // 5、发送7A2 指令1002 长度2 返回 7AA 5002
                msg = ECUagreement.a("1002");
                if (replay()){
                    return;
                }
                //6、发送7A2 指令2703   长度2  返回  7AA 6703   四个字节seed（随机产生，给算法，经运算后下一步发送）
                msg = ECUagreement.a("2703");
                if (replay()){
                    return;
                }
                //7、发送7A2 指令2704+seed运算后的数值   长度6   返回 7AA 6704
                msg = ECUagreement.a("2704" +"seed的运算数值");
                if (replay()){
                    return;
                }
                //8、CANID 7A2   指令 2EF19831323334353637383900     长度 13    返回 7AA  6EF198
                msg = ECUagreement.a("2EF19831323334353637383900");
                if (replay()){
                    return;
                }
                //9、CANID 7A2   指令2EF199   长度 7 返回 7AA  6EF199
                msg = ECUagreement.a("2EF199");
                if (replay()){
                    return;
                }
                //10、CANID 7A2   指令340044 00000000(EOl中的addr) 000003B2(eol中的len)   长度 11 返回 7AA  74...
                msg = ECUagreement.a("34004400000000000003B2");
                if (replay()){
                    return;
                }
                //11、CANID 7A2  指令 3601 + eol中的数据（从00开始到946字节） 长度948  返回 7AA 7601
                msg = ECUagreement.a("3601" +" eol中的数据（从00开始到946字节）");
                if (replay()){
                    return;
                }
                //12、CANID 7A2 指令 37  长度1 返回 77
                msg = ECUagreement.a("37");
                if (replay()){
                    return;
                }
                //13、CANID 7A2 指令 310102026E2ECDCD（一次性校验）  长度8 返回 71
                msg = ECUagreement.a("310102026E2ECDCD");
                if (replay()){
                    return;
                }
                //14、CANID 7A2 指令 3101FF0044 00FE0000(addr) 0009F230(len)(这条指令是数据擦除) 返回7AA 7101FF
                msg = ECUagreement.a("3101FF004400FE00000009F230");
                if (replay()){
                    return;
                }
                //15、CANID 7A2 指令

         }
        }).start();
    }

}
