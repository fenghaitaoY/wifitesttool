package com.example.wifitest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class IperfTestActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "wifitag";
    private ToggleButton mToggleButton;
    private TextView mInterface;
    private TextView mIpAddr;
    private TextView mDeviceName;
    private EditText mCommandEt;
    private TextView mShowConnect;
    private LineChartView lineChart;

    private WifiManager mWifiManager;
    private Handler mHandler;


    private static final int IPERF_Fail = 10000;
    private static final int IPERF_SUCCESS = 10001;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1000;
    private static final String PACKAGE_PATH = "/data/data/com.example.wifitest";

    private static String IPERF_PATH;
    private boolean isDoExecCommand = false;
    private DoIperfTask mIperTask;


    private List<PointValue> values;
    private List<Line> lines;
    private LineChartData lineChartData;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private List<PointValue> points;
    private Axis axisX, axisY;

    private int position = 0;
    private Timer timer;
    private Random random = new Random();
    private boolean isFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iperf_test);

        initView();


        mWifiManager =(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mInterface.setText("interface: " +getProperty("wifi.interface", "wlan0"));
        mDeviceName.setText("device: "+Build.DEVICE);
        mIpAddr.setText("ipaddr: "+getCurrentIp());

        mHandler = new MyHandelr();

        ensure_iperf_exist();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView(){
        mToggleButton = findViewById(R.id.toggleButton);
        mInterface = findViewById(R.id.tv_interface);
        mIpAddr = findViewById(R.id.tv_ipaddr);
        mDeviceName = findViewById(R.id.tv_device);
        mCommandEt = findViewById(R.id.et_command);
        mShowConnect = findViewById(R.id.tv_connect);
        mToggleButton.setOnCheckedChangeListener(this);
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, " togglebutton click");
            }
        });

        lineChart = findViewById(R.id.chart);

        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();
        axisY = new Axis();
        axisY.setLineColor(Color.GREEN);
        axisY.setTextColor(Color.BLACK);
        axisY.setName("Value");


        axisX = new Axis();
        axisX.setLineColor(Color.GREEN);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("Time");
        lineChartData = initDatas(null);
        lineChart.setLineChartData(lineChartData);

        Viewport port = initViewPort(0,20);
        lineChart.setCurrentViewportWithAnimation(port);
        lineChart.setInteractive(false);
        lineChart.setScrollEnabled(true);
        lineChart.setValueTouchEnabled(true);
        lineChart.setFocusableInTouchMode(true);
        lineChart.setViewportCalculationEnabled(false);
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.startDataAnimation();

        points = new ArrayList<>();


    }


    private LineChartData initDatas(List<Line> lines){
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }

    /**
     * 显示当前区域
     * @param left
     * @param right
     * @return
     */
    private Viewport initViewPort(float left, float right){
        Viewport port = new Viewport();
        port.top = 60;
        port.bottom=0;
        port.left= left;
        port.right=right;
        return port;
    }


    /**
     * 最大显示区域
     * @param right
     * @return
     */
    private Viewport initMaxViewPort(float right){
        Viewport port = new Viewport();
        port.top=60;
        port.bottom=0;
        port.left=0;
        port.right=right+20;
        return port;
    }


    private void drawLinePort(DataBean bean){
        Log.i(TAG,"drawLinePort endtime"+Float.valueOf(bean.endtime)+" , bitrate ="+Float.valueOf(bean.bitrate.split(" ")[0]));

        if(bean.endtime.isEmpty() || bean.bitrate.isEmpty()){
          //  return;
        }

        PointValue value1 = new PointValue(Float.valueOf(bean.endtime), Float.valueOf(bean.bitrate.split(" ")[0]));
        value1.setLabel(bean.bitrate);
        pointValueList.add(value1);

        float x = value1.getX();
        Line line = new Line(pointValueList);
        line.setColor(Color.RED);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);
        line.setHasLabels(true);
        line.setHasPoints(true);

        linesList.clear();
        linesList.add(line);
        lineChartData = initDatas(linesList);
        lineChart.setLineChartData(lineChartData);
        Viewport port;
        if (x > 20){
            port = initViewPort(x-20, x);
        }else {
            port = initViewPort(0, 20);
        }
        lineChart.setCurrentViewport(port);

        Viewport maPort = initMaxViewPort(x);
        lineChart.setMaximumViewport(maPort);
    }

    /**
     * 图表绘制结束，设置可以交互，左右拉
     */
    private void interactive(){
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i(TAG, "toggleButton  check changed : "+isChecked);
        if (!isDoExecCommand) {
            isDoExecCommand = true;
            if (mIperTask == null) {
                mIperTask = new DoIperfTask();
                mIperTask.execute();


                linesList.clear();
                pointValueList.clear(); //解决多次绘制有残影问题
                lineChartData = initDatas(null);
                lineChart.setLineChartData(lineChartData);
            }

        }else {
            mIperTask.cancel(true);
            mIperTask = null;
            isDoExecCommand = false;
        }

    }

    private void ensure_iperf_exist(){
        IPERF_PATH = PACKAGE_PATH + "/bin/iperf";
        File file = new File(IPERF_PATH);
        if (!file.exists()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    copyIperfToPath();
                }
            });
        }
    }


     class MyHandelr extends Handler{
         @Override
         public void handleMessage(@NonNull Message msg) {
             super.handleMessage(msg);
             switch (msg.what){
                 case IPERF_Fail:
                     Log.i(TAG,"IPERF FAIL"+msg.obj);
                     break;
                 case IPERF_SUCCESS:
                     Log.i(TAG,"IPERF SUCCESS:"+msg.obj);
                     break;

             }
         }
     }


    class DoIperfTask extends AsyncTask<Void, String, String>{

        Process process = null;
        String command;
        StringBuffer sb;
        int i=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            command = mCommandEt.getText().toString();
            sb = new StringBuffer();
            i=0;

        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.i(TAG, "do in background ");
            try {
                if (command.isEmpty()) {
                    return null;
                }

                if (!command.matches("(iperf )?((-[s,-server])|(-[c,-client] ([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]))|(-[c,-client] \\w{1,63})|(-[h,-help]))(( -[f,-format] [bBkKmMgG])|(\\s)|( -[l,-len] \\d{1,5}[KM])|( -[B,-bind] \\w{1,63})|( -[r,-tradeoff])|( -[v,-version])|( -[N,-nodelay])|( -[T,-ttl] \\d{1,8})|( -[U,-single_udp])|( -[d,-dualtest])|( -[w,-window] \\d{1,5}[KM])|( -[n,-num] \\d{1,10}[KM])|( -[p,-port] \\d{1,5})|( -[L,-listenport] \\d{1,5})|( -[t,-time] \\d{1,8})|( -[i,-interval] \\d{1,4})|( -[u,-udp])|( -[b,-bandwidth] \\d{1,20}[bBkKmMgG])|( -[m,-print_mss])|( -[P,-parallel] d{1,2})|( -[M,-mss] d{1,20}))*")) {
                    return null;
                }

                Log.i(TAG, "Command=" + command);

                String[] arrayCommand = command.split(" ");
                List<String> commandList = new ArrayList<>(Arrays.asList(arrayCommand));

                if (commandList.get(0).equals("iperf")) {
                    commandList.remove(0);
                }


                //先启动iperf监听
                ReadIperfDataHelper helper = ReadIperfDataHelper.getInstance();
                helper.setOnActionDataListener(new ReadIperfDataHelper.IperfActionListener() {
                    @Override
                    public void onAction(String action) {
                        Log.i(TAG, "=====ACTION=" + action);
                        publishProgress(action);
                    }
                });
                helper.startServer();

                //启动iperf
                commandList.add(0, PACKAGE_PATH + "/bin/iperf");
                process = new ProcessBuilder().command(commandList).redirectErrorStream(true).start();
                Log.i(TAG, "do in background end ...");
                process.waitFor();
                process.destroy();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.i(TAG, "progress values: "+values[0]);

            //mShowConnect.setText(sb.append(values[0]+"\n"));
            if (values[0].contains("bits/sec")) {
                DataBean bean = ParseIperDataHelper.parseData(values[0]);
                drawLinePort(bean);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "post execute ");
            if(process != null){
                Log.i(TAG, " post execut process destroy");
                process.destroy();
                try {
                    process.waitFor();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            ReadIperfDataHelper.getInstance().stop();

            mToggleButton.setChecked(false);

            interactive();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i(TAG, "on Cancel");
            if(process != null){
                process.destroy();
                try {
                    process.waitFor();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            ReadIperfDataHelper.getInstance().stop();
            mToggleButton.setChecked(false);

            interactive();
        }
    }


    /**
     * 拷贝iperf bin文件
     */
    private void copyIperfToPath() {
        File localfile;

        Process p;
        try {

            localfile = new File(PACKAGE_PATH+"/bin");
            Log.i(TAG, PACKAGE_PATH+" , exist="+localfile.exists());
            if (!localfile.exists()){
                try {
                    localfile.mkdirs();
                }catch (Exception e){

                }
            }
            localfile = new File(PACKAGE_PATH+"/bin/iperf");
            if (!localfile.exists()){
                localfile.createNewFile();
            }

            p = Runtime.getRuntime().exec("/system/bin/chmod 777 " + localfile.getAbsolutePath());
            InputStream localInputStream = getAssets().open("iperf");
            Log.i(TAG,"chmod 777 " + localfile.getAbsolutePath());
            FileOutputStream localFileOutputStream = new FileOutputStream(localfile.getAbsolutePath());
            FileChannel fc = localFileOutputStream.getChannel();
            FileLock lock = fc.tryLock(); //给文件设置独占锁定
            if (lock == null) {
                Toast.makeText(this,"has been locked !",Toast.LENGTH_SHORT).show();
                return;
            } else {
                FileOutputStream fos = new FileOutputStream(new File(IPERF_PATH));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = localInputStream.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                    Log.i(TAG, "byteCount: "+byteCount);
                }
                fos.flush();// 刷新缓冲区
                localInputStream.close();
                fos.close();

            }
            //两次才能确保开启权限成功
            p = Runtime.getRuntime().exec("/system/bin/chmod 777 " + localfile.getAbsolutePath());
            p.waitFor();
            lock.release();
            localFileOutputStream.close();
            p.destroy();
            Log.i(TAG, "the iperf file is ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 通过反射获得 系统属性值
     * @param key
     * @param defaultValue
     * @return
     */
    public String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, "unknown" ));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return value;
        }
    }

    /**
     * 获取当前已经连接wifi的ip地址
     * @return
     */
    private String getCurrentIp(){
        int paramInt=0;
        WifiInfo info = mWifiManager.getConnectionInfo();
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
            paramInt = info.getIpAddress();

            Log.i(TAG, "paramInt:"+ paramInt+" 0xFF&paramInt"+(0xFF & paramInt));
            return new StringBuffer().append(0xFF & paramInt).append(".")
                                        .append(0xFF & paramInt >> 8).append(".")
                                        .append(0xFF & paramInt >> 16).append(".")
                                        .append(0xFF & paramInt >> 24).toString();
        }else{
            return "请连接wifi";
        }


    }



}
