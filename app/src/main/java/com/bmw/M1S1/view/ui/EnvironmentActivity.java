package com.bmw.M1S1.view.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;

import com.bmw.M1S1.R;
import com.bmw.M1S1.model.Environment;
import com.bmw.M1S1.presenter.ControlPresenter;
import com.bmw.M1S1.presenter.impl.ControlPresentImpl;
import com.bmw.M1S1.view.adapter.EnvironmentAdapter;
import com.bmw.M1S1.view.viewImpl.PreviewControlImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EnvironmentActivity extends BaseActivity implements PreviewControlImpl{

    @Bind(R.id.environment_recyclerview)
    RecyclerView eRecyclerview;
    private EnvironmentAdapter adapter;
    private boolean isStop;
    private ControlPresenter controlPresenter;
    private float lastAir_pressure;
    private boolean isChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        ButterKnife.bind(this);
        controlPresenter = new ControlPresentImpl(this,this);
        init();
        initdata();

    }

    public void init(){
        Log.d(TAG, "init: getdatainit");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eRecyclerview.setLayoutManager(layoutManager);
        adapter = new EnvironmentAdapter(this);
        eRecyclerview.setAdapter(adapter);

    }

    public void initdata(){


        adapter.setAdapterDateChangeListener(new EnvironmentAdapter.AdapterDateChangeListener() {
            @Override
            public void resetDate() {
                isChange = true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "environment onDestroy: ");
        isStop=true;
        controlPresenter.release();
        ButterKnife.unbind(this);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            Log.d("wait", "onKeyDown: ");
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setReturnData(int batterImageId, float pitchAngle,
                              float rollAngle, float pushHeight,
                              int ptz_light, int front_light,
                              int move_speed, String contol_ip,
                              float air_pressure,int alarmInfo,
                              float jiMiQi
    ){
        if(lastAir_pressure != air_pressure || isChange) {
            List<Environment> list = new ArrayList<>();
            Environment environment = new Environment();
            environment.setName("气压");
            environment.setCurrent_num(air_pressure);
            SharedPreferences sharedPreferences = getSharedPreferences(Environment.SHAREPREFERENCES, Context.MODE_PRIVATE);
            environment.setMin_num(sharedPreferences.getFloat(Environment.QIYA_MIN, (float) -1.0));
            environment.setMax_num(sharedPreferences.getFloat(Environment.QIYA_MAX, (float) -1.0));
            list.add(environment);
            adapter.setList(list);
            lastAir_pressure = air_pressure;
            isChange = false;
        }
    }
}
