package com.bmw.M1S1.presenter;

/**
 * Created by admin on 2016/9/5.
 */
public interface ControlPresenter {


    void setQingfuAlarm(int angle);
    void qxAlarm();

    void ptz_up();
    void ptz_down();
    void ptz_left();
    void ptz_right();
    void ptz_zooom_add();
    void ptz_zoom_sub();
    void ptz_size_add();
    void ptz_size_sub();
    void ptz_stop();
    void ptz_autoHozontal();
    void ptz_light_open(int strength);
    void ptz_light_close();

    void changeCamera_front();
    void changeCamera_rear();


    void openFowardLight(int strength);
    void closeFowardLight();

    void push_up();
    void push_down();
    void push_stop();
    void push_getHeight();


    void move_up();
    void move_down();
    void move_left();
    void move_right();
    void move_stop();


    void setSpeed(int speed);

    void shouxian_open();
    void shouxian_close();
    void shouxian_speed(int speed);

    void jiMiQi_sign();
    void jiMiQi_zero();


    void release();     //释放资源
//    boolean isSocketNull();

}
