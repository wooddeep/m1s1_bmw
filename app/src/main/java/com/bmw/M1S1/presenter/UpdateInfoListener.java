package com.bmw.M1S1.presenter;

import com.bmw.M1S1.model.UpdateInfo;

/**
 * Created by admin on 2016/9/21.
 */
public interface UpdateInfoListener {
    void setUpdateInfo(UpdateInfo updateInfo);
    void error(Exception e);
}
