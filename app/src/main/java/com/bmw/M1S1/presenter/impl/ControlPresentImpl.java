package com.bmw.M1S1.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.bmw.M1S1.BaseApplication;
import com.bmw.M1S1.R;
import com.bmw.M1S1.presenter.ControlPresenter;
import com.bmw.M1S1.utils.LogUtil;
import com.bmw.M1S1.utils.UdpSocketUtil;
import com.bmw.M1S1.view.viewImpl.PreviewControlImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/9/5.
 */
public class ControlPresentImpl implements ControlPresenter {

    private PreviewControlImpl cView;
    //    private byte[] commands;
//    private M1S1_TCP_SocketUtil socketUtil;
    private String actionName;
    private int mSpeed;
    private boolean isGetCommand = true;
    private ScheduledExecutorService scheduledExecutorService;
    private static final int PTZ_START = 7
            ,PTZ_LIGHT_START = 10
            ,CAMERA_START = 14
            ,LIGHT_FRONT_START = 17
            ,ZITAI_START = 21
            ,TUIGAN_START = 36
            ,TUIGAN_GET_START = 40
            ,BANBENHAO_START = 44
            ,MOVE_CONTROL_START = 48
            ,JIMICHI_BIAODING_START = 53
            ,JIMIQI_GET_START = 57
            ,BATTERY_START = 64
            ,SHOUXIAN_START = 68
            ,SHOUXIAN_SET_START = 72
            ,IP_GET_START = 76
            ,ALARM_START = 83;

    byte[] commands = new byte[]{(byte) 0xa5, (byte) 0x5a, (byte) 0x00, (byte) 0x5c, (byte) 0x10,
            (byte) 0x03, (byte) 0x01, (byte) 0x1c,   //云台动作7
            (byte) 0x04, (byte) 0x01, (byte) 0x1b, (byte) 0x00, //云台灯光10
            (byte) 0x03, (byte) 0x03, (byte) 0x30,   //摄像转换14
            (byte) 0x04, (byte) 0x03, (byte) 0x32, (byte) 0x00,    //前车灯调光17
            (byte) 0x0f, (byte) 0x83, (byte) 0x34, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   //传感器数据获取21
            (byte) 0x04, (byte) 0x03, (byte) 0x35, (byte) 0x03,    //推杆控制36
            (byte) 0x04, (byte) 0x83, (byte) 0x36, (byte) 0x00,    //推杆高度获取40
            (byte) 0x04, (byte) 0x83, (byte) 0x37, (byte) 0x00,     //版本号获取44
            (byte) 0x05, (byte) 0x05, (byte) 0x51, (byte) 0x05, (byte) 0x00, //移动控制48
            (byte) 0x04, (byte) 0x07, (byte) 0x71, (byte) 0x00,    //计米尺标定53
            (byte) 0x07, (byte) 0x87, (byte) 0x72, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,   //计米尺数据获取57
            (byte) 0x04, (byte) 0x87, (byte) 0x73, (byte) 0x00,    // 电量获取64
            (byte) 0x04, (byte) 0x07, (byte) 0x75, (byte) 0x00,    //执行电动收线68
            (byte) 0x04, (byte) 0x07, (byte) 0x76, (byte) 0x05,     //电动收线速度设置72
            (byte) 0x07, (byte) 0x89, (byte) 0x91, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, //控制终端ip地址76
            (byte) 0x0c, (byte) 0x89, (byte) 0x92, (byte) 0x00, (byte) 0xff, (byte) 0x00, (byte) 0x19, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 //报警83
    };


