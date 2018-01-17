package com.bmw.M1S1.presenter.impl;

import com.bmw.M1S1.utils.UrlUtil;
import com.bmw.M1S1.view.adapter.FileListAdapter;
import com.bmw.M1S1.presenter.FilePresenter;
import com.bmw.M1S1.model.Login_info;
import com.bmw.M1S1.view.viewImpl.FileViewImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/8.
 */
public class FilePresenterImpl implements FilePresenter {

    private FileListAdapter adapter;
    private boolean isPicture;
    private FileViewImpl view;
    private boolean isFileWork;

    public FilePresenterImpl(FileListAdapter adapter, boolean isPicture, FileViewImpl view) {
        this.isPicture = isPicture;
        this.adapter = adapter;
        this.view = view;
        initAdapter();
    }


    private List<File> initData() { //从指定文件夹获取文件列表

        List<File> files = null;
        if (isPicture)
            files = UrlUtil.getFileUtils(UrlUtil.getSDPath() + Login_info.local_picture_path);
        else
            files = UrlUtil.getFileUtils(UrlUtil.getSDPath() + Login_info.local_video_path);
        return files;

    }

    @Override
    public void initAdapter() {
        adapter.setFiles(initData());
        adapter.setOnItemLongClickListener(new FileListAdapter.OnItemLongClickListener() { //长按进入文件管理模式
            @Override
            public void longClick(int position) {
                delete();
                adapter.setList(position);
            }
        });
    }

    @Override
    public void searching(String msg) {
        if (msg.equals("")) {
//            view.showToast("请先输入搜索内容！");
            adapter.setFiles(initData());
            return;
        }
        List<File> searFiles = new ArrayList<>();
        if (initData() != null)
            for (File f : initData()) {
                String name = f.getName();
                if (name.contains(msg)) {
                    searFiles.add(f);
                }
            }
        if (searFiles.size() != 0) {
            adapter.setFiles(searFiles);
            view.showToast("搜索到" + searFiles.size() + "个文件");
        } else{
            adapter.setFiles(searFiles);
        }
//            view.showToast("未搜索到相关文件！");
    }

    @Override
    public void delete() {
        if (isFileWork) {
            if (adapter.getList() != 0) {
                int n = adapter.deleteFile();
                adapter.setFiles(initData());
                view.showToast("成功删除" + n + "个文件");
            }
            isFileWork = false;
            view.isMenuShow(false);
            adapter.setChoose(false);
            view.btnChange("文件管理");
        } else {
            isFileWork = true;
            adapter.setChoose(true);
            view.btnChange("删除");
            view.isMenuShow(true);
        }
    }

    @Override
    public void chooseAll() {
        adapter.chooseAll();
    }

    @Override
    public void cancelAll() {
        if (adapter.getList() != 0)
            adapter.cancelAll();
        else {
            isFileWork = false;
            view.isMenuShow(false);
            adapter.setChoose(false);
            view.btnChange("文件管理");
        }
    }

}
