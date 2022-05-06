package com.home.lamp.bean;

public class Power {
    private String cur_date;
    private Integer openTimes;
    private Long duration;

    public String getCur_date() {
        return cur_date;
    }

    public void setCur_date(String cur_date) {
        this.cur_date = cur_date;
    }

    public Integer getOpenTimes() {
        return openTimes;
    }

    public void setOpenTimes(Integer openTimes) {
        this.openTimes = openTimes;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
