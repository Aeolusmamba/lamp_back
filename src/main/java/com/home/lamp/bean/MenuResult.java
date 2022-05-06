package com.home.lamp.bean;

public class MenuResult extends Result {
    private MenuData data = new MenuData();

    public MenuData getData() {
        return data;
    }

    public void setData(MenuData data) {
        this.data = data;
    }
}
