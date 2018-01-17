package com.bmw.M1S1.view.adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmw.M1S1.R;
import com.bmw.M1S1.view.ui.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/2.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private List<File> files;
    private Context context;
    private boolean isPicture;
    private OnItemLongClickListener listener;
    private static final String TAG = "FileListAdapter";

    public void setChoose(boolean choose) {
        this.isChoose = choose;
        notifyDataSetChanged();
    }

    private boolean isChoose;
    private List<Integer> list;  //记录选择

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public FileListAdapter(Context context, boolean isPicture) {
        this.context = context;
        this.isPicture = isPicture;
        list = new ArrayList<>();
        files = new ArrayList<>();
    }

    public void setFiles(List<File> files) {
        if (files != null) {
            this.files = files;
            notifyDataSetChanged();
        } else {
            this.files = null;
            this.files = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, null);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.img.setTag(files.get(position).getName());
        if (isChoose) {//进入文件管理模式
            holder.img.setClickable(false);
            holder.bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list.contains(position)) {
                        list.remove(list.indexOf(position));
                        notifyDataSetChanged();
                    } else {
                        list.add(position);
                        notifyDataSetChanged();
                    }
                }
            });

        }
        if (list != null && list.contains(position)) {
            Log.d(TAG, "onBindViewHolder: " + position);
            holder.bg.setBackgroundColor(context.getResources().getColor(R.color.btn_press));
        } else {
            holder.bg.setBackgroundColor(context.getResources().getColor(R.color.background));
            Drawable mDrawableDefault = context.getResources().getDrawable(R.color.background);
            Drawable mDrawablePressed = context.getResources().getDrawable(R.color.btn_press);
            StateListDrawable drawable = new StateListDrawable();
            //按下状态
            drawable.addState(new int[]{android.R.attr.state_pressed}, mDrawablePressed);
            //普通状态
            drawable.addState(new int[]{-android.R.attr.state_focused, -android.R.attr.state_selected,
                    -android.R.attr.state_pressed}, mDrawableDefault);
            holder.bg.setBackgroundDrawable(drawable);
        }
        if (isPicture) {

            holder.img.setImageResource(R.mipmap.picfile);
            holder.text.setText(files.get(position).getName());
        } else {
            holder.img.setImageResource(R.mipmap.video);
            holder.text.setText(files.get(position).getName());
        }

        if (!isChoose) {

            if (isPicture) {
                holder.bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: ");
//                        Intent intent = new Intent(context, PicShowActivity.class);
//                        intent.putExtra("bitmap",files.get(position).getAbsolutePath());
//                        context.startActivity(intent);

                        //打开指定的一张照片
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(files.get(position)), "image/*");
                        context.startActivity(intent);


                    }
                });
            } else {

                holder.bg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.d(TAG, "onLongClick: ");
                        if(!isChoose) {
                            Uri uri = Uri.parse("file://" + files.get(position).getAbsolutePath());
//
                            //调用系统自带的播放器
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "video/mp4");
                            context.startActivity(intent);
                        }
                        return true;
                    }
                });
                holder.bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = null;
                        intent = new Intent(context, PlayerActivity.class);
                        intent.putExtra("path", files.get(position).getAbsolutePath());
                        context.startActivity(intent);

                    }
                });
            }
//            holder.bg.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    Log.d(TAG, "onLongClick: ");
//                    if (!isChoose)
//                        listener.longClick(position);
//                    return true;
//                }
//            });
        }
    }


    public void chooseAll() {
        for (int i = 0; i < files.size(); i++) {
            if (!list.contains(i)) {
                System.out.println("adapter:choose = " + i);
                list.add(i);
                System.out.println("adapter:choose = " + list.toString() + " " + list.size());
            }
        }
        notifyDataSetChanged();
    }

    public void cancelAll() {
        for (int i = 0; i < files.size(); i++) {

            if (list.contains(i)) {
                list.remove(list.indexOf(i));

            }
        }
        notifyDataSetChanged();
    }

    public int deleteFile() {
        Log.d(TAG, "deleteFile1: " + list.toString());
        int n = list.size();
        for (Integer i : list) {
            files.get(i).delete();
        }
        cancelAll();
        Log.d(TAG, "deleteFile: " + list.toString());

        return n;
    }

    public int getList() {
        return list.size();
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView text;
        private LinearLayout bg;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_img);
            text = (TextView) itemView.findViewById(R.id.item_text);
            bg = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }

    public interface OnItemLongClickListener {
        void longClick(int position);
    }

    public void setList(int i) {
        list.add(i);
    }

}
