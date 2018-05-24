package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.bean.WriteFileBean;
import com.cy.obdproject.callback.SocketCallBack;
import com.cy.obdproject.socket.MySocketClient;
import com.cy.obdproject.socket.SocketService;
import com.cy.obdproject.tools.ByteTools;
import com.cy.obdproject.tools.ECU2Tools;
import com.cy.obdproject.tools.ECUTools;
import com.cy.obdproject.tools.StringTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteDataWorker {

    private ArrayList<WriteFileBean> writeFileBeans;

    private List<String> myData = new ArrayList<>();
    private int timeOut = 3000;// 超时时间
    private Activity activity;
    private SocketCallBack socketCallBack;
    private String msg = "";
    private byte[] bytes;
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

    private boolean replay2() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        if (bytes == null) {
            putData("刷写文件数据异常！");
            return true;
        }
        if (SocketService.Companion.getIntance() != null && SocketService.Companion.getIntance().isConnected()) {
            SocketService.Companion.getIntance().sendMsg(bytes, connectLinstener);
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
                // 更改canId和reCanId
                ECUagreement.canId = "000007DF";
                ECUagreement.reCanId = "000007AA";
                // 1、发送7DF  指令1003 长度2 不需要接收
                msg = ECUagreement.a("1003");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 2、发送7DF  指令 长度2  只发送不需要接收
                msg = ECUagreement.a("3E80");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 3、发送7DF  指令8502 长度2  只发送不需要接收
                msg = ECUagreement.a("8502");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 4、发送7DF  指令 长度3  只发送不需要接收
                msg = ECUagreement.a("280303");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                ECUagreement.canId = "000007A2";
                // 5、发送7A2 指令1002 长度2 返回 7AA 5002
                msg = ECUagreement.a("1002");
                if (replay()) {
                    return;
                }
                //6、发送7A2 指令2703   长度2  返回  7AA 6703   四个字节seed（随机产生，给算法，经运算后下一步发送）
                msg = ECUagreement.a("2703");
                if (replay()) {
                    return;
                }
                String data = myData.get(0);
                //7、发送7A2 指令2704+seed运算后的数值   长度6   返回 7AA 6704
                msg = ECUagreement.a("2704" +
                        StringTools.byte2hex(ECU2Tools.getBootKey(StringTools.hex2byte(ECUTools.getData2(data, 1, msg)))));
                if (replay()) {
                    return;
                }
                //8、CANID 7A2   指令 2EF19831323334353637383900     长度 13    返回 7AA  6EF198
                msg = ECUagreement.a("2EF19831323334353637383900");
                if (replay()) {
                    return;
                }
                //9、CANID 7A2   指令2EF199   长度 7 返回 7AA  6EF199
                msg = ECUagreement.a("2EF19920180521");
                if (replay()) {
                    return;
                }
                int index = 0;
                while (writeFileBeans.size() > index) {
                    if (index != 0) {
                        //10、CANID 7A2 指令 3101FF0044 00FE0000(addr) 0009F230(len)(这条指令是数据擦除) 返回7AA 7101FF
                        msg = ECUagreement.a("3101FF0044" + writeFileBeans.get(index).getAddress() +
                                String.format("%08x", Integer.valueOf(writeFileBeans.get(index).getLength())));
                        if (replay()) {
                            return;
                        }
                    }
                    //11、CANID 7A2   指令340044 00000000(EOl中的addr) 000003B2(eol中的len)   长度 11 返回 7AA  74...
                    msg = ECUagreement.a("340044" + writeFileBeans.get(index).getAddress() +
                            String.format("%08x", Integer.valueOf(writeFileBeans.get(index).getLength())));
                    if (replay()) {
                        return;
                    }
                    //12、刷写数据，暂定1024一组
                    int index2 = 0;
                    int total = writeFileBeans.get(index).getData().length / 1024;
                    if (writeFileBeans.get(index).getData().length % 1024 != 0) {
                        total = total + 1;
                    }
                    while (total > index2) {
                        String xx = String.format("%02x", index2 % 255 + 1);
                        int byteLength = writeFileBeans.get(index).getData().length - (index2 * 1024);
                        if (byteLength < 1024) {
                            bytes = ByteTools.byteMerger(StringTools.hex2byte(ECUagreement.a("36" + xx)),
                                    ByteTools.subBytes(writeFileBeans.get(index).getData(), index2 * 1024, byteLength));
                        } else {
                            bytes = ByteTools.byteMerger(StringTools.hex2byte(ECUagreement.a("36" + xx)),
                                    ByteTools.subBytes(writeFileBeans.get(index).getData(), index2 * 1024, 1024));
                        }
                        if (replay2()) {
                            return;
                        }
                        index2++;
                    }
                    //13、CANID 7A2 指令 37  长度1 返回 77
                    msg = ECUagreement.a("37");
                    if (replay()) {
                        return;
                    }
                    //14、CANID 7A2 指令 310102026E2ECDCD（一次性校验）  长度8 返回 71
                    msg = ECUagreement.a("31010202" + ECU2Tools.Make_CRC(writeFileBeans.get(index).getData()));
                    if (replay()) {
                        return;
                    }
                    index++;
                }
                // 15、一致性校验
                msg = ECUagreement.a("3101ff01");
                if (replay()) {
                    return;
                }
                // 16、物理寻址11 01（等待4秒）
                msg = ECUagreement.a("1101");
                timeOut = 4000;
                if (replay()) {
                    return;
                }
                timeOut = 3000;
                ECUagreement.canId = "000007DF";
                // 17、发送7DF  指令1003 长度2 不需要接收
                msg = ECUagreement.a("1003");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 18、发送7DF  指令280003 长度3  只发送不需要接收
                msg = ECUagreement.a("280003");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 19、发送7DF  指令8501 长度2  只发送不需要接收
                msg = ECUagreement.a("8501");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 20、发送7DF  指令1001 长度2  只发送不需要接收
                msg = ECUagreement.a("1001");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                ECUagreement.canId = "000007A2";
                // 21、物理寻址 14 FF FF FF
                msg = ECUagreement.a("14FFFFFF");
                if (replay()) {
                    return;
                }
                putData("刷写完成");
            }
        }).start();
    }

}
