package com.example.wifitest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1000;
    private CardView mGuideAging;
    private CardView mGuideIperf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_wifi_test);


        mGuideAging = findViewById(R.id.guide_aging);
        mGuideIperf = findViewById(R.id.guide_iperf);


        mGuideIperf.setOnClickListener(this);
        mGuideAging.setOnClickListener(this);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        }*/


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guide_aging:
                Intent intentAging = new Intent(this, AgingTestActivity.class);
                startActivity(intentAging);
                break;
            case R.id.guide_iperf:
                Intent intentIperf = new Intent(this, IperfTestActivity.class);
                startActivity(intentIperf);
                break;


        }
    }
}
