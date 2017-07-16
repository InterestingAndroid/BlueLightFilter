package com.android.interesting.bluelightfilter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.Toast;


/**
 * 滤蓝光功能:改变agb值
 *
 * bug:截屏，会截取滤蓝光的布局（所以，最好底层提供接口调整屏幕色温）
 */
public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {


    private SeekBar seekBar;
    private Intent intent;
    private boolean floatWindowPermission = false;
    private  final int FLOAT_WINDOW_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.sb_change_filter_level);
        seekBar.setOnSeekBarChangeListener(this);

        checkPermission();

    }

    /**
     * 悬浮窗权限
     */
    private void checkPermission() {
        if(Build.VERSION.SDK_INT>=23){
            if(Settings.canDrawOverlays(this)){
                floatWindowPermission = true;
                startBlueLightFilterService();
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,FLOAT_WINDOW_REQUEST_CODE);
            }
        }else{
            floatWindowPermission = true;
            startBlueLightFilterService();

            //sdk低于23  默认有悬浮窗权限
            // 但是 华为, 小米,oppo等手机会有自己的一套Android6.0以下  会有自己的一套悬浮窗权限管理 也需要做适配
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FLOAT_WINDOW_REQUEST_CODE: {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(this)) {
                        floatWindowPermission = true;
                        startBlueLightFilterService();
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        floatWindowPermission = false;
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }



    private void startBlueLightFilterService() {
        intent = new Intent(this,BlueLightFilterService.class);
        startService(intent);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(floatWindowPermission){
                intent.putExtra("level",progress);
                startService(intent);
            }



    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
