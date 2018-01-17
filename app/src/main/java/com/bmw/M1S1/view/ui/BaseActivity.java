package com.bmw.M1S1.view.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bmw.M1S1.BaseApplication;

public class BaseActivity extends AppCompatActivity {

    public static String TAG = "debug_M1S1";
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBeforeSetContentLayout();
        if(getLayoutId() != 0){
            setContentView(getLayoutId());
        }
        afterSetContentLayout();
    }


    protected void onBeforeSetContentLayout() {

    }

    protected void afterSetContentLayout() {

    }


    protected int getLayoutId() {
        return 0;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Toast mToast;

    public void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            showToast();
        }
    }

    public void toast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
       showToast();
    }

    public void showToast(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                mToast.show();
            }
        });
    }

    public static void log(String msg) {
        Log.i(TAG,"===============================================================================");
        Log.i(TAG, msg);
    }
    public static void error(String msg) {
        Log.e(TAG,"===============================================================================");
        Log.e(TAG, msg);
    }

}
