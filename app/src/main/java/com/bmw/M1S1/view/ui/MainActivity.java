package com.bmw.M1S1.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bmw.M1S1.R;
import com.bmw.M1S1.model.Login_info;
import com.bmw.M1S1.utils.LogUtil;
import com.bmw.M1S1.utils.UdpSocketUtil;
import com.bmw.M1S1.utils.WifiAdmin;
import com.hikvision.netsdk.HCNetSDK;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private Login_info login_info;
    private int key_back;
    private long key_back_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        login_info =  Login_info.getInstance();  //单例模式，初始化；
        initSDK();

    }

    private void initSDK() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            error("海康：HCNetSDK init is failed!");
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "海康：/mnt/sdcard/sdklog/",
                true);
        log("海康：HCNetSDK init is success!");
    }

    @Override
    protected void onResume() {
        wifiAuto();
        super.onResume();
    }

    private void wifiAuto() {
        if(login_info.isWifi_auto()){
            WifiAdmin wifiAdmin = new WifiAdmin(this);
            String ssid = login_info.getWifi_SSID();
            wifiAdmin.openWifi();
            wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(ssid, login_info.getWifi_Password(), 3));

        }
    }

    @OnClick({R.id.pre_view, R.id.play_back, R.id.picture, R.id.environment_stat, R.id.setting})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.pre_view:
                    intent = new Intent(this, PreviewActivity.class);
                break;
            case R.id.play_back:
                intent = new Intent(this, FileActivity.class);
                intent.putExtra("picture", false);
                break;
            case R.id.picture:
                intent = new Intent(this, FileActivity.class);
                break;
            case R.id.environment_stat:
                    intent = new Intent(this, EnvironmentActivity.class);
                break;
            case R.id.setting:
                intent = new Intent(this, SettingActivity.class);
                break;
        }

        if (intent != null)
            startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        controlInit.release();
//        M1S1_TCP_SocketUtil.getInstance().release();
        HCNetSDK.getInstance().NET_DVR_Cleanup();
        ButterKnife.unbind(this);
    }

    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if((System.currentTimeMillis() - key_back_time)>=5000){
                key_back = 0;
            }
            if (key_back != 1) {
                toast("再按一次返回键退出客户端！");
                key_back_time = System.currentTimeMillis();
            }
            if (key_back == 1) {
                mToast.cancel();
                System.exit(0);
            }
            key_back++;

            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
