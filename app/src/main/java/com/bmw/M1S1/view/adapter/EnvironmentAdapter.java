package com.bmw.M1S1.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmw.M1S1.R;
import com.bmw.M1S1.model.Environment;
import com.bmw.M1S1.view.dialog.EnvironmentDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/2.
 */
public class EnvironmentAdapter extends RecyclerView.Adapter<EnvironmentAdapter.ViewHolder> {


    private List<Environment> list;
    private Context context;
    private EnvironmentDialog dialog;
    private AdapterDateChangeListener adapterDateChangeListener;
    private String e_name, e_unit;
    private float min_current, max_current;
    private SharedPreferences sharedPreferences;

    public EnvironmentAdapter(Context context) {
        list = new ArrayList<>();
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Environment.SHAREPREFERENCES, Context.MODE_PRIVATE);
    }

    public void setList(List<Environment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.environment_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Environment environment = list.get(position);
        holder.name.setText(environment.getName());
        holder.min_num.setText(String.valueOf(environment.getMin_num()));
        holder.max_num.setText(String.valueOf(environment.getMax_num()));
        holder.current_num.setText(String.valueOf(environment.getCurrent_num()));
        //根据当前值判断状态
        if (environment.getCurrent_num() < environment.getMin_num()) {
            holder.stat_img.setImageResource(R.drawable.circle_red);
            holder.stat.setText("异常");
            holder.stat.setTextColor(Color.RED);
        } else if (environment.getCurrent_num() > environment.getMax_num()) {
            holder.stat_img.setImageResource(R.drawable.circle_red);
            holder.stat.setText("异常");
            holder.stat.setTextColor(Color.RED);
        } else {
            holder.stat_img.setImageResource(R.drawable.circle_green);
            holder.stat.setText("正常");
            holder.stat.setTextColor(Color.GREEN);
        }
        //点击设置事件
        holder.set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e_name = list.get(position).getName();
                if (e_name.equals("气压")) {
                    min_current = sharedPreferences.getFloat(Environment.QIYA_MIN, -1.0f);
                    max_current = sharedPreferences.getFloat(Environment.QIYA_MAX, -1.0f);
                    e_unit = "PSI";
                }
                dialog = new EnvironmentDialog(context, e_name, e_unit, min_current, max_current); //弹出dialog对话框
                dialog.setDateChangeListener(new EnvironmentDialog.DateChangeListener() {  //监听dialog的确定按钮是否按下
                    @Override
                    public void setDate(String min, String max) {  //传回更改数据
                        if (min.equals("") || max.equals("")) {
                            showError("输入值不能为空！请重新输入！");
                        } else {
                            float min_num = Float.valueOf(min);
                            float max_num = Float.valueOf(max);
                            min_num = (float) (Math.round(min_num * 100)) / 100;
                            max_num = (float) (Math.round(max_num * 100)) / 100;
                            if (min_num < 0 || min_num > max_num) {
                                showError("输入值格式错误！请重新输入！");
                            } else {
                                resetMaxAMin(e_name, min_num, max_num);  //更改最大最小值
                                dialog.dismiss();  //关闭对话框
                                if (adapterDateChangeListener != null)
                                    adapterDateChangeListener.resetDate();  //通知数据更改，刷新视图
                            }
                        }

                    }

                });
            }
        });
    }

    private void showError(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void resetMaxAMin(String name, float min_num, float max_num) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Environment.QIYA_MIN, min_num);
        editor.putFloat(Environment.QIYA_MAX, max_num);
        editor.commit();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView stat_img;
        private TextView name;
        private TextView current_num;
        private TextView min_num;
        private TextView max_num;
        private TextView stat;
        private TextView set;


        public ViewHolder(View itemView) {
            super(itemView);
            stat_img = (ImageView) itemView.findViewById(R.id.stat_img);
            name = (TextView) itemView.findViewById(R.id.name);
            current_num = (TextView) itemView.findViewById(R.id.current_num);
            min_num = (TextView) itemView.findViewById(R.id.min_num);
            max_num = (TextView) itemView.findViewById(R.id.max_num);
            stat = (TextView) itemView.findViewById(R.id.stat);
            set = (TextView) itemView.findViewById(R.id.item_set);
        }
    }

    public void setAdapterDateChangeListener(AdapterDateChangeListener adapterDateChangeListener) {
        this.adapterDateChangeListener = adapterDateChangeListener;
    }

    //接口监听，最大最小值发生变化时通知activity
    public interface AdapterDateChangeListener {
        void resetDate();
    }


}
