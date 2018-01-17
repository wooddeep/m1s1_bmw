package com.bmw.M1S1.presenter.impl;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceView;
import android.widget.Toast;

import com.bmw.M1S1.BaseApplication;
import com.bmw.M1S1.model.All_id_Info;
import com.bmw.M1S1.model.Login_info;
import com.bmw.M1S1.presenter.HCNetSdkLogin;
import com.bmw.M1S1.presenter.PreviewPresenter;
import com.bmw.M1S1.utils.LogUtil;
import com.bmw.M1S1.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2016/9/28.
 */
public class HCNetSdkLoginImpl implements HCNetSdkLogin {

    private PreviewImpl viewImpl;
    private All_id_Info all_id_info;
    private PreviewPresenter previewPresenter;
    private boolean isGoLogin,isfirst,isStopLogin;
    private Login_info loginInfo;
    private ExecutorService cachedThreadPool;
    private Handler handler = new Handler();
    private Toast mToast;

    public HCNetSdkLoginImpl(Context context, PreviewImpl viewImpl, SurfaceView surfaceView) {
        this.viewImpl = viewImpl;
        loginInfo = Login_info.getInstance();
        all_id_info = All_id_Info.getInstance();
        cachedThreadPool = Executors.newCachedThreadPool();

        previewPresenter = new PreviewPresentImpl(context, viewImpl, surfaceView, new PreviewPresentImpl.OnPlayFailedListener() {
            @Override
            public void playFailed() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logout();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        connectDevice();
                    }
                }).start();
            }
        });
        previewPresenter.surfaceAddCallback();
        mToast = Toast.makeText(context,"连接失败！请检查wifi是否连接！",Toast.LENGTH_SHORT);
//        initSDK();
//        connectDevice();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                initSDK();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        connectDevice();
//                    }
//                });
//            }
//        }).start();

    }


    public void login() {
        int m_iLogID = loginNormalDevice();
        if (m_iLogID < 0) {
//            LogUtil.log("连接失败！请检查wifi是否连接！");
            mToast.show();
            return;
        }
        mToast.cancel();
        LogUtil.log("海康：登录成功！");
        all_id_info.setM_iLogID(m_iLogID);
    }

    @Override
    public void logout() {
        previewPresenter.stopSingle();

        LogUtil.log("海康：线程：loginout："+Thread.currentThread().getId());
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(all_id_info.getM_iLogID())) {
            LogUtil.error("海康： 退出登录失败!");
            return;
        }
        LogUtil.log("海康：退出登录成功!");
        all_id_info.setM_iLogID(-1);
        all_id_info.resetData();
    }

    @Override
    public void connectDevice() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (All_id_Info.getInstance().getM_iLogID() < 0 && !isStopLogin) {
                    login();
                    previewPresenter.startSingle();
                    try {
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LogUtil.log("海康：已经登录过，不需重复登录！");


            }
        });
    }

    @Override
    public void release() {
        // release net SDK resource
        mToast.cancel();
        isStopLogin = true;
        isGoLogin = false;
        logout();
//        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }

    private int loginNormalDevice() {

        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            LogUtil.error("海康：HKNetDvrDeviceInfoV30对象创建失败!");
            return -1;
        }

        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(loginInfo.getVideo_ip(), loginInfo.getVideo_port(),
                loginInfo.getVideo_account(), loginInfo.getVideo_password(), m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            LogUtil.error("海康：登录失败!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError() + " " + iLogID);
            return -1;
        }

        all_id_info.setM_oNetDvrDeviceInfoV30(m_oNetDvrDeviceInfoV30);
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            all_id_info.setM_iStartChan(m_oNetDvrDeviceInfoV30.byStartChan);
            all_id_info.setM_iChanNum(m_oNetDvrDeviceInfoV30.byChanNum);
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            all_id_info.setM_iStartChan(m_oNetDvrDeviceInfoV30.byStartDChan);
            all_id_info.setM_iChanNum(m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256);
        }

//        if(HCNetSDK.getInstance().NET_DVR_MakeKeyFrameSub(iLogID,all_id_info.getM_iChanNum())){
//            viewImpl.ilog("子码流动态产生一个关键帧!");
//        }

        return iLogID;
    }
}
