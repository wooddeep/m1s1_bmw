package com.bmw.M1S1.model.impl;


import android.util.Log;

import com.bmw.M1S1.BaseApplication;
import com.bmw.M1S1.model.UpdateInfo;
import com.bmw.M1S1.model.model.UpdateMode;
import com.bmw.M1S1.presenter.UpdateInfoListener;
import com.bmw.M1S1.utils.LogUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by admin on 2016/9/21.
 */
public class UpdateInfoImpl implements UpdateMode {

    @Override
    public void getUpdateInfo(UpdateInfoListener listener) {
//        String path = Login_info.url + "/update.txt";
//        new MyAsyncTask(listener).execute(path);
        getData(listener);
    }

    public void getData(final UpdateInfoListener listener) {

        BmobQuery query = new BmobQuery("UpdateInfo");
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    if(ary != null)
                    try {
                        JSONObject json = ary.getJSONObject(0);
                        if(json != null) {
                            Gson g = new Gson();
                            UpdateInfo u = g.fromJson(json.toString(), UpdateInfo.class);
                            Log.i("update", "done: 更新信息数据下载完成");
                            listener.setUpdateInfo(u);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        Log.i("update 1",e1.toString());
                    }

                } else {
                    LogUtil.log("程序更新："+e);
                    listener.error(e);
                }
            }
        });
    }
/*
    class MyAsyncTask extends AsyncTask<String,Void,UpdateInfo>{

        private UpdateInfoListener listener;

        public MyAsyncTask(UpdateInfoListener listener) {
            this.listener = listener;
        }

        @Override
        protected UpdateInfo doInBackground(String... strings) {
            StringBuffer sb = new StringBuffer();
            String line = null;
            BufferedReader reader = null;
            try {
                // 创建一个url对象
                URL url = new URL(strings[0]);
                // 通過url对象，创建一个HttpURLConnection对象（连接）
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                // 通过HttpURLConnection对象，得到InputStream
                reader = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                // 使用io流读取文件
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String info = sb.toString();
            if(info.contains("&")) {
                UpdateInfo updateInfo = new UpdateInfo();
                updateInfo.setVersion(info.split("&")[1]);
                updateInfo.setDescription(info.split("&")[2]);
                updateInfo.setUrl(info.split("&")[3]);
                updateInfo.setApkName(info.split("&")[4]);
                Log.d("updateInfoImpl", "doInBackground: "+updateInfo.toString());
                return updateInfo;
            }else{
                Log.d("updateInfoImpl", "doInBackground: null");
                return null;
            }
        }

        @Override
        protected void onPostExecute(UpdateInfo updateInfo) {
            if(listener != null)
                listener.setUpdateInfo(updateInfo);
            super.onPostExecute(updateInfo);
        }
    }*/
}
