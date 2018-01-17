package com.bmw.M1S1.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bmw.M1S1.R;


public class EnvironmentDialog{

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private DateChangeListener listener;
    private TextView set;
    private  EditText min;
    private  EditText max;

    public EnvironmentDialog(Context context,String name,String unit,float min_num,float max_num) {


        dialog = new AlertDialog.Builder(context).create();
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
//p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);

        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(R.layout.environment_set_item);
        set = (TextView) window.findViewById(R.id.dialog_set);
        min = (EditText) window.findViewById(R.id.dialog_min);
        max = (EditText) window.findViewById(R.id.dialog_max);
        min.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        max.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        min.setText(String.valueOf(min_num));
        min.setSelection(String.valueOf(min_num).length());
        max.setText(String.valueOf(max_num));
        max.setSelection(String.valueOf(max_num).length());
        TextView name_text = (TextView) window.findViewById(R.id.dialog_name);
        TextView unit_text = (TextView) window.findViewById(R.id.dialog_unit);
        name_text.setText(name);
        unit_text.setText(unit);
        set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null)
                    listener.setDate(min.getText().toString(),max.getText().toString());
            }
        });
    }

    public void setDateChangeListener(DateChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }


    public interface DateChangeListener{
        void setDate(String min,String max);
    }
}