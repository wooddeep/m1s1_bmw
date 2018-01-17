package com.bmw.M1S1.utils;

import android.util.Log;
import android.widget.Toast;

import com.bmw.M1S1.BaseApplication;
import com.bmw.M1S1.view.ui.BaseActivity;

/**
 * Created by yMuhuo on 2017/2/7.
 */
public class LogUtil {

    private static final String TAG = BaseActivity.TAG;

    public static void log(String msg) {
            Log.i(TAG, msg);
    }

    public static void error(String msg) {
            Log.e(TAG, msg );
    }
}
