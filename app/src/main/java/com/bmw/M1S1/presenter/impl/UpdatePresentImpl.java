package com.bmw.M1S1.presenter.impl;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.bmw.M1S1.R;
import com.bmw.M1S1.utils.UrlUtil;
import com.bmw.M1S1.view.dialog.UpdateDialog;
import com.bmw.M1S1.model.UpdateInfo;
import com.bmw.M1S1.view.dialog.UpdateProgressDialog;
import com.bmw.M1S1.model.impl.UpdateInfoImpl;
import com.bmw.M1S1.model.model.UpdateMode;
import com.bmw.M1S1.presenter.UpdateInfoListener;
import com.bmw.M1S1.presenter.UpdatePresenter;
import com.bmw.M1S1.view.viewImpl.UpdateViewImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2016/9/21.
 */
public class UpdatePresentImpl implements UpdatePresenter,UpdateInfoListener {

    private UpdateMode updateMode;
    private String version;
    private UpdateViewImpl view;
    private Context context;
    private UpdateProgressDialog proDialog;
    private String url,apkName;

    public UpdatePresentImpl(UpdateViewImpl view,Context context,String version) {
        this.updateMode = new UpdateInfoImpl();
        this.view = view;
        this.context = context;
        this.version = version;

        initWifi();
    }

    private void initWifi() {
        mHandler.sendEmptyMessage(3);

    }


    public void update() {
        updateMode.getUpdateInfo(this);
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int progress = msg.arg1;
                    proDialog.setProgress(progress);
                    break;
                case 2:
                    proDialog.dismiss();
                    view.update(apkName);
                    break;
                case 3:
                    view.showError("正在检查更新！");
                    update();
                    break;
                case 4:
                    view.showError("网络不可用！请联网后重试！");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public void realese() {
    }

    @Override
    public void setUpdateInfo(UpdateInfo updateInfo) {
        if(updateInfo!= null) {
            if (isNeedUpdate(updateInfo)) {
                showUpdateDialog(updateInfo.getVersion(), updateInfo.getDescription());
                url = updateInfo.getUrl();
                apkName = updateInfo.getApkName();
            } else {
                view.showError("已经是最新版本！");
//                wifiPresenter.open();
//                wifiPresenter.connect();
            }
        }else{
            view.showError("网络错误！请检查网络是否连接！");
//            wifiPresenter.connect();
        }
    }

    @Override
    public void error(Exception e) {
        view.showError("网络不可用！请联网后重试！");
    }

    private void showUpdateDialog(String version, String describe){
        final UpdateDialog dialog = new UpdateDialog(context,version,describe);
        dialog.setListening(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.update_cancel:
//                        wifiPresenter.connect();
                        dialog.dismiss();
                        break;
                    case R.id.update_sure:
                        showProgressDialog();
                        dialog.dismiss();
                        break;
                }
            }
        });
    }

    private void showProgressDialog() {
        proDialog = new UpdateProgressDialog(context);
        downloadDate();

    }

    public void downloadFile(){
//        DownLoadFile downLoadFile = new DownLoadFile("temp.jpg","","http://bmob-cdn-7943.b0.upaiyun.com/2016/12/06/5c0d6402c113430dbace112f12ea3b2b.jpg");
        BmobFile bomfile = new BmobFile(apkName,"",url);
        downloadFile(bomfile);
    }

    private void downloadFile(BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
//                toast("开始下载...");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
//                    toast("下载成功,保存路径:"+savePath);
                    mHandler.sendEmptyMessage(2);
                }else{
//                    toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    Log.d("bmob", "done: 下载失败！");
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                msg.arg1 = value;
                mHandler.sendMessage(msg);
            }

        });
    }


    private void downloadDate() {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {


            @Override
        public void onResponse(Call call, Response response) throws IOException {

            InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = UrlUtil.getSDPath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    Log.d("h_bl", "total=" +" "+total);
                    File file = new File(SDPath,
                            apkName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.arg1 = progress;
                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    mHandler.sendEmptyMessage(2);
                    Log.d("h_bl", "文件下载成功");
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("h_bl", "onFailure");
            }
        });
    }




    private boolean isNeedUpdate(UpdateInfo info) {
        String v = info.getVersion(); // 最新版本的版本号
        if (v.equals(version)) {
            return false;
        } else {
            return true;
        }
    }
}
