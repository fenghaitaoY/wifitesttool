package com.example.wifitest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiUtilTools {

    /**
     * 获取当前已经连接wifi的ip地址
     * @return
     */
    public static String getCurrentIp(Context context){
        int paramInt=0;
        WifiInfo info = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
            paramInt = info.getIpAddress();


            return new StringBuffer().append(0xFF & paramInt).append(".")
                    .append(0xFF & paramInt >> 8).append(".")
                    .append(0xFF & paramInt >> 16).append(".")
                    .append(0xFF & paramInt >> 24).toString();
        }else{
            return "请连接wifi";
        }


    }

    String st;
}
