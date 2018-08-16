package com.cy.obdproject.worker;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.agreement.OBDagreement;
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
    private int timeOut = 6000;// 超时时间
    private Activity activity;
    private SocketCallBack socketCallBack;
    private String msg = "";
    private String key = "";
    private MySocketClient.ConnectLinstener connectLinstener;
    private Long sysTime1 = 0L;
    private Long sysTime2 = 0L;
    private Handler handler;
    private Runnable runnable;

    private int updateNum = 0;

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

    private boolean replay2() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        if (msg.length() > 8) {
            key = msg.substring(6, 8);
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
        return sleep() || checkData2();
    }

    private boolean sleep() {
        while (myData.size() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sysTime2 = new Date().getTime();
            if (sysTime2 - sysTime1 > timeOut) {
                if (timeOut != 500) {
                    putData("返回数据超时");
                }
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
                if (timeOut != 500) {
                    putData("返回数据异常");
                }
                return true;
            }
        } else {
            if (timeOut != 500) {
                putData("返回数据超时");
            }
            return true;
        }
        return false;
    }

    private boolean checkData2() {
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
                if (timeOut != 500) {
                    putData("返回数据异常");
                }
                return true;
            }
        } else {
            if (timeOut != 500) {
                putData("返回数据超时");
            }
            return true;
        }
        return false;
    }

    private void putData(final String mmsg) {
        if (mmsg.equals("返回数据异常") || mmsg.equals("返回数据超时")) {
            // stop 3e
            msg = OBDagreement.h();
            if (SocketService.Companion.getIntance() != null &&
                    SocketService.Companion.getIntance().isConnected() &&
                    SocketService.Companion.isConnected()) {
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
            }
        }
        WriteDataWorker.this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WriteDataWorker.this.socketCallBack.getData(mmsg);
            }
        });
    }

    private void next() {
        updateNum = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 更改canId和reCanId
                timeOut = 500;
                ECUagreement.canId = "000007DF";
                ECUagreement.reCanId = "000007AA";
                // 1、发送7DF  指令1003 长度2 不需要接收
                msg = ECUagreement.a("1003");
                Log.e("cyf", "发送信息 : " + msg + "  ");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 2、发送7DF  指令 长度2  只发送不需要接收
                msg = ECUagreement.a("3E80");
                Log.e("cyf", "发送信息 : " + msg + "  ");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                // 3、发送7DF  指令8502 长度2  只发送不需要接收
                msg = ECUagreement.a("8502");
                Log.e("cyf", "发送信息 : " + msg + "  ");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 4、发送7DF  指令 长度3  只发送不需要接收
                msg = ECUagreement.a("280303");
                Log.e("cyf", "发送信息 : " + msg + "  ");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 4-2、start 3e
                msg = OBDagreement.g();
                if (replay2()) {
                }
                // 更改canId
                timeOut = 6000;
                ECUagreement.canId = "000007A2";
                // 5、发送7A2 指令1002 长度2 返回 7AA 5002
                msg = ECUagreement.a("1002");
                if (replay()) {
                    return;
                }

                ECUagreement.canId = "000007DF";
                // 2、发送7DF  指令 长度2  只发送不需要接收
                msg = ECUagreement.a("3E80");
                Log.e("cyf", "发送信息 : " + msg + "  ");
                SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ECUagreement.canId = "000007A2";
                //6、发送7A2 指令2703   长度2  返回  7AA 6703   四个字节seed（随机产生，给算法，经运算后下一步发送）
                msg = ECUagreement.a("2703");
                if (replay()) {
                    return;
                }
                String data = myData.get(0);
                //7、发送7A2 指令2704+seed运算后的数值   长度6   返回 7AA 6704
                msg = ECUagreement.a("2704" +
                        StringTools.byte2hex(ECU2Tools._GetBootKey(Long.valueOf(ECUTools.getData2(data, 1, msg), 16))));
                if (replay()) {
                    return;
                }
                //8、CANID 7A2   指令 2EF19831323334353637383900     长度 13    返回 7AA  6EF198
                // msg = ECUagreement.a("2EF19831323334353637383900");//hs5
                msg = ECUagreement.a("2EF1980000000000000000");
                if (replay()) {
                    return;
                }
                //9、CANID 7A2   指令2EF199   长度 7 返回 7AA  6EF199
                // msg = ECUagreement.a("2EF19920180521");//hs5
                msg = ECUagreement.a("2EF199180521");
                if (replay()) {
                    return;
                }
                // 计算总数
                int totalNum = 0;
                for (int i = 0; i < writeFileBeans.size(); i++) {
                    int total = writeFileBeans.get(i).getData().length / 1024;
                    if (writeFileBeans.get(i).getData().length % 1024 != 0) {
                        total = total + 1;
                    }
                    totalNum = totalNum + total;
                }
                // 开始循环刷写流程
                int index = 0;
                while (writeFileBeans.size() > index) {
                    putData("开始刷写第" + (index + 1) + "段");
//                    if (index != 0) {
//                        //10、CANID 7A2 指令 3101FF0044 00FE0000(addr) 0009F230(len)(这条指令是数据擦除) 返回7AA 7101FF
//                        msg = ECUagreement.a("3101FF0044" + writeFileBeans.get(index).getAddress() +
//                                String.format("%08x", Integer.valueOf(writeFileBeans.get(index).getLength())));
//                        if (replay()) {
//                            return;
//                        }
//                    }
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
                        String xx = "";
                        if (index2 > 254) {
                            xx = String.format("%02x", (index2 + 1) % 256);
                        } else {
                            xx = String.format("%02x", index2 % 255 + 1);
                        }

                        int byteLength = writeFileBeans.get(index).getData().length - (index2 * 1024);
                        if (byteLength < 1024) {
                            msg = ECUagreement.a("36" + xx + StringTools.byte2hex(ByteTools.subBytes(writeFileBeans.get(index).getData(), index2 * 1024, byteLength)));
                        } else {
                            msg = ECUagreement.a("36" + xx + StringTools.byte2hex(ByteTools.subBytes(writeFileBeans.get(index).getData(), index2 * 1024, 1024)));
                        }
                        if (replay()) {
                            return;
                        }
                        int x = (updateNum * 100 / totalNum);
                        updateNum++;
                        // Log.e("cyf88", updateNum + " * 100 / " + totalNum + " = " + x);
                        putData("-" + x);
                        index2++;
                    }
                    //13、CANID 7A2 指令 37  长度1 返回 77
                    msg = ECUagreement.a("37");
                    if (replay()) {
                        return;
                    }
                    //14、CANID 7A2 指令 310102026E2ECDCD（一次性校验）  长度8 返回 71
                    String crc = Long.toHexString(ECU2Tools.CRC(writeFileBeans.get(index).getData2()));
                    if (crc.length() == 1) {
                        crc = "0000000" + crc;
                    } else if (crc.length() == 2) {
                        crc = "000000" + crc;
                    } else if (crc.length() == 3) {
                        crc = "00000" + crc;
                    } else if (crc.length() == 4) {
                        crc = "0000" + crc;
                    } else if (crc.length() == 5) {
                        crc = "000" + crc;
                    } else if (crc.length() == 6) {
                        crc = "00" + crc;
                    } else if (crc.length() == 7) {
                        crc = "0" + crc;
                    }
                    // msg = ECUagreement.a("31010202" + crc);//hs5
                    msg = ECUagreement.a("3101ff01ff");
                    if (replay()) {
                        return;
                    }
                    // putData("第" + (index + 1) + "段刷写完成");

                    if (index == 0) {
                        //10、CANID 7A2 指令 3101FF0044 00FE0000(addr) 0009F230(len)(这条指令是数据擦除) 返回7AA 7101FF
//                        msg = ECUagreement.a("3101FF0044" + writeFileBeans.get(index).getAddress() +
//                                String.format("%08x", Integer.valueOf(writeFileBeans.get(index).getLength())));//hs5
                        msg = ECUagreement.a("3101ff00ff");
                        if (replay()) {
                            return;
                        }
                    }

                    index++;
                }
                // 15、一致性校验
