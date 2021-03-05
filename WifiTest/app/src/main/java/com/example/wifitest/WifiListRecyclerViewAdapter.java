package com.example.wifitest;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiListRecyclerViewAdapter  extends RecyclerView.Adapter<WifiListRecyclerViewAdapter.WifiViewHolder>{

    Context mContext;
    List<MyWifiInfo> mData;
    private static final int SIGNAL_LEVELS = 4;

    public WifiListRecyclerViewAdapter(Context context, List<MyWifiInfo> data){
        mContext = context;
        mData = data;
    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WifiViewHolder holder = new WifiViewHolder(LayoutInflater.from(mContext).inflate(R.layout.scan_wifi_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(WifiViewHolder holder, int position) {

        holder.ssid.setText(mData.get(position).result.SSID);
        holder.rssi.setText(String.valueOf(mData.get(position).result.level)+" dBm");

        switch (getLevel(mData.get(position).result.level)){
            case 0:
                holder.wifipic.setImageResource(R.drawable.ic_wifi_lock_signal_1_dark);
                break;
            case 1:
                holder.wifipic.setImageResource(R.drawable.ic_wifi_lock_signal_2_dark);
                break;
            case 2:
                holder.wifipic.setImageResource(R.drawable.ic_wifi_lock_signal_3_dark);
                break;
            case 3:
                holder.wifipic.setImageResource(R.drawable.ic_wifi_lock_signal_4_dark);
                break;
        }

        switch (mData.get(position).supportFreq){
            case 1:
                holder.freq.setText(R.string.wifi_item_freq_2_4G);
                break;
            case 2:
                holder.freq.setText(R.string.wifi_item_freq_5G);
                break;
            case 3:
                holder.freq.setText(R.string.wifi_item_freq_5G_and_24);
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    int getLevel(int mRssi) {
        if (mRssi == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, SIGNAL_LEVELS);
    }


    class WifiViewHolder extends RecyclerView.ViewHolder{

        ImageView wifipic;
        TextView ssid;
        TextView rssi;
        TextView freq;

        public WifiViewHolder(View view){
            super(view);
            wifipic = view.findViewById(R.id.wifi_item_picture);
            ssid = view.findViewById(R.id.wifi_item_ssid);
            rssi = view.findViewById(R.id.wifi_item_rssi);
            freq = view.findViewById(R.id.wifi_item_freq);
        }
    }


}
