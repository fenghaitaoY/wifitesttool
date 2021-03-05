package com.example.wifitest;

import android.util.Log;

public class ParseIperDataHelper {


    public static DataBean parseData(String formatdata){
        //[  5]  10.00-11.00  sec  3.24 MBytes  27.2 Mbits/sec    0    707 KBytes
        Log.i("wifitag", "index="+formatdata.indexOf("Mbits/sec"));
        String socket  = formatdata.substring(formatdata.indexOf("["), formatdata.indexOf("]")+1);

        String [] datas = formatdata.split(" ");
        String time="";
        String endtime ="";
        for (String data: datas){
            //Log.i("wifitag", ""+data);
            if (data.contains("-")){
                time = data;
                endtime = time.split("-")[1];
                break;
            }
        }

        String[] bitrates = formatdata.split("  ");
        String bitrate="";
        String transfer="";

        for (String bit : bitrates){
            //Log.i("wifitag", " bit= "+bit);
            if (bit.contains("/sec")){
                bitrate = bit;
                break;  //解析完bitrate后跳出循环
            }
            if (bit.contains("Byte")){
                transfer = bit;

            }
        }


        Log.i("wifitag", " parse  socket ="+socket+" , time="+time+" , endtime="+endtime+", transfer="+transfer+ ", bitrate="+bitrate);
        DataBean dataBean = new DataBean();
        dataBean.endtime = endtime;
        dataBean.bitrate = bitrate;
        dataBean.transfer = transfer;

        return dataBean;
    }
}
