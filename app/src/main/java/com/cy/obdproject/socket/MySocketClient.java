package com.cy.obdproject.socket;

import android.util.Log;

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
            Log.e("cyf", "MySocketClient 是否连接上 ： " + flag);
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
                int length = Integer.parseInt(Integer.parseInt(data.substring(2, 4), 16) + ""
                        + Integer.parseInt(data.substring(4, 6), 16));
                data = data.substring(0, 6 + (length * 2) + 2);
                Log.i("cyf", "收到信息 : " + data);
                mListener.onReceiveData(data);
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
        if (mClient != null && mClient.isConnected()) {
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

}
