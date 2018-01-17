package com.bmw.M1S1.view.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;


import com.bmw.M1S1.R;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerActivity extends BaseActivity {

    private final String TAG = "PlayerActivity";
    @Bind(R.id.player_play)
    ImageView btn_play;
    @Bind(R.id.player_seekbar)
    SeekBar seekBar;
    @Bind(R.id.player_surface)
    SurfaceView sv;
    private String path;
    private int iPort;
    private boolean isPause;
    private boolean isStop;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        path = getIntent().getStringExtra("path");
        iPort = Player.getInstance().getPort();
        Player.getInstance().setFileEndCB(iPort, new PlayerCallBack.PlayerPlayEndCB() {
            @Override
            public void onPlayEnd(int i) {
                PlayerActivity.this.finish();
            }
        });

        Player.getInstance().setFileRefCB(iPort, new PlayerCallBack.PlayerFileRefCB() {
            @Override
            public void onFileRefDone(int i) {

            }
        });
        open();


    }

    public void open(){
        Player.getInstance().openFile(iPort,path);
        sv.getHolder().addCallback(callback);
        seekBar.setOnSeekBarChangeListener(change);
        seekBar.setMax( Player.getInstance().getFileTotalFrames(iPort));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop){
                    seekBar.setProgress(Player.getInstance().getCurrentFrameNum(iPort));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @OnClick({R.id.player_play})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_play:

                if(isPause){
                    Player.getInstance().pause(iPort,0);
                    isPause = false;
                    btn_play.setImageResource(android.R.drawable.ic_media_pause);
                }else {
                    Player.getInstance().pause(iPort,1);
                    isPause = true;
                    btn_play.setImageResource(android.R.drawable.ic_media_play);
                }

                break;
        }
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        // SurfaceHolder被修改的时候回调
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被销毁");

            Player.getInstance().stop(iPort);
            Player.getInstance().closeFile(iPort);
            Player.getInstance().freePort(iPort);

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被创建");
//
            Player.getInstance().play(iPort,holder);
            btn_play.setImageResource(android.R.drawable.ic_media_pause);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.i(TAG, "SurfaceHolder 大小被改变");
        }

    };

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
//
            Player.getInstance().setCurrentFrameNum(iPort,progress);
//            Player.getInstance().pause(iPort,0);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            Player.getInstance().pause(iPort,1);

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };


    @Override
    protected void onDestroy() {
        isStop = true;
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