    private boolean isQxAlarm;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] bytes = (byte[]) msg.obj;
            int batteryNum = Integer.valueOf(bytes[BATTERY_START+1]);
            LogUtil.log("接收：成功-获取电量：" + batteryNum);

            int zitai_s = ZITAI_START;

            float pitchAngle = ((bytes[zitai_s+3] & 0xff) << 8 | (bytes[zitai_s+4]) & 0xff) & 0xffff;
            float rollAngle = ((bytes[zitai_s+5] & 0xff) << 8 | (bytes[zitai_s+6]) & 0xff) & 0xffff;
            pitchAngle = ((bytes[zitai_s+3] & 0xff) >> 7) == 0 ? pitchAngle : (32768 - pitchAngle);
            rollAngle = ((bytes[zitai_s+5] & 0xff) >> 7) == 0 ? rollAngle : (32768 - rollAngle);

            int qiya_num = ((bytes[zitai_s+11] & 0xff) << 8) | (bytes[zitai_s+12] & 0xff);
            float air_pressure = getQiya(qiya_num);
            LogUtil.log("接收气压：气压初始值："+qiya_num+" 气压处理值："+air_pressure);

            //32768
            pitchAngle = pitchAngle / 10;
            rollAngle = rollAngle / 10;
            LogUtil.log("接收：成功-获取姿态数据！" + "pitch = " + pitchAngle + "  rollangle = " + rollAngle);


            float height = Float.valueOf(bytes[TUIGAN_GET_START+1] & 0xff);
            height = height / 10;
            LogUtil.log("接收：成功-获取升降高度：" + height);

            //云台灯光
            int ptz_light_strength = bytes[PTZ_LIGHT_START+1] & 0xff;

            //前灯灯光
            int front_light_strength = bytes[LIGHT_FRONT_START+1] & 0xff;

            //移动速度
            int move_speed = bytes[MOVE_CONTROL_START+2] & 0xff;

            int ip_s = IP_GET_START;
            String control_ip = (bytes[ip_s+1] & 0xff) + "." + (bytes[ip_s+2] & 0xff) + "." + (bytes[ip_s+3] & 0xff) + "." + (bytes[ip_s+4] & 0xff);


            int alarmInfo = (bytes[ALARM_START+1] & 0xff);
            if (alarmInfo > 0) {
                LogUtil.log("警告检查: control " + alarmInfo + " byte[87]= " + (bytes[87] & 0xff));

            }
            if (alarmInfo > 0) {
                for (int i = 0; i < bytes.length; i++) {
                    LogUtil.log("警告检查 byte[" + i + "] = " + Integer.toHexString(bytes[i] & 0xff));
                }
            }

            if (isQxAlarm && alarmInfo == 0) {
                commands[ALARM_START+2] = (byte) 0xff;
                isQxAlarm = false;
            }

            /*
            if (!isQxAlarm)
                commands[90] = bytes[90];
            if ((bytes[90] & 0xff) > 0)
                isQxAlarm = true;
            else
                isQxAlarm = false;
*/
            int jimi_s = JIMIQI_GET_START;
            int jiMiQi_mm = ((bytes[jimi_s+1] & 0xff) | (bytes[jimi_s+2] & 0xff) << 8 | (bytes[jimi_s+3] & 0xff) << 16 | (bytes[jimi_s+4] & 0xff) << 24) & 0xffffffff;

            float jiMiQi_m = (float) jiMiQi_mm/1000;
            if (cView != null)
                cView.setReturnData(
                        chooseBatteryIcon(batteryNum),
                        pitchAngle, rollAngle, height,
                        ptz_light_strength, front_light_strength,
                        move_speed, control_ip, air_pressure, alarmInfo, jiMiQi_m
                );

            bytes = null;

