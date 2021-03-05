package com.example.wifitest;

import android.net.wifi.ScanResult;

public class MyWifiInfo {
    ScanResult result;
    int supportFreq; //2.4G=1; 5G = 2; 2.4/5G=3

    public ScanResult getResult() {
        return result;
    }

    public void setResult(ScanResult result) {
        this.result = result;
    }

    public int getSupportFreq() {
        return supportFreq;
    }

    public void setSupportFreq(int supportFreq) {
        this.supportFreq = supportFreq;
    }

    public boolean isSupport24G(){
        return supportFreq == 1 ;
    }

    public boolean isSupport5G(){
        return supportFreq == 2 ;
    }

}
