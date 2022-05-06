package com.home.lamp.bean;

import java.util.ArrayList;

public class PowerData {
    private ArrayList<Chart> powerList = new ArrayList<>();
    private Integer total;

    public ArrayList<Chart> getPowerList() {
        return powerList;
    }

    public void setPowerList(ArrayList<Chart> powerList) {
        this.powerList = powerList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
