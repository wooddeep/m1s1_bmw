package com.bmw.M1S1.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bmw.M1S1.R;


public class UpdateProgressDialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private ProgressBar pBar;
    private TextView pro_num1,pro_num2;

    public UpdateProgressDialog(Context context) {


        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
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
        window.setContentView(R.layout.update_progress_dialog);
        pBar = (ProgressBar) window.findViewById(R.id.update_progressBar);
        pBar.setProgress(0);
        pro_num1 = (TextView) window.findViewById(R.id.pro_num1);
        pro_num2 = (TextView) window.findViewById(R.id.pro_num2);
    }

    public void setProgress(int num){
        pBar.setProgress(num);
        pro_num1.setText(num+"%");
        pro_num2.setText(num+"/100");
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }



}