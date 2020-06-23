package com.alex.com.alex.livertmppushsdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

public class SettingActivity extends Activity implements View.OnClickListener {
    public static final String RTMPURL_MESSAGE = "com.alex.com.alex.livertmppushsdk.demo.rtmpurl";



    private void InitUI(){
        findViewById(R.id.timeonoff).setOnClickListener(this);
        findViewById(R.id.reboot).setOnClickListener(this);
        findViewById(R.id.shutdown).setOnClickListener(this);
        findViewById(R.id.setAutoTime).setOnClickListener(this);
        findViewById(R.id.setAutoTimeZone).setOnClickListener(this);
        findViewById(R.id.setTimeZone).setOnClickListener(this);
        findViewById(R.id.getSeriNo).setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        InitUI();

        showInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.timeonoff:
                timeOnOff();
                break;
            case R.id.reboot:
                reboot();
                break;
            case R.id.shutdown:
                shutdown();
                break;
            case R.id.setAutoTime:
                setAutoTime();
                break;
            case R.id.setAutoTimeZone:
                setAutoTimeZone();
                break;
            case R.id.setTimeZone:
                setTimeZone();
                break;
            case R.id.getSeriNo:
                getSeriNo();
                break;
        }
    }

    private void getSeriNo() {
         Toast.makeText(this, Build.SERIAL,Toast.LENGTH_LONG).show();
    }

    private void setAutoTime() {
        Intent intent = new Intent("com.mrk.auto.time" );
        intent.putExtra( "auto_time","1");
        sendBroadcast(intent);
    }

    private void setTimeZone() {
        Intent intent = new Intent("com.mrk.set.time.zone" );
        intent.putExtra( "time_zone","Asia/Shanghai"  );
         sendBroadcast(intent);
    }

    private void setAutoTimeZone() {
        Intent intent = new Intent("com.mrk.auto.time.zon" );
        intent.putExtra( "auto_time_zone","1"  );
        sendBroadcast(intent);
    }

    private void shutdown() {
        Intent intent = new Intent("shutdown.zysd.now" );
         sendBroadcast(intent);
    }

    private void reboot() {
        Intent intent = new Intent("reboot.zysd.now" );
         sendBroadcast(intent);
    }

    private void timeOnOff() {
        Intent intent = new Intent("com.mrk.setpoweronoff" );
// 用于设置本次关机时间，表示 2018/1/1 8:30 关机
        int[] timeoff= {2014,1,1,8,30};
//用于设置下次开机时间， 表示 2018/1/1 20:00 开机
        int[] timeon = {2014,1,1,20,30};
        intent.putExtra("timeon", timeon);
        intent.putExtra("timeoff", timeoff);
        intent.putExtra("enable" ,true); //使能开关机功能， 设为 false,则为关闭，true 为打开
        sendBroadcast(intent);
    }




    private void showInfo() {

        TextView text = (TextView) findViewById(R.id.textView1);

        String phoneInfo = "Product: " + android.os.Build.PRODUCT + "\n";
        phoneInfo += ", 内核版本: " + getLinuxCore_Ver() + "\n";
        phoneInfo += ", 内部版本: " + getInner_Ver() + "\n";

        phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI + "\n";
        phoneInfo += ", TAGS: " + android.os.Build.TAGS + "\n";
        phoneInfo += ", VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE + "\n";
        phoneInfo += ", MODEL: " + android.os.Build.MODEL + "\n";
        phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK + "\n";
        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE + "\n";
        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE + "\n";
        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY + "\n";
        phoneInfo += ", BRAND: " + android.os.Build.BRAND + "\n";
        phoneInfo += ", BOARD: " + android.os.Build.BOARD + "\n";
        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n";
        phoneInfo += ", ID: " + android.os.Build.ID + "\n";
        phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER + "\n";
        phoneInfo += ", USER: " + android.os.Build.USER + "\n";
        phoneInfo += ", BOOTLOADER: " + android.os.Build.BOOTLOADER + "\n";
        phoneInfo += ", HARDWARE: " + android.os.Build.HARDWARE + "\n";
        phoneInfo += ", INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL + "\n";
        phoneInfo += ", CODENAME: " + android.os.Build.VERSION.CODENAME + "\n";
        phoneInfo += ", SDK: " + android.os.Build.VERSION.SDK_INT + "\n";
        text.setText(phoneInfo);

    }


    /**
     * BASEBAND-VER
     * 基带版本
     * return String
     */

    public static String getBaseband_Ver(){
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[] { String.class,String.class });
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
// System.out.println(">>>>>>><<<<<<<" +(String)result);
            Version = (String)result;
        } catch (Exception e) {
        }
        return Version;
    }

    /**
     * CORE-VER
     * 内核版本
     * return String
     */

    public static String getLinuxCore_Ver() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }


// get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);


        String result = "";
        String line;
// get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }

    /**
     * INNER-VER
     * 内部版本
     * return String
     */

    public static String getInner_Ver(){
        String ver = "" ;

        if(android.os.Build.DISPLAY .contains(android.os.Build.VERSION.INCREMENTAL)){
            ver = android.os.Build.DISPLAY;
        }else{
            ver = android.os.Build.VERSION.INCREMENTAL;
        }
        return ver;

    }
}
