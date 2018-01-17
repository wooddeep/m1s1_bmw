package com.bmw.M1S1;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by yMuhuo on 2016/12/12.
 */
public class BaseApplication extends Application{

    static Context mContext;
    static Resources mResources;
    private static final String PREF_M1S1 = "M1S1_PREF";
    private static long last_duration_time;
    private static String last_toast_msg;
    public static final String ALARMSET = "ALARMSET";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mResources = mContext.getResources();
    }

    public synchronized static BaseApplication context(){
        return (BaseApplication) mContext;
    }

    public static Resources resources(){
        return mResources;
    }

    public static SharedPreferences getSharedPreferences(){
        return context().getSharedPreferences(PREF_M1S1,Context.MODE_PRIVATE);
    }

    public static void toast(String msg){
        toast(msg, Toast.LENGTH_SHORT);
    }

    public static void toast(String msg,int duration){
        if(msg != null && !msg.equalsIgnoreCase("")){
            long current_time = System.currentTimeMillis();
            if( !msg.equalsIgnoreCase(last_toast_msg) || current_time - last_duration_time>2000){
                View view = LayoutInflater.from(context()).inflate(R.layout.toast_view,null);
                TextView textView = (TextView) view.findViewById(R.id.toast_tv);
                textView.setText(msg);
                Toast toast = new Toast(context());
                toast.setView(view);
                toast.setDuration(duration);
                toast.show();

                last_duration_time = System.currentTimeMillis();
                last_toast_msg = msg;
            }
        }
    }

}
