package com.example.wifitest;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadIperfDataHelper {

    IperfActionListener mListener;
    private static ReadIperfDataHelper singleton;

    RunThread mThread;

    private ReadIperfDataHelper(){}

    public static ReadIperfDataHelper getInstance(){
        if (singleton == null){
            synchronized (ReadIperfDataHelper.class){
                if (singleton == null){
                    singleton = new ReadIperfDataHelper();
                }
            }
        }
        return singleton;
    }


    public interface IperfActionListener{
        void onAction(String action);
    }


    public void setOnActionDataListener(IperfActionListener listener){
        mListener = listener;
    }


    public void startServer(){
        if (mThread == null){
            mThread = new RunThread();
            mThread.start();
        }
    }


    public void stop(){
        Log.i("wifitag", " stop");
        if (mThread != null){
            mThread.interrupt();
            mThread = null;
        }
    }


    class RunThread extends Thread{
        @Override
        public void run() {
            LocalServerSocket serverSocket=null;
            try {
                Log.i("wifitag", " start");
                serverSocket = new LocalServerSocket("/tmp/mysocket");
                while (true) {
                    LocalSocket receiver = serverSocket.accept();
                    if (receiver != null) {
                        InputStream input = receiver.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String info = reader.readLine();
                        Log.i("wifitag", " info =" + info);
                        mListener.onAction(info);
                        input.close();
                        reader.close();
                    }
                    receiver.close();
                }
            }catch (IOException ignored){

            }

        }
    }

}
