package com.bmw.M1S1.model;

/**
 * Created by admin on 2016/9/19.
 */
public class Environment {


    private static final String TAG = "debug_M1S1";

    public final static String SHAREPREFERENCES = "ENVIRONMENT";
    public final static String QIYA_MIN = "QIYAMIN";
    public final static String QIYA_MAX = "QIYAMAX";


    private String name;
    private float current_num;
    private float min_num;
    private float max_num;
    private int stat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCurrent_num() {
        return current_num;
    }

    public void setCurrent_num(float current_num) {
        this.current_num = current_num;
    }

    public float getMin_num() {
        return min_num;
    }

    public void setMin_num(float min_num) {
        this.min_num = min_num;
    }

    public float getMax_num() {
        return max_num;
    }

    public void setMax_num(float max_num) {
        this.max_num = max_num;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        return "Environment{" +
                "name='" + name + '\'' +
                ", current_num=" + current_num +
                ", min_num=" + min_num +
                ", max_num=" + max_num +
                ", stat=" + stat +
                '}';
    }
}
