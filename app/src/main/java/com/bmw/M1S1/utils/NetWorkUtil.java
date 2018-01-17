package com.bmw.M1S1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by admin on 2016/9/21.
 */
public class NetWorkUtil {



    public static int pingHost(String str){

        int resault= -1;
        try {
            // TODO: Hardcoded for now, make it UI configurable
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 3 " +str);
            int status = p.waitFor();
            if (status == 0) {
                //  mTextView.setText("success") ;
                resault= 0;
            }
            else
            {
                resault= 1;
                //  mTextView.setText("fail");
            }
        } catch (IOException e) {
            //  mTextView.setText("Fail: IOException"+"\n");
        } catch (InterruptedException e) {
            //  mTextView.setText("Fail: InterruptedException"+"\n");
        }

        return resault;
    }

    public static int isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return 0;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        LogUtil.log("网络类型：" + i + "===状态===" + networkInfo[i].getState());
                        LogUtil.log("网络类型：" + i + "===类型===" + networkInfo[i].getTypeName());
                        if ("WIFI".equals(networkInfo[i].getTypeName())) {
                            return 2;
                        }else {

                            LogUtil.log("网络类型：" + i + "===验证联网成功===");
                            return 1;
                        }
                    }
                }
            }
        }

        return 0;
    }

    public static boolean wifiIsConnected(Context context)
    {//通过ping百度检测WIFI是否可用
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);//
            int status = p.waitFor();//只有0时表示正常返回。
            return (connectivityManager.getActiveNetworkInfo() != null && status==0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public static String getWifiIp(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
            return null;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public static String getGprsIp(Context context) {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreferenceIpAddress", ex.toString());
        }
        return null;
    }


}
