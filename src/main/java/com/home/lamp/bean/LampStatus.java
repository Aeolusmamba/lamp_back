package com.home.lamp.bean;

public class LampStatus {
    private Integer ledState;
    private Integer light;
    private Integer humidity;
    private Integer temperature;
    private String updateTime;

    public Integer getLedState() {
        return ledState;
    }

    public void setLedState(Integer ledState) {
        this.ledState = ledState;
    }

    public Integer getLight() {
        return light;
    }

    public void setLight(Integer light) {
        this.light = light;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
