package com.bmw.M1S1.view.ui;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bmw.M1S1.BaseApplication;
import com.bmw.M1S1.R;
import com.bmw.M1S1.model.All_id_Info;
import com.bmw.M1S1.model.Login_info;
import com.bmw.M1S1.presenter.ControlPresenter;
import com.bmw.M1S1.presenter.HCNetSdkLogin;
import com.bmw.M1S1.presenter.VideoCutPresenter;
import com.bmw.M1S1.presenter.impl.ControlPresentImpl;
import com.bmw.M1S1.presenter.impl.HCNetSdkLoginImpl;
import com.bmw.M1S1.presenter.impl.VideoCutPresentImpl;
import com.bmw.M1S1.utils.NetWorkUtil;
import com.bmw.M1S1.view.dialog.AlarmSetDialog;
import com.bmw.M1S1.view.view.SwitchButton;
import com.bmw.M1S1.view.viewImpl.PreviewControlImpl;
import com.bmw.M1S1.view.viewImpl.PreviewImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PreviewActivity extends BaseActivity implements PreviewImpl,
        SeekBar.OnSeekBarChangeListener, PreviewControlImpl {

    private static final String TAG = "main/PreviewActivity";

    @Bind(R.id.main_surface)
    SurfaceView surfaceView;
    @Bind(R.id.battery)
    ImageView batteryImg;
    @Bind(R.id.recordImg)
    ImageView recordImg;

    @Bind(R.id.preview_move)
    ImageView modelMoveAndYuntaiImg;
    @Bind(R.id.preview_light)
    LinearLayout light_ptz_container;
    @Bind(R.id.preview_light_far)
    LinearLayout light_front_container;
    @Bind(R.id.preview_speed)
    LinearLayout speed_container;
    @Bind(R.id.light_sb)
    SeekBar light_ptz_sb;
    @Bind(R.id.far_light_sb)
    SeekBar light_front_sb;
    @Bind(R.id.speed_sb)
    SeekBar speed_sb;
    @Bind(R.id.capture_container_flayout)
    FrameLayout main_container;


    @Bind(R.id.pitch_angle)
    TextView pitchTv;
    @Bind(R.id.roll_angle)
    TextView rollTv;
    @Bind(R.id.jimi_length)
    TextView jimiTv;
    @Bind(R.id.ip_adress)
    TextView ip_adress;
    @Bind(R.id.ip_adress2)
    TextView ip_adress2;
    @Bind(R.id.ip_adress_container)
    LinearLayout ip_adress_container;


    @Bind(R.id.push_high)
    TextView pushHeightTview;


    @Bind(R.id.shouxian_auto)
    SwitchButton shouxianAuto;
    @Bind(R.id.shouxian_preview_speed)
    SeekBar shouxianSpeed_seekBar;
    @Bind(R.id.shouxian_container)
    LinearLayout shouxianContainer;
    @Bind(R.id.shouxian_unAuto)
    LinearLayout shouxianUnAuto;
    @Bind(R.id.line_back)
    FrameLayout shouxian_btn;

    @Bind(R.id.alarm_container)
    LinearLayout alarm_container;
    @Bind(R.id.alarm_cancle)
    ImageView alarm_cancel;

    @Bind(R.id.preview_top_connect_img)
    ImageView connect_img;
    @Bind(R.id.preview_top_connect_tv)
    TextView connect_tv;


    private HCNetSdkLogin hcNetSdkLogin;
    private VideoCutPresenter videoCutPresenter;
    private ControlPresenter controlPresenter;
    private boolean isCloudModel;
    private boolean isLight_open;
    private boolean isLight_far_open;
    private boolean isSpeed_open;
    private int speed = 3;
    private boolean isCameraRear;
    private boolean isShouxianMode;
    private int shouxianSpeed = 5;
    private int ptz_light_strength;
    private int front_light_strength;
    private boolean isFirst = true;
    private int key_back;
    private long key_back_time;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean isConnect, isGetControl;
    private Handler handler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview;
    }


    @Override
    protected void afterSetContentLayout() {
        super.afterSetContentLayout();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);

        initPresenter();
        initHandler();
        initSeekBar();
        initConnect();


    }

    //初始化presenter
    private void initPresenter() {
        hcNetSdkLogin = new HCNetSdkLoginImpl(this, this, surfaceView);
        videoCutPresenter = new VideoCutPresentImpl(this);
        controlPresenter = new ControlPresentImpl(this, this);

    }

    //初始化连接状态
    private void initConnect() {

        scheduledExecutorService = Executors.newScheduledThreadPool(3);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {


                if (NetWorkUtil.isNetworkAvailable(BaseApplication.context()) != 2) {
                    handler.sendEmptyMessage(1);
                } else {

                    int videoPing = NetWorkUtil.pingHost(Login_info.getInstance().getVideo_ip());
                    int controlPing = NetWorkUtil.pingHost(Login_info.getInstance().getSocket_ip());

                    boolean isVideoIpConnect = videoPing == 0 ? true : false;
                    boolean isControlIpConnect = controlPing == 0 ? true : false;


                    if (isVideoIpConnect && isControlIpConnect) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }

                }

            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    //初始化handler
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (!isConnect && connect_img != null && connect_tv != null) {
                            connect_img.setImageResource(R.mipmap.connect);
                            connect_tv.setTextColor(getResources().getColor(R.color.colorText));
                            isConnect = true;
                        }

                        break;
                    case 1:
                        if (isConnect && connect_img != null && connect_tv != null) {
                            connect_img.setImageResource(R.mipmap.disconnect);
                            connect_tv.setTextColor(getResources().getColor(R.color.alarm_color));
                            handler.sendEmptyMessage(3);
                            isConnect = false;
                        }
                        break;
                    case 2:
                        if (!isGetControl && ip_adress_container != null && ip_adress != null && ip_adress2 != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ip_adress_container.setBackground(getResources().getDrawable(R.drawable.bg_circle));
                            }
                            ip_adress.setTextColor(getResources().getColor(R.color.colorText));
                            ip_adress2.setTextColor(getResources().getColor(R.color.colorText));
                            ip_adress.setText("有");
                            isGetControl = true;
                        }
                        break;
                    case 3:
                        if (isGetControl && ip_adress_container != null && ip_adress != null && ip_adress2 != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ip_adress_container.setBackground(getResources().getDrawable(R.drawable.bg_circle_red));
                            }
                            ip_adress.setTextColor(getResources().getColor(R.color.alarm_color));
                            ip_adress2.setTextColor(getResources().getColor(R.color.alarm_color));
                            ip_adress.setText("无");
                            isGetControl = false;
                        }
                        break;
                }
            }
        };
    }

    //初始化seekbar
    private void initSeekBar() {
        light_ptz_sb.setMax(255);
        light_front_sb.setMax(255);
        speed_sb.setMax(10);
        speed_sb.setProgress(speed);
        controlPresenter.setSpeed(speed);

        speed_sb.setOnSeekBarChangeListener(this);
        light_ptz_sb.setOnSeekBarChangeListener(this);
        light_front_sb.setOnSeekBarChangeListener(this);


        shouxianSpeed_seekBar.setMax(9);
        shouxianSpeed_seekBar.setProgress(shouxianSpeed);
        shouxianSpeed_seekBar.setOnSeekBarChangeListener(this);
        shouxianAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    controlPresenter.shouxian_open();
                else
                    controlPresenter.shouxian_close();
            }
        });
    }

    @Override
    public void setReturnData(final int batterImageId, final float pitchAngle, final float rollAngle,
                              final float pushHeight, final int ptz_light,
                              final int front_light, final int move_speed,
                              final String control_ip, float air_pressure,
                              final int alarmInfo, final float jiMiQi) {
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                if (light_front_sb != null && light_ptz_sb != null && speed_sb != null && pitchTv != null && rollTv != null && jimiTv != null) {

                    if (NetWorkUtil.getWifiIp(PreviewActivity.this) != null && NetWorkUtil.getWifiIp(PreviewActivity.this).equals(control_ip)) {
                        handler.sendEmptyMessage(2);
                    } else {
                        handler.sendEmptyMessage(3);
                    }
/*

                    ptz_light_strength = ptz_light;
                    front_light_strength = front_light;
                    light_front_sb.setProgress(front_light_strength);
                    light_ptz_sb.setProgress(ptz_light_strength);

                    if (move_speed != 0) {
                        speed = move_speed;
                        speed_sb.setProgress(speed);
                    }
*/

                    pitchTv.setText(pitchAngle + "°");
                    rollTv.setText(rollAngle + "°");
                    if (jiMiQi < 10) {
                        jimiTv.setTextSize(8);
                    } else if (jiMiQi < 100) {
                        jimiTv.setTextSize(7);
                    } else {
                        jimiTv.setTextSize(6);
                    }
                    jimiTv.setText(jiMiQi + "m");
                    pushHeightTview.setText(pushHeight + "cm");
                    if (batteryImg != null)
                        batteryImg.setImageResource(batterImageId);

                    if (alarmInfo == 0) {
                        isFirst = true;
                        alarm_container.setVisibility(View.GONE);
                    }

                    if (alarmInfo > 0 && isFirst) {
                        log("alarm: " + alarmInfo);
                        alarm_container.setVisibility(View.VISIBLE);
                        isFirst = false;
                    }
                }

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (All_id_Info.getInstance().getM_iLogID() >= 0) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                hcNetSdkLogin.connectDevice();
            }
        }).start();
    }

    //释放资源
    @Override
    protected void onDestroy() {
        scheduledExecutorService.shutdownNow();
        controlPresenter.release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                hcNetSdkLogin.release();
            }
        }).start();
        controlPresenter = null;
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    //设置返回控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if ((System.currentTimeMillis() - key_back_time) >= 5000) {
                key_back = 0;
            }
            if (key_back != 1) {
                toast("再按一次返回键退出预览！");

                key_back_time = System.currentTimeMillis();
            }
            if (key_back == 1) {
                mToast.cancel();
                finish();
            }
            key_back++;

            return true;
        }
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //录像回调处理
    @Override
    public void record(int i, boolean isRecord) {
        switch (i) {
            case 0:
                if (isRecord) {
//                    toast("开始录像");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recordImg.setImageResource(R.mipmap.recordpress);
                        }
                    });
                } else {
                    toast("开始录像失败");
                }
                break;
            case 1:
                if (isRecord) {
//                    toast("停止录像");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recordImg.setImageResource(R.mipmap.record);
                        }
                    });
                } else {
                    toast("停止录像失败");
                }
                break;
        }
    }

    //截图回调处理
    @Override
    public void capture(boolean isCapture, String path) {
        if (isCapture) {

            final Bitmap bitmap = BitmapFactory.decodeFile(path);
            final ImageView imageView = new ImageView(this);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(200, 200);
            layoutParams.setMargins(100, 100, 0, 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(bitmap);
            main_container.addView(imageView);


//        Animation animation = AnimationUtils.loadAnimation(this,R.anim.capture);
//        animation.setFillAfter(true);
//        imageView.startAnimation(animation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                        imageView.setVisibility(View.GONE);
                            main_container.removeView(imageView);
                            bitmap.recycle();
                        }
                    });
                }
            }).start();
        } else {
            toast("截图失败！");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.preview_move, R.id.record, R.id.capture,
            R.id.preview_light, R.id.preview_light_far,
            R.id.preview_speed, R.id.cameraChange,
            R.id.line_back, R.id.alarm, R.id.alarm_cancle, R.id.autoHozontal
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_move:
                if (isCloudModel) {
                    modelMoveAndYuntaiImg.setImageResource(R.mipmap.move);
                    isCloudModel = false;
                } else {
                    modelMoveAndYuntaiImg.setImageResource(R.mipmap.camera);
                    isCloudModel = true;
                }
                break;
            case R.id.record:
                videoCutPresenter.record();
                break;
            case R.id.capture:
                videoCutPresenter.capture();
                break;
            case R.id.preview_light:
                isLight_open = setSeekbar(isLight_open, light_ptz_container, light_ptz_sb);
                break;
            case R.id.preview_light_far:
                isLight_far_open = setSeekbar(isLight_far_open, light_front_container, light_front_sb);
                break;
            case R.id.preview_speed:
                isSpeed_open = setSeekbar(isSpeed_open, speed_container, speed_sb);
                break;

            case R.id.cameraChange:
                if (isCameraRear) {
                    controlPresenter.changeCamera_front();
                    isCameraRear = false;
                } else {
                    controlPresenter.changeCamera_rear();
                    isCameraRear = true;
                }
                break;
            case R.id.line_back:

                if (isShouxianMode) {
                    isShouxianMode = false;
                    controlPresenter.shouxian_close();
                    shouxianAuto.setChecked(false);
                    shouxian_btn.setBackgroundColor(BaseApplication.resources().getColor(R.color.nothing));
                    shouxianContainer.setVisibility(View.GONE);
                } else {
                    isShouxianMode = true;
                    shouxian_btn.setBackground(BaseApplication.resources().getDrawable(R.drawable.bg_container));
                    shouxianContainer.setVisibility(View.VISIBLE);
                }
                /*
                shouxianDialog = new ShouxianDialog(this, isShouxianMode, shouxianSpeed);
                shouxianDialog.setOnDataChangelistener(new ShouxianDialog.OnDataChangeListener() {
                    @Override
                    public void speedChange(int speed) {
                        shouxianSpeed = speed;
                        controlPresenter.shouxian_speed(shouxianSpeed);
                    }

                    @Override
                    public void modelChange(boolean isOpen) {
                        isShouxianMode = isOpen;
                        if(isOpen)
                            controlPresenter.shouxian_open();
                        else
                            controlPresenter.shouxian_close();
                    }
                });*/
                break;
            case R.id.alarm:
                AlarmSetDialog alarmDialog = new AlarmSetDialog(PreviewActivity.this);
                alarmDialog.setOnAlarmDataChangeListener(new AlarmSetDialog.AlarmDataChangeListener() {
                    @Override
                    public void DataChange(int angle) {
                        controlPresenter.setQingfuAlarm(angle);
                    }
                });
                break;
            case R.id.alarm_cancle:
                alarm_container.setVisibility(View.GONE);
                controlPresenter.qxAlarm();
                break;
            case R.id.autoHozontal:
                controlPresenter.ptz_autoHozontal();

                break;

        }
    }

    private boolean setSeekbar(boolean isOpen, LinearLayout container, SeekBar seekBar) {
        if (isOpen) {
            container.setBackgroundColor(BaseApplication.resources().getColor(R.color.nothing));
            seekBar.setVisibility(View.GONE);
            isOpen = false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                container.setBackground(BaseApplication.resources().getDrawable(R.drawable.bg_container));
            }
            seekBar.setVisibility(View.VISIBLE);
            isOpen = true;
        }
        return isOpen;
    }

    @OnTouch({R.id.preview_move_up, R.id.preview_move_down, R.id.preview_move_left, R.id.preview_move_right,
            R.id.preview_up, R.id.preview_down,
            R.id.zoom_add, R.id.zoom_sub,
            R.id.size_add, R.id.size_sub,
            R.id.turnZero,
            R.id.shouxian_unAuto
    })
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP)
            log("动作-按下");
        switch (view.getId()) {
            case R.id.preview_move_up:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isCloudModel) {
                        controlPresenter.ptz_up();
                    } else {
                        controlPresenter.move_up();
                    }
                }
                control_stop(event, 0);
                break;
            case R.id.preview_move_down:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isCloudModel) {
                        controlPresenter.ptz_down();
                    } else {
                        controlPresenter.move_down();
                    }
                }
                control_stop(event, 0);
                break;
            case R.id.preview_move_left:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isCloudModel) {
                        controlPresenter.ptz_left();
                    } else {
                        controlPresenter.move_left();
                    }
                }
                control_stop(event, 0);
                break;
            case R.id.preview_move_right:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isCloudModel) {
                        controlPresenter.ptz_right();
                    } else {
                        controlPresenter.move_right();
                    }
                }
                control_stop(event, 0);
                break;
            case R.id.preview_up:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.push_up();
                }
                control_stop(event, 2);
                break;
            case R.id.preview_down:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.push_down();
                }
                control_stop(event, 2);
                break;
            case R.id.zoom_add:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.ptz_zooom_add();
                }
                control_stop(event, 1);
                break;
            case R.id.zoom_sub:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.ptz_zoom_sub();
                }
                control_stop(event, 1);
                break;
            case R.id.size_add:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.ptz_size_add();
                }
                control_stop(event, 1);
                break;
            case R.id.size_sub:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.ptz_size_sub();
                }
                control_stop(event, 1);
                break;

            case R.id.turnZero:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controlPresenter.jiMiQi_sign();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    controlPresenter.jiMiQi_zero();
                }
                break;
            case R.id.shouxian_unAuto:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    shouxianAuto.setChecked(false);
                    controlPresenter.shouxian_open();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    controlPresenter.shouxian_close();
                }
                break;
        }
        return false;
    }

    private void control_stop(MotionEvent event, int which) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (which) {
                case 0:
                    if (isCloudModel) {
                        controlPresenter.ptz_stop();
                    } else {
                        controlPresenter.move_stop();
                    }
                    break;
                case 1:
                    controlPresenter.ptz_stop();
                    break;
                case 2:
                    controlPresenter.push_stop();
                    controlPresenter.push_getHeight();
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.light_sb:
                ptz_light_strength = seekBar.getProgress();
                if (ptz_light_strength == 0) {
                    controlPresenter.ptz_light_close();
                    break;
                }
                controlPresenter.ptz_light_open(ptz_light_strength);
                break;
            case R.id.far_light_sb:
                front_light_strength = seekBar.getProgress();
                if (front_light_strength == 0) {
                    controlPresenter.closeFowardLight();
                    break;
                }
                controlPresenter.openFowardLight(front_light_strength);
                break;
            case R.id.speed_sb:
                speed = seekBar.getProgress();
                controlPresenter.setSpeed(speed);
                break;
            case R.id.shouxian_preview_speed:
                controlPresenter.shouxian_speed(seekBar.getProgress());
                break;
        }
    }
}
