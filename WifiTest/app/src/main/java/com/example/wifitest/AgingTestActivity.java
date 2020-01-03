package com.example.wifitest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class AgingTestActivity extends AppCompatActivity {

    private static final String TAG = "wifitag";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1000;

    private TextView mSwitchTextview;
    private Switch mWifiSwitch;
    private TextView mSSIDTextView;
    private TextView mPasswordTextView;
    private TextView mSuccessTextview;
    private TextView mFailTextview;
    private TextView mScanWifiCount;
    private RecyclerView mRecycler;
    private EditText mCountEdit;
    private Button mTestButton;
    private Button mResetButton;

    public static final int WIFI_SWITCH =1000;
    public static final int WIFI_TEST_STOP=1001;

    private WifiManager  mWifiManager;
    private WifiListRecyclerViewAdapter mAdapter;
    private List<ScanResult> mData;
    WifiBroadCastReceiver broadCastReceiver;
    private SharedPreferences sharedPreferences;
    ConnectivityManager connManager;
    NetworkInfo networkInfo;

    private DividerItemDecoration mDivider;
    private int mTestCount=0;
    private boolean isTestStart=false;
    private boolean isScanResult=false;
    private boolean isConnected = false;
    private boolean isBootReceiveStart = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case WIFI_SWITCH:
                    sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                    int count = sharedPreferences.getInt("success",0)+ sharedPreferences.getInt("fail", 0);
                    Log.i(TAG, " handlerMessage performClic  isTestStart ="+isTestStart + ", count ="+count);
                    if (isTestStart && count < mTestCount) {
                        mWifiSwitch.performClick();
                    }
                    break;
                case WIFI_TEST_STOP:
                    isTestStart = false;
                    mTestButton.setText(R.string.test_start);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isBootReceiveStart = getIntent().getBooleanExtra("boot", false);
        Log.i(TAG, " isBootreceiveStart = "+isBootReceiveStart);

        mWifiManager =(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initView();

        broadCastReceiver = new WifiBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadCastReceiver, filter);
        mData = new ArrayList<>();

        setListener();
    }

    private void initView(){
        mSwitchTextview = findViewById(R.id.switch_tv);
        mWifiSwitch = findViewById(R.id.switch_wifi);
        mSSIDTextView = findViewById(R.id.tv_ssid);
        mPasswordTextView = findViewById(R.id.tv_psw);
        mSuccessTextview = findViewById(R.id.tv_success);
        mFailTextview = findViewById(R.id.tv_fail);
        mRecycler = findViewById(R.id.recycler);
        mCountEdit = findViewById(R.id.et_count);
        mTestButton = findViewById(R.id.test_bt);
        mResetButton = findViewById(R.id.test_reset);
        mScanWifiCount = findViewById(R.id.tv_scan_wifi_count);

        //添加分隔线
        mDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDivider.setDrawable(getDrawable(R.drawable.recycle_divider_line_shape));
    }

    private void setListener(){

        mAdapter = new WifiListRecyclerViewAdapter(this, mData);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mAdapter);

        mWifiSwitch.setChecked(mWifiManager.isWifiEnabled());
        mWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG, " Switch = "+b+ ", main thread ="+Thread.currentThread().getName());
                final boolean changed = b;
                if (b) {
                    mSwitchTextview.setText(R.string.open);
                }else{
                    mSwitchTextview.setText(R.string.close);
                    isConnected = false;
                    isScanResult = false;
                }
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        boolean isOpen = mWifiManager.setWifiEnabled(changed);
                        Log.i(TAG," WIFI isOpen ="+isOpen+" thread id ="+Thread.currentThread().getName());
                    }
                }.start();


            }
        });


        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mCountEdit.getEditableText().toString())){
                    mTestCount = Integer.valueOf(mCountEdit.getText().toString());
                }else {
                    return;
                }
                if (isTestStart){
                    isTestStart = false;
                    mTestButton.setText(R.string.test_start);
                }else{
                    isTestStart = true;
                    mTestButton.setText(R.string.test_stop);
                    new MyThread().start();
                }
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTestCount=0;
                mCountEdit.setText("");
                isTestStart=false;
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putInt("success", 0);
                editor.putInt("fail", 0);
                editor.commit();
                updateResult();
                if (mWifiSwitch.isChecked()){
                    mWifiSwitch.performClick();
                }
            }
        });

    }

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        }

        getSSIDInfo();
        updateResult();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTestStart=false;
        unregisterReceiver(broadCastReceiver);
    }

    private void updateResult(){
        mSuccessTextview.setText(String.format(getResources().getString(R.string.success),sharedPreferences.getInt("success",0)));
        mFailTextview.setText(String.format(getResources().getString(R.string.fail),sharedPreferences.getInt("fail",0)));
    }

    private void  getSSIDInfo(){
        connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Log.i(TAG, " isconnected ="+networkInfo.isConnected());
            if (networkInfo.isConnected()) {
                final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null) {
                    String ssid = connectionInfo.getSSID();
                    if (Build.VERSION.SDK_INT >= 17 && ssid.startsWith("\"") && ssid.endsWith("\""))
                        ssid = ssid.replaceAll("^\"|\"$", "");
                    mSSIDTextView.setText(ssid);
                }
            }

            updateResult();
    }


    private static void removeDuplicate(List<String> list) {
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }

    class WifiBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "onReceive ACTION ="+intent.getAction());
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:  //开关变化通知
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_DISABLED);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            Log.i(TAG, " disable ");
                            mSSIDTextView.setText("");
                            mData.clear();
                            mAdapter.notifyDataSetChanged();
                            mScanWifiCount.setText(String.format(getResources().getString(R.string.scan), mData.size()));
                            Log.i(TAG,"-------------------------------------------------wifi state send message ------");
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Log.i(TAG, " enable ");
                            mWifiManager.startScan();
                            break;
                    }


                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION: //扫描结果通知
                    Log.i(TAG, " scan action ");
                    List<ScanResult> Results = mWifiManager.getScanResults();
                    mData.clear();
                    mRecycler.removeItemDecoration(mDivider);

                    //数据排序，去重
                    Collections.sort(Results, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult rt1, ScanResult rt2) {
                            return Math.abs(rt1.level) - Math.abs(rt2.level);
                        }
                    });

                    List<ScanResult> temps = new ArrayList<>();
                    for (ScanResult result : Results){
                        if (!result.SSID.isEmpty() && !temps.contains(result)){
                            temps.add(result);
                        }
                    }
                    Results.clear();
                    Results.addAll(temps);

                    mData.addAll(Results);
                    mScanWifiCount.setText(String.format(getResources().getString(R.string.scan), mData.size()));
                    mRecycler.addItemDecoration(mDivider);
                    mAdapter.notifyDataSetChanged();
                    if (Results.size() > 0 && mWifiSwitch.isChecked()){
                        isScanResult = true;
                    }
                    Log.i(TAG, " result ="+Results.size());
                    for (ScanResult result : Results) {
                        Log.i(TAG, " wifi 结果 : ssid =" + result.SSID + ", RSSI=" + result.level);
                    }
                    Log.i(TAG,"-------------------------------------------------scan send message ------");

                    break;

                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION: //连接结果通知
                    Log.i(TAG," supplicant state change ");
                    break;

                case WifiManager.NETWORK_STATE_CHANGED_ACTION: //网络状态变化通知
                    Log.i(TAG,"network change ");
                    break;
                case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
                    Log.i(TAG,"supplicant connection change ");
                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION: //wifi连接通知
                    Log.i(TAG,"connectivity action ");

                    sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (mWifiSwitch.isChecked()) {
                        connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (networkInfo.isConnected()) {
                            isConnected = true;
                            editor.putInt("success", sharedPreferences.getInt("success", 0) + 1);
                            //mWifiManager.startScan();
                            List<ScanResult> scanResults = mWifiManager.getScanResults();
                            Log.i(TAG, "scanResults size =" + scanResults.size());
                        } else {
                            editor.putInt("fail", sharedPreferences.getInt("fail", 0) + 1);
                        }
                        editor.commit();
                    }


                    getSSIDInfo();

                    break;
            }
        }
    }


    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (isTestStart){
                try {
                    sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "------------Thread  loop--------switch ="+mWifiSwitch.isChecked()+", isConnected ="+isConnected +" , isScanResult="+isScanResult );
                if (mWifiSwitch.isChecked() && isConnected && isScanResult) {
                    Message message = Message.obtain();
                    message.what = WIFI_SWITCH;
                    mHandler.sendMessage(message);
                }else if (!mWifiSwitch.isChecked() && !isConnected && ! isScanResult){
                    Message message = Message.obtain();
                    message.what = WIFI_SWITCH;
                    mHandler.sendMessage(message);
                }
                sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                int count = sharedPreferences.getInt("success",0)+ sharedPreferences.getInt("fail", 0);
                Log.i(TAG," Thread loop count ="+count+", mTestCount="+mTestCount);
                if (count == mTestCount){
                    Message message = Message.obtain();
                    message.what = WIFI_TEST_STOP;
                    mHandler.sendMessage(message);
                }
            }
        }
    }


}
