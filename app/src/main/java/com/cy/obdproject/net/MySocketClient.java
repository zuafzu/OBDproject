package com.cy.obdproject.net;

import android.util.Log;

import com.cy.obdproject.agreement.ECUagreement;
import com.cy.obdproject.agreement.OBDagreement;
import com.cy.obdproject.tools.StringTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

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
        if (mClient == null) {
            mClient = new Socket();
            SocketAddress socAddress = new InetSocketAddress(mDstName, mDesPort);
            mClient.connect(socAddress, 3000);
            boolean flag = mClient.isConnected();
            Log.e("cyf", "是否连接上 ： " + flag);
            if (flag) {
                index = 0;
                next();
            }
        }
        //获取其他客户端发送过来的数据
        InputStream inputStream = mClient.getInputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(buffer)) != -1) {
            //通过回调接口将获取到的数据推送出去
            if (mListener != null) {
                String data = StringTools.byte2hex(buffer);
                data = data.substring(0, StringTools.byte2hex(buffer).lastIndexOf("AA") + 2);
                Log.e("cyf", "data ： " + data);
                String mKey = data.substring(6, 8);
                int length = Integer.parseInt(Integer.parseInt(data.substring(2, 4), 16) + ""
                        + Integer.parseInt(data.substring(4, 6), 16));
                if (mKey.equals(key) && length == ((data.length() - 8) / 2) && data.endsWith("00AA")) {
                    next();
                } else {
                    mListener.onReceiveData("obd启动异常");
                    disconnect();
                }
            }
        }
    }


    /**
     * 将数据发送给指定的接收者
     *
     * @param data 需要发送的内容
     */
    public void send(String data) throws IOException {
        if (mClient != null) {
            OutputStream outputStream = mClient.getOutputStream();
            outputStream.write((data).getBytes());
        }
    }

    /**
     * 将数据发送给指定的接收者
     *
     * @param data 需要发送的内容
     */
    public void send(byte[] data) throws IOException {
        if (mClient != null) {
            OutputStream outputStream = mClient.getOutputStream();
            outputStream.write(data);
        }
    }

    /**
     * 获取socket连接情况
     */
    public boolean isConnected() {
        if (mClient != null) {
            mClient.isConnected();
            return mClient.isConnected();
        }
        return false;
    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (null != mClient && mClient.isConnected()) {
            mClient.shutdownInput();
            mClient.shutdownOutput();
            mClient.close();
            mClient = null;
        }
        Log.e("cyf", "是否断开了 ： " + (mClient == null));
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

    private String key = "";
    private int index = 0;

    private void next() {
        try {
            String msg = "";
            switch (index) {
                case 0:
                    msg = OBDagreement.a("10", "05", "0c");
                    Log.i("cyf", "msg1 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                case 1:
                    msg = OBDagreement.b("10", "0007a120");
                    Log.i("cyf", "msg2 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                case 2:
                    msg = OBDagreement.c("10", "c6dc0000", "0019f5f4");
                    Log.i("cyf", "msg3 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                case 3:
                    msg = OBDagreement.d("10", "18da00fa", "18dafa00");
                    Log.i("cyf", "msg4 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                case 4:
                    msg = OBDagreement.e("10", "00");
                    Log.i("cyf", "msg5 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                case 5:
                    msg = OBDagreement.f("10", "00");
                    Log.i("cyf", "msg6 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                case 6:
                    msg = ECUagreement.a("10", "18da00fa", "0008", "0210030000000000");
                    Log.i("cyf", "msg1 : " + msg);
                    send(StringTools.hex2byte(msg));
                    break;
                default:
                    mListener.onReceiveData("obd启动成功");
                    disconnect();
                    break;
            }
            if (msg.length() > 8) {
                key = msg.substring(6, 8);
            }
            index++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
