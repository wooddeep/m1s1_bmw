package com.bmw.M1S1.view.dialog;

/**
 * Created by admin on 2016/9/19.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.M1S1.R;
import com.bmw.M1S1.model.Login_info;
import com.bmw.M1S1.view.view.SwitchButton;


public class SettingDialog implements View.OnClickListener {

    private static final String TAG = "YMH";
    private AlertDialog dialog;
    private Login_info login_info;
    private EditText eIp, ePort, eAccount, ePassword, eSocket_ip, eSocket_port,wSsid,wPassword;
    private TextView sure, cancel,reset;
    private SwitchButton switchButton;
    private boolean isWifiConnectAuto;
    private SwitchButton zimaliu_sbtn;
    private boolean isZimaliu;
    private Context context;


    public SettingDialog(Context context) {


        this.context = context;
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
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
        window.setContentView(R.layout.dialog_setting);
        eIp = (EditText) window.findViewById(R.id.haikang_ip);
        ePort = (EditText) window.findViewById(R.id.haikang_port);
        ePassword = (EditText) window.findViewById(R.id.haikang_password);
        eAccount = (EditText) window.findViewById(R.id.haikang_account);
        eSocket_ip = (EditText) window.findViewById(R.id.socket_ip);
        eSocket_port = (EditText) window.findViewById(R.id.socket_port);
        wSsid = (EditText) window.findViewById(R.id.wifi_ssid);
        wPassword = (EditText) window.findViewById(R.id.wifi_password);
        sure = (TextView) window.findViewById(R.id.set_sure);
        cancel = (TextView) window.findViewById(R.id.set_cancel);
        reset = (TextView) window.findViewById(R.id.set_reset);
        switchButton = (SwitchButton) window.findViewById(R.id.wifi_isConnect_auto);
        zimaliu_sbtn = (SwitchButton) window.findViewById(R.id.haikang_zimaliu);

        login_info = Login_info.getInstance();
        eIp.setText(login_info.getVideo_ip());
        ePort.setText(login_info.getVideo_port()+"");
        eAccount.setText(login_info.getVideo_account());
        ePassword.setText(login_info.getVideo_password());
        eSocket_ip.setText(login_info.getSocket_ip());
        eSocket_port.setText(login_info.getSocket_port() + "");
        wSsid.setText(login_info.getWifi_SSID());
        wPassword.setText(login_info.getWifi_Password());
        isWifiConnectAuto = login_info.isWifi_auto();
        isZimaliu = login_info.isVideo_zimaliu();
        switchButton.setChecked(isWifiConnectAuto);
        zimaliu_sbtn.setChecked(isZimaliu);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isWifiConnectAuto = b;
            }
        });
        zimaliu_sbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isZimaliu = b;
            }
        });
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
        reset.setOnClickListener(this);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0){
                    listener.changeReporter(false);
                }
                return false;
            }
        });

    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        listener.changeReporter(false);
        dialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_sure:
                if (isInputRight()) {
                    login_info.setData(eIp.getText().toString(),
                         ePort.getText().toString(),
                            eAccount.getText().toString(),
                            ePassword.getText().toString(),
                            eSocket_ip.getText().toString(),
                            Integer.valueOf(eSocket_port.getText().toString()),
                            wSsid.getText().toString(),wPassword.getText().toString(),isWifiConnectAuto,isZimaliu);
                    if (listener != null)
                        listener.changeReporter(true);
                    dismiss();
                }else{
                    Toast.makeText(context,"输入格式有误，请重新输入！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.set_cancel:
                dismiss();
                break;
            case R.id.set_reset:
                eIp.setText(Login_info.base_video_ip);
                ePort.setText(Login_info.base_video_port+"");
                eAccount.setText(Login_info.base_video_account);
                ePassword.setText(Login_info.base_video_password);
                eSocket_ip.setText(Login_info.base_socket_ip);
                eSocket_port.setText(Login_info.base_socket_port+ "");
                wSsid.setText(Login_info.base_wifi_SSID);
                wPassword.setText(Login_info.base_wifi_Password);
                switchButton.setChecked(Login_info.base_wifi_auto);
                zimaliu_sbtn.setChecked(Login_info.base_video_zimaliu);
                break;
        }
    }

    private boolean isInputRight() {
        if (!eIp.getText().toString().equals("") &&
                !ePort.getText().toString().equals("") &&
                !eAccount.getText().toString().equals("") &&
                !ePassword.getText().toString().equals("") &&
                !eSocket_ip.getText().toString().equals("") &&
                !eSocket_port.getText().toString().equals("") &&
                !wSsid.getText().toString().equals("") &&
                !wPassword.getText().toString().equals("") &&
                eSocket_ip.getText().toString().split("\\.").length == 4 &&
                eIp.getText().toString().split("\\.").length == 4) {
            return true;
        }
        return false;
    }

    private OnSettingChangeListener listener;

    public void setOnSettingChangeListener(OnSettingChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSettingChangeListener {
        void changeReporter(boolean isChange);
    }


}