/*

            NET_DVR_SHOWSTRING_V30 showString =   new NET_DVR_SHOWSTRING_V30();
            HCNetSDK.getInstance().NET_DVR_GetDVRConfig(All_id_Info.getInstance().getM_iLogID(),
                    HCNetSDK.NET_DVR_GET_SHOWSTRING_V30,
                    All_id_Info.getInstance().getM_iChanNum(),showString);
            byte[] zifus = showString.struStringInfo[0].sString;
            for(byte b:zifus){

                LogUtil.log("字符叠加：1 = "+ b);
            }
*/

        }

    };

    private UdpSocketUtil udpSocketUtil;

    private float getQiya(int qiya) {

        if (qiya < 0) {
            qiya = 0 - qiya;
        }
        if(qiya == 0)
            return 0;
        float result = (float) ((qiya * 10.0f - 101325.0) * 0.1450377F / 1000.0f);
        return (float) (Math.round(result * 100)) / 100;

    }


    public ControlPresentImpl(PreviewControlImpl cView, Context context) {
        this.cView = cView;
//        socketUtil = M1S1_TCP_SocketUtil.getInstance();
        scheduledExecutorService = Executors.newScheduledThreadPool(3);
        udpSocketInit();
//        socketUtil = M1S1_TCP_SocketUtil.getIntance();
//        socketUtil.initSocket();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                actionName = "定时发送请求";
                getReader();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        SharedPreferences sharedPreferences = BaseApplication.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int alarm_angle = sharedPreferences.getInt(BaseApplication.ALARMSET,0);
        if(alarm_angle == 0){
            editor.putInt(BaseApplication.ALARMSET,25);
            editor.commit();
        }else if(alarm_angle != 25){
            setQingfuAlarm(alarm_angle);
        }

    }

    private void udpSocketInit(){

        udpSocketUtil = new UdpSocketUtil();
        udpSocketUtil.setOnDataReadListener(new UdpSocketUtil.OnDataReadListener() {
            @Override
            public void read(byte[] bytes) {
                Message msg = new Message();
                msg.obj = bytes;
                handler.sendMessage(msg);
            }
        });
    }
