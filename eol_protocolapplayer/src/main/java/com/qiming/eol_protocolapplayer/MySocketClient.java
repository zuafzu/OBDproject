package com.qiming.eol_protocolapplayer;

import android.util.Log;

import com.qiming.eol_public.InitClass;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * socket连接工具
 */
public class MySocketClient {

    private Socket mClient;
    /**
     * 服务端的ip
     */
    private String mDstName;
    /**
     * 服务端端口号
     */
    private int mDesPort;

    private ConnectLinstener mListener;

    private boolean isImLog = false;


    public MySocketClient(String dstName, int dstPort) {
        this.mDstName = dstName;
        this.mDesPort = dstPort;
    }

    /**
     * 与服务端进行连接
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        try {
            Class clazz = Class.forName("com.qiming.eol_scriptrunner.ScriptManager");
            Field field1 = clazz.getField("isImLog");
            isImLog = field1.getBoolean(clazz);
        } catch (Exception e) {
            LogTools.errLog(e);
        }
        if (mClient == null) {
            mClient = new Socket();
            SocketAddress socAddress = new InetSocketAddress(mDstName, mDesPort);
            mClient.connect(socAddress, 3000);
            // mClient.setKeepAlive(true);//开启保持活动状态的套接字
            boolean flag = mClient.isConnected();
            Log.e("cyf", "MySocketClient 是否连接上 ： " + flag);
        }
        //获取其他客户端发送过来的数据
//        InputStream inputStream = mClient.getInputStream();
//        byte[] buffer = new byte[1024];
        byte[] buffer;
//        int len = -1;
        while ((buffer = GetDataByte()) != null) {
            //通过回调接口将获取到的数据推送出去
            String data = StringTools.byte2hex(buffer);
            //data = data.substring(0, len * 2);
            // Log.i("cyf", len + " 收到原始信息 : " + data);
            Log.i("cyf", buffer.length + " 收到原始信息 : " + data);
            if (mListener != null) {
                // 多包
                while (!data.equals("")) {
                    int length = Integer.parseInt(Integer.parseInt(data.substring(2, 4), 16) + ""
                            + Integer.parseInt(data.substring(4, 6), 16));
                    String mdata = data.substring(0, 6 + (length * 2) + 2);
                    data = data.replace(mdata, "");
                    mListener.onReceiveData(mdata);
                    // 接收（写日志）
                    if (isImLog) {
                        String filePath = InitClass.pathTongxun;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS");
                        Date date = new Date(System.currentTimeMillis());
                        String fileName = simpleDateFormat.format(date) + "log.txt";
                        FileUtil.writeTxtToFile(simpleDateFormat2.format(date) + "  接收  " + mdata, filePath, fileName);
                        Log.e("cyf77", "写完通讯接收的日志");
                    }
                }
            }
        }
    }

    /**
     * 将数据发送给指定的接收者
     *
     * @param data 需要发送的内容
     */
    public void send(byte[] data) {
        try {
            if (mClient != null && mClient.isConnected()) {
                OutputStream outputStream = mClient.getOutputStream();
                outputStream.write(data);
            }
            // 发送（写日志）
            if (isImLog) {
                String filePath = InitClass.pathTongxun;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS");
                Date date = new Date(System.currentTimeMillis());
                String fileName = simpleDateFormat.format(date) + "log.txt";
                if (data.length > 512) {
                    FileUtil.writeTxtToFile(simpleDateFormat2.format(date) + "  发送  " + "大于512字节...", filePath, fileName);
                } else {
                    FileUtil.writeTxtToFile(simpleDateFormat2.format(date) + "  发送  " + StringTools.byte2hex(data), filePath, fileName);
                }
                Log.e("cyf77", "写完通讯发送的日志");
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            Log.e("cyf", "obd连接异常，无法发送数据了");
        }
    }

    /**
     * 获取socket连接情况
     */
    public boolean isConnected() {
        if (mClient != null) {
            return mClient.isConnected();
        }
        return false;
    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disconnect() {
        try {
            if (null != mClient && mClient.isConnected()) {
                mClient.shutdownInput();
                mClient.shutdownOutput();
                mClient.close();
                mClient = null;
            }
            Log.e("cyf", "是否断开了 ： " + (mClient == null));
        } catch (Exception e) {
            LogTools.errLog(e);
        }
    }

    public void setOnConnectLinstener(ConnectLinstener linstener) {
        this.mListener = linstener;
    }

    /**
     * 数据接收回调接口
     */
    public interface ConnectLinstener {
        void onReceiveData(String data);
    }

    public int JavaUnSignedByte2ToJavaSignedInt32(byte[] bytes) {
        int result = 0;
        if (bytes.length == 2) {
            int c = (bytes[0] & 0xff) << 8;
            int d = (bytes[1] & 0xff);
            result = c | d;
        }
        return result;
    }

    /**
     * 功能：接收数据
     * <p>
     * 参数：
     * inStream:输入数据流
     * size：要接收字节数
     * <p>
     * 返回值：接收到的数据流 失败返回null
     **/
    public byte[] RecvBytes(int size) {
        //当前缓冲区起始地址
        byte[] result = new byte[size];

        //将要接收数据字节数
        int currentRecvBuffAddr = 0;
        int DataSizeNeedToRecv = size;
        int netResult = 0;

        //数据尚未接受完毕
        while (DataSizeNeedToRecv > 0) {
            //使用WinSock API接收数据
            try {
                netResult = mClient.getInputStream().read(result, currentRecvBuffAddr, DataSizeNeedToRecv);
            } catch (Exception e) {
                LogTools.errLog(e);
                return null;
            }

            //判断网络状态
            if (netResult <= 0) {
                return null;
            }
            //将要接收数减少
            DataSizeNeedToRecv = DataSizeNeedToRecv - netResult;
            //缓冲区地址递增
            currentRecvBuffAddr = currentRecvBuffAddr + netResult;
        }

        return result;
    }

    byte BEGIN = (byte) 0x55;
    byte END = (byte) 0xaa;


    public byte[] GetDataByte() {
        byte[] Tmp = null;
        byte[] DateLength = null;
        int nDataLength = -1;

        while (true) {
            Tmp = RecvBytes(1);
            if (Tmp == null) {
                return null;
            }

            if (Tmp[0] != BEGIN) {
                continue;
            }

            DateLength = RecvBytes(2);
            if (DateLength == null) {
                return null;
            }
            nDataLength = JavaUnSignedByte2ToJavaSignedInt32(DateLength);

            Tmp = RecvBytes(nDataLength + 1);
            if (Tmp == null) {
                return null;
            }

            if (Tmp[nDataLength] != END) {
                continue;
            }

            break;
        }

        byte[] data = new byte[1 + 2 + Tmp.length];
        data[0] = BEGIN;
        System.arraycopy(DateLength, 0, data, 1, DateLength.length);
        System.arraycopy(Tmp, 0, data, 1 + 2, Tmp.length);

        return data;
    }

}
