package com.home.lamp.bean;

public class DeviceStatusResult extends Result {

    private DeviceStatusData data = new DeviceStatusData();

    public DeviceStatusData getData() {
        return data;
    }

    public void setData(DeviceStatusData data) {
        this.data = data;
    }
}
