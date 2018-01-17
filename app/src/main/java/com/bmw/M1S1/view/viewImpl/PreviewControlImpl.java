package com.bmw.M1S1.view.viewImpl;

/**
 * Created by yMuhuo on 2017/2/7.
 */
public interface PreviewControlImpl {
    void setReturnData(int batterImageId,float pitchAngle,
                       float rollAngle, float pushHeight,
                       int ptz_light,int front_light,
                       int move_speed,String contol_ip,
                       float air_pressure,int alarmInfo,float jiMiQi);
}
