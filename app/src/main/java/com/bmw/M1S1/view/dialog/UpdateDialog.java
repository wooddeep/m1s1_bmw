package com.bmw.M1S1.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bmw.M1S1.R;


public class UpdateDialog {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private TextView sure,cancel,title,detail;

    public UpdateDialog(Context context,String version,String describe) {


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
        window.setContentView(R.layout.update_dialog);
        sure = (TextView) window.findViewById(R.id.update_sure);
        cancel = (TextView) window.findViewById(R.id.update_cancel);
        detail = (TextView) window.findViewById(R.id.update_detail);
        detail.setText(describe);
        title = (TextView) window.findViewById(R.id.updatedialog_title);
        title.setText("是否升级至版本 "+version +" ?");

    }


    public void setListening(View.OnClickListener listener){
        sure.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }


}