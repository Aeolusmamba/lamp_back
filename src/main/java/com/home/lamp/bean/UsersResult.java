package com.home.lamp.bean;

public class UsersResult extends Result {
    private UsersData data = new UsersData();

    public UsersData getData() {
        return data;
    }

    public void setData(UsersData data) {
        this.data = data;
    }
}
