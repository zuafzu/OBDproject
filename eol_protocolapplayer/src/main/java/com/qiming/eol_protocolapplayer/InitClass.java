package com.qiming.eol_protocolapplayer;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class InitClass {

    private JSONObject jo = new JSONObject();

    private String RESULT = "SUCCESS";
    private String DESC = "";

    private long outTime = 10 * 1000;
    private MySocketClient msgClient;
    private List<String> myData = new ArrayList<>();
    private String key = "";
    private String msg = "";
    private byte[] msgBytes = null;
    private Long sysTime1 = 0L;
    private Long sysTime2 = 0L;

    private boolean isDynamicRun = false;

    private boolean isImLog = false;

    @SuppressLint("StaticFieldLeak")
    private com.qiming.eol_public.InitClass publicUnit;
    public void setPublicUnit(com.qiming.eol_public.InitClass publicUnit)
    {
        this.publicUnit = publicUnit;
    }

    public String Controls(String inputdata) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            long startTime = System.currentTimeMillis();
            // 解析入参
            JSONObject object = new JSONObject(inputdata);
            String switchKey = object.optString("CMD");
            switch (switchKey) {
                case "INIT":// 连接soket
                    try {
                        Class clazz = Class.forName("com.qiming.eol_scriptrunner.ScriptManager");
                        Field field1 = clazz.getField("isImLog");
                        isImLog = field1.getBoolean(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    DESC = "";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (msgClient == null) {
                                String dstName = "";
                                int dstPort = -1;
                                try {
                                    Class clazz = Class.forName("com.qiming.eol_scriptrunner.ScriptManager");
                                    Field field1 = clazz.getField("mDstName");
                                    dstName = field1.get(clazz).toString();
                                    Field field2 = clazz.getField("dstPort");
                                    dstPort = field2.getInt(clazz);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                                if (dstName.equals("") || dstPort < 0) {
                                    DESC = "，ip地址或者端口号不正确";
                                } else {
                                    msgClient = new MySocketClient(dstName, dstPort);
                                }
                            }
                            if (msgClient.isConnected()) {
                                msgClient.disconnect();
                            }
                            try {
                                msgClient.connect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    while (true) {
                        Thread.sleep(1000);
                        Log.e("cyf", "000");
                        if (msgClient.isConnected()) {
                            Log.e("cyf", "111111");
                            break;
                        }
                        // 设置10秒超时时间
                        if ((System.currentTimeMillis() - startTime) > outTime) {
                            jsonObject.put("RESULT", "FAULT");
                            jsonObject.put("DESC", "socket连接超时" + DESC);
                            return jsonObject.toString();
                        }
                    }
                    return jsonObject.toString();
                case "STARTCAN":
                    RESULT = "SUCCESS";
                    DESC = "";

                    msg = OBDagreement.f("01");
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    msg = OBDagreement.i();
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }

                    msg = OBDagreement.a(String.format("%02x", Integer.valueOf(jo.optString("PIN_TYPE_CAN_H")))
                            , String.format("%02x", Integer.valueOf(jo.optString("PIN_TYPE_CAN_L"))));
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    msg = OBDagreement.b(String.format("%08x", Integer.valueOf(jo.optString("CP_Baudrate"))));
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    // 不需要转16进制，本身是16进制
//                    msg = OBDagreement.c(String.format("%08x", Integer.valueOf(jo.optString("CP_CanACR"))),
//                            String.format("%08x", Integer.valueOf(jo.optString("CP_CanAMR"))));
                    msg = OBDagreement.c(oderString(jo.optString("CP_CanACR"), 8),
                            oderString(jo.optString("CP_CanAMR"), 8));
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    // 不需要转16进制，本身是16进制
//                    msg = OBDagreement.d(String.format("%08x", Integer.valueOf(jo.optString("TESTERTOECU"))),
//                            String.format("%08x", Integer.valueOf(jo.optString("ECUTOTESTER"))));
                    ECUagreement.canId = oderString(jo.optString("TESTERTOECU"), 8);
                    ECUagreement.reCanId = oderString(jo.optString("ECUTOTESTER"), 8);
                    msg = OBDagreement.d(ECUagreement.canId,
                            ECUagreement.reCanId);
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    msg = OBDagreement.e(String.format("%02x", Integer.valueOf(jo.optString("STMIN"))));
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    msg = OBDagreement.f("00");
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    return jsonObject.toString();
                case "STOPCAN":
                    msg = OBDagreement.f("01");
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    msg = OBDagreement.i();
                    if (replay()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    break;
                case "DESTORY":// 断开soket
                    if (msgClient != null && msgClient.isConnected()) {
                        msgClient.disconnect();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private String oderString(String string, int len) {
        int size = len - string.length();
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < size; i++) {
            value.append("0");
        }
        return value.toString() + string;
    }

    private boolean replay() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        if (msg.length() > 8) {
            key = msg.substring(6, 8).toLowerCase();
        }
        if (msgClient != null && msgClient.isConnected()) {
            Log.e("cyf", "发送信息 : " + msg + "  ");
            msgClient.setOnConnectLinstener(new MySocketClient.ConnectLinstener() {
                @Override
                public void onReceiveData(String data) {
                    Log.e("cyf", "收到信息 : " + OBDagreement.unDecodeString(data));
                    myData.add(OBDagreement.unDecodeString(data));
                }
            });
            msgClient.send(StringTools.hex2byte(msg));
            sysTime1 = new Date().getTime();
            sysTime2 = 0L;
        } else {
            DESC = "OBD连接断开，请重新启动软件";
            return true;
        }
        return sleep() || checkData();
    }

    private boolean sleep() {
        while (myData.size() == 0) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sysTime2 = new Date().getTime();
            if (sysTime2 - sysTime1 > outTime) {
                DESC = "返回数据超时";
                return true;
            }
        }
        return false;
    }

    private boolean checkData() {
        sysTime2 = new Date().getTime();
        if (sysTime2 - sysTime1 <= outTime) {
            for (int i = 0; i < myData.size(); i++) {
                String mKey = myData.get(i).substring(6, 8).toLowerCase();
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
                DESC = "返回数据异常";
                return true;
            }
        } else {
            DESC = "返回数据超时";
            return true;
        }
        return false;
    }

    public String SetVCIParams(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");

            JSONObject obj = new JSONObject(inputdata);
            Iterator iterator = obj.keys();
            while (iterator.hasNext()) {
                String key = iterator.next() + "";
                jo.put(key, obj.optString(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public String DatatransForCan(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            msg = ECUagreement.a(new JSONObject(inputdata).optString("DATA"));
            // 判断携带没携带canId信息
            if (new JSONObject(inputdata).has("TESTERTOECU")) {
                ECUagreement.canId = oderString(new JSONObject(inputdata).optString("TESTERTOECU"), 8);
            }
            if (replay2()) {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", DESC);
                return jsonObject.toString();
            }
            String data = myData.get(0);
            int b = data.toLowerCase().indexOf(ECUagreement.reCanId.toLowerCase());
            String msg = data.substring(b + ECUagreement.reCanId.length() + 4, data.length() - 2);
            jsonObject.put("DATA", msg);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private boolean replay2() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        if (msgClient != null && msgClient.isConnected()) {
            Log.e("cyf", "发送信息 : " + msg + "  ");
            msgClient.setOnConnectLinstener(new MySocketClient.ConnectLinstener() {
                @Override
                public void onReceiveData(String data) {
//                    Log.e("cyf", "收到信息 : " + OBDagreement.unDecodeString(data));
//                    if (OBDagreement.unDecodeString(data).toLowerCase().contains(ECUagreement.reCanId.toLowerCase())) {
//                        myData.add(OBDagreement.unDecodeString(data));
//                    }
                    Log.e("cyf", "收到信息 : " + data);
                    if (OBDagreement.unDecodeString(data).toLowerCase().contains(ECUagreement.reCanId.toLowerCase())) {
                        myData.add(data);
                    }
                }
            });
            msgClient.send(StringTools.hex2byte(msg));
            sysTime1 = new Date().getTime();
            sysTime2 = 0L;
        } else {
            DESC = "OBD连接断开，请重新启动软件";
            return true;
        }
        return sleep() || checkData2();
    }

    private boolean checkData2() {
        sysTime2 = new Date().getTime();
        if (sysTime2 - sysTime1 <= outTime) {
            String mmsg = "";
            for (int i = 0; i < myData.size(); i++) {
                mmsg = ECUTools.getData(myData.get(i), 1, msg);
                if (mmsg.equals(ECUTools.ERR)) {
                    myData.remove(i);
                    i--;
                } else if (mmsg.equals(ECUTools.WAIT)) {
                    myData.remove(i);
                    sysTime1 = new Date().getTime();
                    sysTime2 = 0L;
                    return sleep() || checkData2();
                } else {
                    break;
                }
            }
            if (myData.size() == 0) {
                DESC = "返回数据异常";
                return true;
            }
        } else {
            DESC = "返回数据超时";
            return true;
        }
        return false;
    }

    public String Datatrans36Block(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            if (jo.optString("FORMAT").equals("36_SNO") &&
                    jo.optString("TYPE").equals("CAN")) {
                jsonObject.put("RESULT", "SUCCESS");
                jsonObject.put("DESC", "");

                // String DATAMEMORY = jo.optString("DATAMEMORY");//数据

                // Class clazz = Class.forName("com.qiming.eol_producefileparser.InitClass");
                // Method mm = clazz.getDeclaredMethod("getBytesData", (Class<?>) null);
                // byte[] bytes = (byte[]) mm.invoke(null, (Object) null);


                byte[] bytes = publicUnit.getBytesData();

                long LEN = Long.valueOf(jo.optString("LEN"), 16);//数据长度
                int PACKMAXLEN = Integer.valueOf(jo.optString("PACKMAXLEN"));//每包最大值
                String MESSAGE_TO_PROGRESS = jo.optString("MESSAGE_TO_PROGRESS");//是否发送进度，1发送

                int index2 = 0;
                long total = LEN / PACKMAXLEN;
                if (LEN % PACKMAXLEN != 0) {
                    total = total + 1;
                }
                while (total > index2) {
                    String xx = "";
                    if (index2 > 254) {
                        xx = String.format("%02x", (index2 + 1) % 256);
                    } else {
                        xx = String.format("%02x", index2 % 255 + 1);
                    }

                    int byteLength = (int) (LEN - (index2 * PACKMAXLEN));
                    Log.e("cyf", "准备发送36信息");
                    // 优化模式
                    byte[] mByte = null;
                    if (byteLength < PACKMAXLEN) {
                        mByte = ByteTools.subBytes(bytes, index2 * PACKMAXLEN, byteLength);
                    } else {
                        mByte = ByteTools.subBytes(bytes, index2 * PACKMAXLEN, PACKMAXLEN);
                    }
                    String l1 = Integer.toHexString(mByte.length + 1 + 1 + 1 + ECUagreement.canId.length() / 2 + 1 + 1 + 1 + 1);
                    String l2 = Integer.toHexString(mByte.length + 1 + 1);
                    Log.e("cyf", "准备发送36信息1");
                    msgBytes = ByteTools.byteMerger(StringTools.hex2byte("aa" +
                                    oderString(l1, 4) +
                                    "75" +
                                    OBDagreement.numPlus() +
                                    ECUagreement.canLinkNum +
                                    ECUagreement.canId + oderString(l2, 4) +
                                    "36" +
                                    xx),
                            mByte);
                    msgBytes = ByteTools.byteMerger(msgBytes, StringTools.hex2byte("55"));
                    // byte数组模式，先转字符串，再转byte数组发送
//                    if (byteLength < PACKMAXLEN) {
//                        msg = ECUagreement.a("36" + xx + StringTools.byte2hex(ByteTools.subBytes(bytes, index2 * PACKMAXLEN, byteLength)));
//                    } else {
//                        msg = ECUagreement.a("36" + xx + StringTools.byte2hex(ByteTools.subBytes(bytes, index2 * PACKMAXLEN, PACKMAXLEN)));
//                    }
                    // 字符串模式，json接受不了太大的数据，暂不使用该模式
//                    if (byteLength < PACKMAXLEN) {
//                        msg = ECUagreement.a("36" + xx + DATAMEMORY.substring(index2 * PACKMAXLEN * 2, (index2 * PACKMAXLEN + byteLength) * 2));
//                    } else {
//                        msg = ECUagreement.a("36" + xx + DATAMEMORY.substring(index2 * PACKMAXLEN * 2, (index2 * PACKMAXLEN + PACKMAXLEN) * 2));
//                    }
                    Log.e("cyf", "准备发送36信息完毕");
                    if (replay3()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        break;
                    }
                    if (MESSAGE_TO_PROGRESS.equals("1")) {
                        int x = (int) (index2 * 100 / total);
                        try {
                            publicUnit.SetFlashPos(x);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    index2++;
                }
            } else {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "无法调用");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private boolean replay3() {
        if (myData.size() > 0) {
            myData.remove(0);
        }
        Log.e("cyf", (msgClient != null) + "  判断  " + msgClient.isConnected());
        if (msgClient != null && msgClient.isConnected()) {
            if (msgBytes != null) {
                Log.e("cyf", "发送36信息");
            } else {
                Log.e("cyf", "发送信息 : " + msg + "  ");
            }
            msgClient.setOnConnectLinstener(new MySocketClient.ConnectLinstener() {
                @Override
                public void onReceiveData(String data) {
//                    Log.e("cyf", "收到信息 : " + OBDagreement.unDecodeString(data));
//                    if (OBDagreement.unDecodeString(data).toLowerCase().contains(ECUagreement.reCanId.toLowerCase())) {
//                        myData.add(OBDagreement.unDecodeString(data));
//                    }
                    Log.e("cyf", "收到信息 : " + data);
                    if (OBDagreement.unDecodeString(data).toLowerCase().contains(ECUagreement.reCanId.toLowerCase())) {
                        myData.add(data);
                    }
                }
            });
            // msgClient.send(StringTools.hex2byte(msg));
            msgClient.send(msgBytes);
            if (msgBytes != null) {
                Log.e("cyf", "发送36信息 发完");
            } else {
                Log.e("cyf", "发送信息 : " + msg + "  ");
            }
            sysTime1 = new Date().getTime();
            sysTime2 = 0L;
        } else {
            DESC = "OBD连接断开，请重新启动软件";
            return true;
        }
        return sleep() || checkData3();
    }

    private boolean checkData3() {
        sysTime2 = new Date().getTime();
        if (sysTime2 - sysTime1 <= outTime) {
            String mmsg = "";
            for (int i = 0; i < myData.size(); i++) {
                // mmsg = ECUTools.getData(myData.get(i), 1, msg);
                mmsg = ECUTools.getData2(myData.get(i), 1, "36");
                if (mmsg.equals(ECUTools.ERR)) {
                    myData.remove(i);
                    i--;
                } else if (mmsg.equals(ECUTools.WAIT)) {
                    myData.remove(i);
                    sysTime1 = new Date().getTime();
                    sysTime2 = 0L;
                    return sleep() || checkData3();
                } else {
                    break;
                }
            }
            if (myData.size() == 0) {
                DESC = "返回数据异常";
                return true;
            }
        } else {
            DESC = "返回数据超时";
            return true;
        }
        return false;
    }

    public String ReadMutilDatas(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            JSONObject jo = new JSONObject(inputdata);
            String DATA = jo.optString("DATA");
            List<ParseBean> base = ParseBeanList(DATA);
            JSONArray jsonArray = new JSONArray();
            int a = 0;
            while (a < base.size()) {
//                if (a == 8 || a == 9) {
//                    a++;
//                    continue;
//                }
                id = base.get(a).getSid() + base.get(a).getId();
                msg = ECUagreement.a(id);
                // 判断携带没携带canId信息
                if (new JSONObject(inputdata).has("TESTERTOECU")) {
                    ECUagreement.canId = oderString(new JSONObject(inputdata).optString("TESTERTOECU"), 8);
                }
                if (replay2()) {
                    jsonObject.put("RESULT", "FAULT");
                    jsonObject.put("DESC", DESC);
                    return jsonObject.toString();
                }
                String data = myData.get(0);
                int b = data.toLowerCase().indexOf(ECUagreement.reCanId.toLowerCase());
                String msg = data.substring(b + ECUagreement.reCanId.length() + 4, data.length() - 2);
                SigNal s = ParseSigNal(msg, base);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("Name", s.getName());
                jsonObject1.put("Value", s.getValue());
                jsonArray.put(jsonObject1);
                a++;
                Log.e("cyf", "a : " + a + "   base.size : " + base.size());
            }
            jsonObject.put("DATA", "{\"List\":" + jsonArray.toString() + "}");
            try {
                publicUnit.SetBaseInfo(jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                if (id.equals("")) {
                    jsonObject.put("DESC", "错误信息：" + e.getMessage());
                } else {
                    jsonObject.put("DESC", "错误信息：" + e.getMessage() + "（错误访问sid+did是：" + id + "）");
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

        List<ParseBean> ParseBeanList(String json) {
        List<ParseBean> result = new ArrayList<ParseBean>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsList = jsonObject.getJSONArray("List");
            int count = jsList.length();
            for (int i = 0; i < count; i++) {
                JSONObject js = jsList.getJSONObject(i);
                ParseBean item = new ParseBean();
                item.Parse(js);
                result.add(item);
            }
        } catch (JSONException e) {
        }

        return result;
    }

    SigNal ParseSigNal(String content, List<ParseBean> list) {
        SigNal result = null;

        String strDID = content.substring(2, 6); // ��ĿID
        // -------------������Ϣ
        for (ParseBean dictItem : list) {
            String id = dictItem.getId();
            if (id != null) {
                if (id.equals(strDID)) {
                    result = SigNal.Pase4Service(dictItem, content.substring(2)/*ȥ�� 0x62*/);
                    break;
                }
            }
        }

        return result;
    }

    public  String ReadContinusDatas(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        try {
            isDynamicRun = true;
            jsonObject.put("RESULT", "SUCCESS");
            JSONObject jo = new JSONObject(inputdata);
            String DATA = jo.optString("ITEMS");
            List<ParseBean> base = ParseBeanList("{\"List\":" + DATA + "}");
            while (isDynamicRun) {
                JSONArray jsonArray = new JSONArray();
                int a = 0;
                Log.e("cyf77", "base.size()   " + base.size());
                while (a < base.size()) {
                    id = base.get(a).getSid() + base.get(a).getId();
                    msg = ECUagreement.a(id);
                    // 判断携带没携带canId信息
                    if (new JSONObject(inputdata).has("TESTERTOECU")) {
                        ECUagreement.canId = oderString(new JSONObject(inputdata).optString("TESTERTOECU"), 8);
                    }
                    if (replay2()) {
                        jsonObject.put("RESULT", "FAULT");
                        jsonObject.put("DESC", DESC);
                        return jsonObject.toString();
                    }
                    String data = myData.get(0);
                    int b = data.toLowerCase().indexOf(ECUagreement.reCanId.toLowerCase());
                    String msg = data.substring(b + ECUagreement.reCanId.length() + 4, data.length() - 2);
                    SigNal s = ParseSigNal(msg, base);
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Name", s.getName());
                    jsonObject1.put("Value", s.getValue());
                    jsonArray.put(jsonObject1);
                    a++;
                }
                Thread.sleep(5);
                jsonObject.put("DATA", jsonArray.toString());

                try {
                publicUnit.SetLiveInfo(jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                isDynamicRun = false;
                jsonObject.put("RESULT", "FAULT");
                if (id.equals("")) {
                    jsonObject.put("DESC", "错误信息：" + e.getMessage());
                } else {
                    jsonObject.put("DESC", "错误信息：" + e.getMessage() + "（错误访问sid+did是：" + id + "）");
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public String SetStopReadLive(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            isDynamicRun = false;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public String ReadFreezeDatas(String inputdata) {
        Log.e("cyf", "inputdata : " + inputdata);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            JSONObject jo = new JSONObject(inputdata);
            String DICT = jo.optString("DICT");
            List<ParseBean> base = ParseBeanList(DICT);
            String DATA = jo.optString("DATA");
            msg = ECUagreement.a(DATA);
            // 判断携带没携带canId信息
            if (new JSONObject(inputdata).has("TESTERTOECU")) {
                ECUagreement.canId = oderString(new JSONObject(inputdata).optString("TESTERTOECU"), 8);
            }
            if (replay2()) {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", DESC);
                return jsonObject.toString();
            }
            String data = myData.get(0);
            int b = data.toLowerCase().indexOf(ECUagreement.reCanId.toLowerCase());
            String msg = data.substring(b + ECUagreement.reCanId.length() + 4, data.length() - 2);
            List<SigNal> list = ParseFreezeSigNal(msg, base);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("Name", list.get(i).getName());
                jsonObject1.put("Value", list.get(i).getValue());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("DATA", "{\"List\":" + jsonArray.toString() + "}");
            try {
//                Class clz = Class.forName("com.qiming.eol_message.InitClass");
//                Method method = clz.getDeclaredMethod("SetBaseInfo", String.class);
//                method.invoke(null, jsonObject.toString());
                publicUnit.SetBaseInfo(jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    List<SigNal> ParseFreezeSigNal(String content, List<ParseBean> freezes) {
        String strBody = content.substring(14);
        int n = 0;
        List<SigNal> listFrz = new ArrayList<SigNal>();
        try {
            while (n < strBody.length()) {
                String pid = strBody.substring(n, n += (2 * 2));
                boolean found = false;

                for (ParseBean pb : freezes) {
                    String id = pb.getId();
                    found = pid.equalsIgnoreCase(id);
                    if (found) {
                        int len = pb.getLength();
                        String data = strBody.substring(n - (2 * 2), n += (len * 2));
                        if (ParseTypeEnum.Null.equals(pb.getParseType()) == false) {
                            SigNal signal = SigNal.Pase4Service(pb, data);
                            listFrz.add(signal);
                        }
                        break;
                    }
                }
            }

        } catch (Throwable t) {

        }
        return listFrz;
    }

}
