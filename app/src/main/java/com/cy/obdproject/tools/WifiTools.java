package com.cy.obdproject.tools;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class WifiTools {

    private Context context;
    private WifiManager wifiManager;

    public WifiTools(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    // wifi热点开关
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            apConfig.SSID = "StationTest";
            apConfig.preSharedKey = "66666666";
            apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            apConfig.allowedKeyManagement.set(4);
            apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }


    //输出链接到当前设备的IP地址
    public String getHotIp() {
        ArrayList<String> connectedIP = getConnectedHotIP();
        StringBuilder resultList = new StringBuilder();
        for (String ip : connectedIP) {
            resultList.append(ip);
            resultList.append("\n");
        }
        return resultList.toString();
    }

    private ArrayList<String> getConnectedHotIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    //获取热点状态
    public int getWifiAPState() {
        int state = -1;
        try {
            Method method2 = wifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifiManager);
        } catch (Exception ignored) {

        }
        return state;
    }

}
