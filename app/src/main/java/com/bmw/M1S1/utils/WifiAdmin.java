package com.bmw.M1S1.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet4Address;
import java.util.List;

public class WifiAdmin {
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;
    private final String TAG = "WifiPresent/WifiAdmin";


    //构造函数
    public WifiAdmin(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        resetWifiInfo();
    }

    public void resetWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {

            Log.i(TAG, "wifi已经开启");
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        resetWifiInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        resetWifiInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        resetWifiInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        resetWifiInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /*
     * 判断当前wifi的加密方式
	 */
    public int getWifiType(Context context, String ssid) {
        int type = -1;
        List<ScanResult> list = mWifiManager.getScanResults();
        for (ScanResult scResult : list) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;// 网络接入的性能,判断加密方式
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        type = 0;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        type = 1;
                    } else {
                        type = 2;
                    }

                }

            }
        }
        return type;
    }

    // Wifi的网络名称
    public String getSSID() {
        resetWifiInfo();
        return mWifiInfo.getSSID();
    }

    // wifi的连接速度Mbps
    public int getLinkSpeed() {
        resetWifiInfo();
        return mWifiInfo.getLinkSpeed();
    }

    /*
     * 1.wifi的信号强弱 2.得到的值是一个0到-100的区间值，是一个int型数据
     * 3.0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线。
     */
    public int getRssi() {
        resetWifiInfo();
        return mWifiInfo.getRssi();
    }

    public String getIpAddress() {
        return ipIntToString(mWifiInfo.getIpAddress());
    }

    // 将int类型的IP转换成字符串形式的IP
    private String ipIntToString(int ip) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        resetWifiInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public String getAllWifiInfo() {
        String wifiSSID = getSSID();
        String wifiMacAddress = getMacAddress();
        int wifiLinkSpeed = getLinkSpeed();
        int wifiRssi = getRssi();
        String wifiIpAddress = getIpAddress();

        String result = "SSID:" + wifiSSID + ",MacAddress:" + wifiMacAddress
                + ",wifiLinkSpeed:" + wifiLinkSpeed + ",wifiRssi:" + wifiRssi
                + ",wifiIpAddress:" + wifiIpAddress;

        return result;
    }

    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        Log.i(TAG,"连接Id：a--" + wcgID);
        Log.i(TAG, "连接情况：b--" + b);
    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        Log.i(TAG, "正在断开wifi disconnectWifi: "+netId);
        mWifiManager.disconnect();
//        mWifiManager.disableNetwork(netId);
        removeConfig(getSSID());
    }

    public void removeConfig(String mySSID){
        WifiConfiguration tempConfig = this.IsExsits(mySSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
            Log.d(TAG, "正在移除config: "+mySSID);
        }
    }

    //然后是一个实际应用方法，只验证过没有密码的情况：

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            Log.i(TAG, "CreateWifiInfo: 正在移除config");
            mWifiManager.removeNetwork(tempConfig.networkId);
//            return config;
//            mWifiManager.saveConfiguration();
        }

        if (Type == 1) //没有密码的情况
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) //WEP加密的情况
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 3) //WPA加密的情况
        {
            config.preSharedKey = "\"" + Password + "\"";
            Log.i(TAG, "CreateWifiInfo: 正在连接创建config  ssid= "+SSID+" password = "+ Password);
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;

        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null)
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"") || existingConfig.SSID.equals(SSID)) {
                    return existingConfig;
                }
            }
        return null;
    }

}
