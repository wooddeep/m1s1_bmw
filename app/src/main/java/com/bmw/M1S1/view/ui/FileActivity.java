package com.bmw.M1S1.view.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bmw.M1S1.R;
import com.bmw.M1S1.view.adapter.FileListAdapter;
import com.bmw.M1S1.presenter.FilePresenter;
import com.bmw.M1S1.presenter.impl.FilePresenterImpl;
import com.bmw.M1S1.view.viewImpl.FileViewImpl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileActivity extends BaseActivity implements FileViewImpl{

    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.picture_recyclerview)
    RecyclerView pRecycler;
    @Bind(R.id.pic_menu)
    LinearLayout picMenu;
    @Bind(R.id.file_btn)
    Button fileWork;
    private FileListAdapter adapter;
    private boolean isFileWork;
    private boolean isPicture;
    private FilePresenter filePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
        isPicture = getIntent().getBooleanExtra("picture",true); //判断图片还是视频文件

        initRecyclerView(); //初始化recyclerview
        filePresenter = new FilePresenterImpl(adapter,isPicture,this); //初始化presenter
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String msg = searchEdit.getText().toString();
                filePresenter.searching(msg);
            }
        });
    }
    private void initRecyclerView() {
        GridLayoutManager gManager = new GridLayoutManager(this, 3);
        pRecycler.setLayoutManager(gManager);
        adapter = new FileListAdapter(this,isPicture);
        pRecycler.setAdapter(adapter);

    }

    @OnClick({R.id.search_btn, R.id.file_btn, R.id.chooseAll, R.id.cancelAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                break;
            case R.id.file_btn:
                filePresenter.delete();
                break;
            case R.id.chooseAll:
                filePresenter.chooseAll();
                break;
            case R.id.cancelAll:
               filePresenter.cancelAll();
                break;
        }
    }
    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void btnChange(String msg) {
        fileWork.setText(msg);
    }

    @Override
    public void isMenuShow(boolean isMenuShow) {
        if(isMenuShow){
            picMenu.setVisibility(View.VISIBLE);
        }else {
            picMenu.setVisibility(View.GONE);
        }
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
    protected void onResume() {
        super.onResume();
        filePresenter.initAdapter();
    }
}
