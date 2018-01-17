package com.bmw.M1S1.model;

import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

/**
 * Created by admin on 2016/9/30.
 */
public class All_id_Info {

    private static  All_id_Info instance = null;

    private int m_iPlayID = -1;
    private int m_iLogID = -1;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30;
    private int m_iStartChan = -1;
    private int m_iChanNum = -1;
    private int m_iPort = -1;
    private int multi_chan_num = -1;

    private All_id_Info(){};

    public void resetData(){
          m_iPlayID = -1;
          m_iLogID = -1;
          m_oNetDvrDeviceInfoV30 = null;
          m_iStartChan = -1;
          m_iChanNum = -1;
          m_iPort = -1;
          multi_chan_num = -1;
    }

    public static All_id_Info getInstance(){
        if(instance == null){
            synchronized (All_id_Info.class){
                if(instance == null)
                    instance = new All_id_Info();
            }
        }
        return instance;
    }


    public int getM_iPort() {
        return m_iPort;
    }

    public void setM_iPort(int m_iPort) {
        this.m_iPort = m_iPort;
    }

    public int getMulti_chan_num() {
        return multi_chan_num;
    }

    public void setMulti_chan_num(int multi_chan_num) {
        this.multi_chan_num = multi_chan_num;
    }

    public int getM_iStartChan() {
        return m_iStartChan;
    }

    public void setM_iStartChan(int m_iStartChan) {
        this.m_iStartChan = m_iStartChan;
    }

    public int getM_iChanNum() {
        return m_iChanNum;
    }

    public void setM_iChanNum(int m_iChanNum) {
        this.m_iChanNum = m_iChanNum;
    }


    public int getM_iPlayID() {
        return m_iPlayID;
    }

    public void setM_iPlayID(int m_iPlayID) {
        this.m_iPlayID = m_iPlayID;
    }

    public int getM_iLogID() {
        return m_iLogID;
    }

    public void setM_iLogID(int m_iLogID) {
        this.m_iLogID = m_iLogID;
    }

    public NET_DVR_DEVICEINFO_V30 getM_oNetDvrDeviceInfoV30() {

        return m_oNetDvrDeviceInfoV30;
    }

    public void setM_oNetDvrDeviceInfoV30(NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30) {
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) { //模拟通道
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) { //数字通道
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        this.m_oNetDvrDeviceInfoV30 = m_oNetDvrDeviceInfoV30;
    }




}