/*
    private void tcpSocketInit(){

        socketUtil = new M1S1_TCP_SocketUtil();
        socketUtil.setOnDataReaderListener(new M1S1_TCP_SocketUtil.OnDataReaderListener() {
            @Override
            public void result(byte[] bytes) {

                Message msg = new Message();
                msg.obj = bytes;
                handler.sendMessage(msg);
            }
        });
    }*/
    /*
    @Override
    public boolean isSocketNull() {
        return socketUtil.isSocketNull();
    }
*/

    private void getReader() {
        if (isGetCommand)
//            socketUtil.getReader(commands, actionName);
        udpSocketUtil.send(commands,"命令");
    }

    private synchronized void set_commands(String name, int command, int arg_count, int[] args,int command_start) {

        actionName = "动作" + name;

        LogUtil.log("动作-按下命令：------- " + actionName);

        commands[command_start] = (byte) command;
        if (arg_count > 1) {
            for (int i = 0; i < arg_count - 1; i++) {
                commands[command_start + i + 1] = (byte) args[i];
            }
        }
    }

    private void ptz_command(String name, int command_num) {
        set_commands(name,  command_num, 0x01, new int[]{},PTZ_START);
    }

    @Override
    public void setQingfuAlarm(int angle) {

        int angleLow = (angle & 0x00ff);
        int angleHight = (angle >> 8);

        set_commands("倾覆警报设置",0x92, 0x05, new int[]{0x00, 0xff, angleHight, angleLow},ALARM_START);
    }

    @Override
    public void qxAlarm() {
        isQxAlarm = true;
        set_commands("取消倾覆警报",  0x92, 0x03, new int[]{0x00, 0x00},ALARM_START);
    }

    @Override
    public void ptz_up() {
        ptz_command("云台-向上", 0x13);
    }

    @Override
    public void ptz_down() {
        ptz_command("云台-向下", 0x12);
    }

    @Override
    public void ptz_left() {
        ptz_command("云台-向左", 0x10);
    }

    @Override
    public void ptz_right() {
        ptz_command("云台-向右", 0x11);
    }

    @Override
    public void ptz_zooom_add() {
        ptz_command("云台-变倍远", 0x17);
    }

    @Override
    public void ptz_zoom_sub() {
        ptz_command("云台-变倍近", 0x16);
    }

    @Override
    public void ptz_size_add() {
        ptz_command("云台-聚焦远", 0x15);
    }

    @Override
    public void ptz_size_sub() {
        ptz_command("云台-聚焦近", 0x14);

    }

    @Override
    public void ptz_stop() {
        set_commands("云台-停止", 0x1c, 0x02, new int[]{0x00},PTZ_START);

    }

    @Override
    public void ptz_autoHozontal() {
        ptz_command("云台-自动水平", 0x1f);

    }

    @Override
    public void ptz_light_open(int strength) {
        set_commands("云台-灯光开", 0x1b, 0x02, new int[]{strength},PTZ_LIGHT_START);
    }

    @Override
    public void ptz_light_close() {
        set_commands("云台-灯光关",  0x1b, 0x02, new int[]{0x00},PTZ_LIGHT_START);

    }

    @Override
    public void changeCamera_front() {
        set_commands("摄像变换-前摄像头", 0x30, 0x01, new int[]{},CAMERA_START);
    }

    @Override
    public void changeCamera_rear() {
        set_commands("摄像变换-后摄像头", 0x31, 0x01, new int[]{},CAMERA_START);
    }

    @Override
    public void openFowardLight(int strength) {
        set_commands("打开-前灯",  0x32, 0x02, new int[]{strength}, LIGHT_FRONT_START);
    }

    @Override
    public void closeFowardLight() {
        set_commands("关闭-前灯", 0x32, 0x02, new int[]{0x00},LIGHT_FRONT_START);
    }


    @Override
    public void push_up() {
        set_commands("上升",  0x35, 0x02, new int[]{0x01}, TUIGAN_START);

    }

    @Override
    public void push_down() {
        set_commands("下降",  0x35, 0x02, new int[]{0x02},TUIGAN_START);

    }

    @Override
    public void push_stop() {
        set_commands("升降-停止",  0x35, 0x02, new int[]{0x03}, TUIGAN_START);

    }

    @Override
    public void push_getHeight() {
        set_commands("获取-升降高度", 0x36, 0x01, new int[]{}, TUIGAN_GET_START);
    }

    @Override
    public void move_up() {
        set_commands("移动-前进",  0x51, 0x03, new int[]{0x01, mSpeed}, MOVE_CONTROL_START);
    }

    @Override
    public void move_down() {
        set_commands("移动-后退",  0x51, 0x03, new int[]{0x02, mSpeed},  MOVE_CONTROL_START);
    }

    @Override
    public void move_left() {
        set_commands("移动-向左",  0x51, 0x03, new int[]{0x03, mSpeed},  MOVE_CONTROL_START);
    }

    @Override
    public void move_right() {
        set_commands("移动-向右",  0x51, 0x03, new int[]{0x04, mSpeed},  MOVE_CONTROL_START);
    }

    @Override
    public void move_stop() {
        set_commands("移动-停止",  0x51, 0x03, new int[]{0x05, mSpeed},  MOVE_CONTROL_START);
    }

    @Override
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    @Override
    public void shouxian_open() {
        set_commands("收线-开启",  0x75, 0x02, new int[]{0x01}, SHOUXIAN_START);
    }

    @Override
    public void shouxian_close() {
        set_commands("收线-关闭",  0x75, 0x02, new int[]{0x00}, SHOUXIAN_START);
    }

    @Override
    public void shouxian_speed(int speed) {
        set_commands("收线速度",  0x76, 0x02, new int[]{speed}, SHOUXIAN_SET_START);
    }

    @Override
    public void jiMiQi_sign() {
        set_commands("计米器-标记",  0x71, 0x02, new int[]{0x01}, JIMICHI_BIAODING_START);
    }

    @Override
    public void jiMiQi_zero() {
        set_commands("计米器-归零",  0x71, 0x02, new int[]{0x00}, JIMICHI_BIAODING_START);
    }

    private int chooseBatteryIcon(int batteryNum) {
        if (batteryNum >= 86) {
            return R.mipmap.battery6;
        } else if (batteryNum >= 72) {
            return R.mipmap.battery5;
        } else if (batteryNum >= 58) {
            return R.mipmap.battery4;
        } else if (batteryNum >= 44) {
            return R.mipmap.battery3;
        } else if (batteryNum >= 30) {
            return R.mipmap.battery2;
        } else if (batteryNum > 16) {
            return R.mipmap.battery1;
        } else {
            return R.mipmap.battery0;
        }
    }

    @Override
    public void release() {//释放资源
//        if (socketUtil != null)
//            socketUtil.release();
        if(udpSocketUtil != null)
            udpSocketUtil.release();
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown())
            scheduledExecutorService.shutdownNow();

        LogUtil.log("release: ControlPresentImpl");
    }

}
