package com.home.lamp.bean;

import java.util.ArrayList;

public class DeviceStatusData {
    private ArrayList<ArrayList<Chart>> reportList = new ArrayList<>();
    private Integer total;

    public ArrayList<ArrayList<Chart>> getReportList() {
        return reportList;
    }

    public void setReportList(ArrayList<ArrayList<Chart>> reportList) {
        this.reportList = reportList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
