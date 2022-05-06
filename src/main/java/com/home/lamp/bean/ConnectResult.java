package com.home.lamp.bean;

public class ConnectResult extends Result {
    ConnectData data = new ConnectData();

    public ConnectData getData() {
        return data;
    }

    public void setData(ConnectData data) {
        this.data = data;
    }
}
