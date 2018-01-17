package com.bmw.M1S1.presenter.impl;

import com.bmw.M1S1.jna.HCNetSDKJNAInstance;
import com.bmw.M1S1.model.All_id_Info;
import com.bmw.M1S1.model.Login_info;
import com.bmw.M1S1.presenter.VideoCutPresenter;
import com.bmw.M1S1.utils.LogUtil;
import com.bmw.M1S1.utils.UrlUtil;
import com.bmw.M1S1.view.viewImpl.PreviewImpl;
import com.hikvision.netsdk.HCNetSDK;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by admin on 2016/9/30.
 */
public class VideoCutPresentImpl implements VideoCutPresenter {

    private PreviewImpl preview;
    private boolean isRecord;
    private All_id_Info all_id_info;

    public VideoCutPresentImpl(PreviewImpl preview) {
        this.preview = preview;
        all_id_info = All_id_Info.getInstance();
        pathIsExist();
    }

    @Override
    public void record() {
        int m_iPlayID = all_id_info.getM_iPlayID();
        if (!isRecord) {

            LogUtil.log("自定义接口录像！");
            int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 2, UrlUtil.getSDPath() + Login_info.local_video_path + UrlUtil.getFileName() + ".mp4");
            if (clibrary <= 0) {

                LogUtil.error("海康：抓拍：开始录制失败："+HCNetSDK.getInstance().NET_DVR_GetLastError());
                if (preview != null) {
                    preview.record(0, false);
                }
                return;
            } else {
                LogUtil.error("自定义接口录像！" + clibrary);
                LogUtil.log("海康：抓拍：开始录制成功！");
                if (preview != null) {
                    LogUtil.log("NET_DVR_SaveRealData succ!");
                    preview.record(0, true);
                }
            }
            /*if (!HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID,
                    UrlUtil.getSDPath() + Login_info.local_video_path+UrlUtil.getFileName()+".avi")) {
                LogUtil.error("海康：NET_DVR_SaveRealData failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                preview.record(0,false);
                return;
            } else {
                LogUtil.log("海康：NET_DVR_SaveRealData succ!");
                preview.record(0,true);
            }*/
            isRecord = true;
        } else {
            if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                LogUtil.error("海康：NET_DVR_StopSaveRealData failed! error: "
                        + HCNetSDK.getInstance()
                        .NET_DVR_GetLastError());
                preview.record(1, false);
            } else {
                LogUtil.log("海康：NET_DVR_StopSaveRealData succ!");
                preview.record(1, true);
            }
            isRecord = false;
        }
    }

    @Override
    public void capture() {
        try {
            int m_iPort = all_id_info.getM_iPort();
            if (m_iPort < 0) {
                LogUtil.error("海康：please start preview first");
                preview.capture(false, null);
                return;
            }
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth,
                    stHeight)) {
                LogUtil.error("海康：getPictureSize failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            Player.MPInteger stSize = new Player.MPInteger();
            if (!Player.getInstance()
                    .getJPEG(m_iPort, picBuf, nSize, stSize)) {
                LogUtil.error("海康：getBMP failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }

            String path = UrlUtil.getSDPath() + Login_info.local_picture_path
                    + UrlUtil.getFileName() + ".jpg";
            FileOutputStream file = new FileOutputStream(path);
            file.write(picBuf, 0, stSize.value);
            file.close();
            preview.capture(true, path);
//            preview.iToast("截图已保存");
        } catch (Exception err) {
            LogUtil.error("海康：error: " + err.toString());
        }
    }

    /**
     * 路径是否存在，不能存在则创建
     */
    private void pathIsExist() {
        File file = new File(UrlUtil.getSDPath() + Login_info.local_video_path);
        if (!file.exists())
            file.mkdirs();

        File file1 = new File(UrlUtil.getSDPath() + Login_info.local_picture_path);
        if (!file1.exists())
            file1.mkdirs();
    }

}
