package com.home.lamp.bean;

import java.util.ArrayList;

public class StatusData {
    private ArrayList<LampStatus> statusList = new ArrayList<>();
    private Integer total;

    public ArrayList<LampStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(ArrayList<LampStatus> statusList) {
        this.statusList = statusList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