//                msg = ECUagreement.a("3101ff01");
//                if (replay()) {
//                    return;
//                }
                // 16、物理寻址11 01（等待4秒）
                msg = ECUagreement.a("1101");
                timeOut = 4000;
                if (replay()) {
                    return;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                timeOut = 500;
                ECUagreement.canId = "000007DF";
                // 17、发送7DF  指令1003 长度2 不需要接收
                msg = ECUagreement.a("1003");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 18、发送7DF  指令280003 长度3  只发送不需要接收
                msg = ECUagreement.a("280003");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 19、发送7DF  指令8501 长度2  只发送不需要接收
                msg = ECUagreement.a("8501");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 19-2、stop 3e
                timeOut = 6000;
                msg = OBDagreement.h();
                if (replay2()) {
                    return;
                }
                timeOut = 500;
                // 20、发送7DF  指令1001 长度2  只发送不需要接收
                msg = ECUagreement.a("1001");
                // SocketService.Companion.getIntance().sendMsg(StringTools.hex2byte(msg), connectLinstener);
                if (replay()) {

                }
                // 更改canId
                timeOut = 6000;
                ECUagreement.canId = "000007A2";
                // 21、物理寻址 14 FF FF FF
                msg = ECUagreement.a("14FFFFFF");
                if (replay()) {
                    return;
                }
                putData("-100");
                putData("刷写完成");
            }
        }).start();
    }

}
