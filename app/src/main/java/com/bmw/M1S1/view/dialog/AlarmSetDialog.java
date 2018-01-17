package com.bmw.M1S1.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bmw.M1S1.BaseApplication;
import com.bmw.M1S1.R;
import com.bmw.M1S1.view.view.SwitchButton;


public class AlarmSetDialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private Context context;
    private EditText alarmEdt;
    private TextView sureTv;


    public AlarmSetDialog(Context context) {


        this.context = context;
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        dialog.setView(new EditText(context));//实现弹出虚拟键盘
        dialog.show();
        WindowManager manager = (WindowManager) context.
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (dm.heightPixels() * 0.3);   //高度设置为屏幕的0.3
//        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

//        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.dialog_alarm_set);

        alarmEdt = (EditText) window.findViewById(R.id.alarm_setEdt);
        sureTv = (TextView) window.findViewById(R.id.alarm_sureTv);
        SharedPreferences sharedPreferences = BaseApplication.getSharedPreferences();
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        int alarm_angle = sharedPreferences.getInt(BaseApplication.ALARMSET,0);
        alarmEdt.setText(alarm_angle+"");
        sureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String angleStr = alarmEdt.getText().toString();
                if(angleStr!= "") {
                    int angle = Integer.valueOf(angleStr);
                    editor.putInt(BaseApplication.ALARMSET,angle);
                    editor.commit();
                    if(listener!= null)
                        listener.DataChange(angle);
                }
                dismiss();
            }
        });
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }

    private AlarmDataChangeListener listener;

    public interface AlarmDataChangeListener{
        void DataChange(int angle);
    }
    public void setOnAlarmDataChangeListener(AlarmDataChangeListener listener){
        this.listener = listener;
    }

}