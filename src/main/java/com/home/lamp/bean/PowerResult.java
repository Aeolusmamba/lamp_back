package com.home.lamp.bean;

public class PowerResult extends Result{
    private PowerData data = new PowerData();

    public PowerData getData() {
        return data;
    }

    public void setData(PowerData data) {
        this.data = data;
    }
}
