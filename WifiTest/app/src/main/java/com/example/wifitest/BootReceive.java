package com.example.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("WIFITEST","on boot receiver ");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent mainIntent = new Intent(context, AgingTestActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainIntent.putExtra("boot", true);
            context.startActivity(mainIntent);
        }
    }
}
